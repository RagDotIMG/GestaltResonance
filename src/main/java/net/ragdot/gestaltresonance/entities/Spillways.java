package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;

public class Spillways extends GestaltBase {

    public Spillways(EntityType<? extends Spillways> type, World world) {
        super(type, world);
    }

    @Override
    public net.minecraft.util.Identifier getGestaltId() {
        return net.minecraft.util.Identifier.of("gestaltresonance", "spillways");
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return GestaltBase.createBaseStandAttributes();
    }

    @Override
    protected boolean canMeleeAttack() { return false; }

    @Override
    protected double getMaxChaseRange() { return 1.0; }

    @Override
    protected double getAttackReach() { return 8.0; }

    @Override
    protected float getAttackDamage() { return 6.0f; }

    @Override
    protected int getAttackCooldownTicks() { return 40; }

    @Override
    protected float getDamageReductionFactor() { return 0.0f; }

    @Override
    protected void applyOwnerPassiveBuffs(PlayerEntity owner) {
        // 1) Swim faster: refresh Dolphin's Grace while active
        if (!owner.getWorld().isClient) {
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 15, 0, true, false, true));
        }

        // 2) Breathe twice as long underwater: when submerged and actually consuming air,
        //    restore 1 air every other tick so net loss is halved.
        //    Skip if they already have Water Breathing or Conduit Power (which already prevent air loss).
        boolean hasFullBreathEffect = owner.hasStatusEffect(StatusEffects.WATER_BREATHING) || owner.hasStatusEffect(StatusEffects.CONDUIT_POWER);
        if (!hasFullBreathEffect && (owner.isSubmergedInWater() || owner.isTouchingWater())) {
            int currentAir = owner.getAir();
            int maxAir = owner.getMaxAir();
            if (currentAir > 0 && currentAir < maxAir) {
                long time = owner.getWorld().getTime();
                if ((time & 1L) == 0L) { // every other tick
                    owner.setAir(Math.min(maxAir, currentAir + 1));
                }
            }
        }
    }

    // Ability 1 (Lachryma): Create a block of water at the spot the player is looking
    public void lachryma(ServerPlayerEntity player) {
        if (player == null || player.getServerWorld() == null) return;

        double range = 6.5; // slight increase for shallow angles
        // Include fluids in raycast so we can directly target water source blocks
        BlockHitResult hit = (BlockHitResult) player.raycast(range, 1.0f, true);

        // Must hit a block; no mid-air behavior
        if (hit.getType() != HitResult.Type.BLOCK) return;

        // First: attempt to TAKE water with absolute priority over creation.
        // Compute common positions/states
        BlockPos posAtHit = BlockPos.ofFloored(hit.getPos());
        BlockPos hitPos = hit.getBlockPos();
        BlockState hitState = player.getWorld().getBlockState(hitPos);
        boolean hitIsAir = player.getWorld().isAir(hitPos);
        boolean hitIsSolid = !hitIsAir && !player.getWorld().getBlockState(hitPos).getCollisionShape(player.getWorld(), hitPos).isEmpty();

        // Case A: Waterlogged block directly hit → clear waterlogged first
        if (hitState.contains(Properties.WATERLOGGED) && Boolean.TRUE.equals(hitState.get(Properties.WATERLOGGED))) {
            BlockState cleared = hitState.with(Properties.WATERLOGGED, false);
            player.getWorld().setBlockState(hitPos, cleared);
            float newStamina = Math.min(getMaxStamina(), getStamina() + 10f);
            this.setStamina(newStamina);
            return;
        }

        // Compute basic ray data
        Vec3d eye = player.getEyePos();
        Vec3d toHit = hit.getPos().subtract(eye);
        double dist = toHit.length();
        if (dist <= 0.0001) return;
        Vec3d dir = toHit.normalize();

        // Robust sampling around the exact hit point so taking works from any face (top/sides/bottom)
        // Prioritize SOURCE water at/near the cursor. Only source water is taken (flowing is ignored for take).
        Vec3d hitVec = hit.getPos();
        BlockPos posAt = BlockPos.ofFloored(hitVec);
        BlockPos posBehind = BlockPos.ofFloored(hitVec.subtract(dir.multiply(0.05)));
        BlockPos posAhead = BlockPos.ofFloored(hitVec.add(dir.multiply(0.05)));

        // Check these three positions for SOURCE water, in a stable order.
        BlockPos[] nearSamples = new BlockPos[] { posAt, posBehind, posAhead };
        for (BlockPos samplePos : nearSamples) {
            var fs = player.getWorld().getFluidState(samplePos);
            if (fs.isOf(Fluids.WATER) && fs.isStill()) {
                player.getWorld().setBlockState(samplePos, Blocks.AIR.getDefaultState());
                float newStamina = Math.min(getMaxStamina(), getStamina() + 10f);
                this.setStamina(newStamina);
                return;
            }
        }

        // Additional face-adjacent check: if we are aiming at the underside/top/side of a neighboring block
        // (e.g., pointing at the block UNDER a floating water source), inspect the block on the hit face.
        // If that adjacent block is a source water block, take it as well. This fixes bottom-face pickup.
        BlockPos facePos = hitPos.offset(hit.getSide());
        var faceFs = player.getWorld().getFluidState(facePos);
        if (faceFs.isOf(Fluids.WATER) && faceFs.isStill()) {
            player.getWorld().setBlockState(facePos, Blocks.AIR.getDefaultState());
            float newStamina = Math.min(getMaxStamina(), getStamina() + 10f);
            this.setStamina(newStamina);
            return;
        }

        // Else: try to CREATE water on the adjacent face, requires stamina based on level (see below).
        // Gate 1: block creation only if a SOURCE water exists along the line of sight (including at/near hit).
        //         Flowing water should NOT block creation.
        // Quick near-hit source check first (reuse samples):
        for (BlockPos samplePos : nearSamples) {
            var fs = player.getWorld().getFluidState(samplePos);
            if (fs.isOf(Fluids.WATER) && fs.isStill()) {
                return; // a source is at/near hit → do not create
            }
        }
        // Also consider the block we are placing against (face-adjacent) for source presence
        if (faceFs.isOf(Fluids.WATER) && faceFs.isStill()) {
            return;
        }
        // Ray-march from eyes to hit to detect any source water on the path; block creation if found.
        boolean sourceOnPath = false;
        {
            double stepLen = 0.25;
            int steps = (int) Math.ceil(dist / stepLen);
            Vec3d step = dir.multiply(stepLen);
            Vec3d p = eye;
            for (int i = 1; i <= steps; i++) {
                p = p.add(step);
                BlockPos sp = BlockPos.ofFloored(p);
                var fs = player.getWorld().getFluidState(sp);
                if (fs.isOf(Fluids.WATER) && fs.isStill()) {
                    sourceOnPath = true;
                    break;
                }
            }
        }
        if (sourceOnPath) {
            return; // do not create through a path that crosses any source block
        }

        // Gate 2: ensure the directly hit block is solid (not air and with non-empty collision shape),
        // and is not itself a fluid block. This ensures we only create when targeting a solid surface.
        if (!player.getWorld().getFluidState(hitPos).isEmpty()) {
            return; // the hit block is a fluid block → do not create
        }
        if (!hitIsSolid) {
            return; // only create when looking at a solid block
        }

        // Dynamic stamina cost: base 20, -2 per level, min 10
        int lvl = Math.max(1, this.getLvl());
        int createCostInt = Math.max(10, 20 - ((lvl - 1) * 2));
        float createCost = (float) createCostInt;

        if (this.getStamina() < createCost) return; // fail silently if not enough

        // Place on the face the player hit
        BlockPos targetPos = hitPos.offset(hit.getSide());

        if (!player.getWorld().isValid(targetPos)) return;

        BlockState current = player.getWorld().getBlockState(targetPos);
        // If the target block supports waterlogging, set it true (vanilla-like placement).
        if (current.contains(Properties.WATERLOGGED)) {
            Boolean wl = current.get(Properties.WATERLOGGED);
            if (!Boolean.TRUE.equals(wl)) {
                player.getWorld().setBlockState(targetPos, current.with(Properties.WATERLOGGED, true));
                // Drain stamina on successful placement (level-scaled cost)
                this.setStamina(this.getStamina() - createCost);
                // Reward Gestalt experience for successful use
                this.setExp(this.getExp() + 2);
                return;
            } else {
                return; // already waterlogged → no action
            }
        }

        // Otherwise, place a source water block if the space is replaceable. If the space currently holds
        // flowing water, convert it into a source (allowed). If it's already a source, do nothing.
        boolean canReplace = current.isAir() || current.getCollisionShape(player.getWorld(), targetPos).isEmpty();
        if (!canReplace) return;
        var targetFs = player.getWorld().getFluidState(targetPos);
        if (targetFs.isOf(Fluids.WATER)) {
            if (targetFs.isStill()) {
                return; // already a source here → nothing to do
            } else {
                // flowing water → convert to source
                player.getWorld().setBlockState(targetPos, Blocks.WATER.getDefaultState());
            }
        } else if (targetFs.isEmpty()) {
            player.getWorld().setBlockState(targetPos, Blocks.WATER.getDefaultState());
        } else {
            return; // some non-water fluid present → abort
        }

        // Drain stamina on successful placement (level-scaled cost)
        this.setStamina(this.getStamina() - createCost);

        // Reward Gestalt experience for successful use
        this.setExp(this.getExp() + 2);
    }
}
