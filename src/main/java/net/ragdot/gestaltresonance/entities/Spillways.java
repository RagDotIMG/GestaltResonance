package net.ragdot.gestaltresonance.entities;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

public class Spillways extends GestaltBase {

    // Cooldown for Power 1 (Lachryma)
    private int lachrymaCooldown = 0;

    private int tearsForFearsCooldown = 0;
    private int tearsForFearsMaxCooldown = 0;

    public Spillways(EntityType<? extends Spillways> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (lachrymaCooldown > 0) {
                lachrymaCooldown--;
                this.setPowerCooldown(0, lachrymaCooldown, 15);
            }
            if (tearsForFearsCooldown > 0) {
                tearsForFearsCooldown--;
                this.setPowerCooldown(1, tearsForFearsCooldown, tearsForFearsMaxCooldown);
            }
        }
    }

    @Override
    public void setOwner(PlayerEntity owner) {
        super.setOwner(owner);
        if (owner != null && !this.getWorld().isClient) {
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            this.lachrymaCooldown = gp.gestaltresonance$getGestaltPowerCooldownRemaining(getGestaltId(), 0);
            this.tearsForFearsCooldown = gp.gestaltresonance$getGestaltPowerCooldownRemaining(getGestaltId(), 1);
            this.tearsForFearsMaxCooldown = gp.gestaltresonance$getGestaltPowerCooldownMax(getGestaltId(), 1);
        }
    }

    @Override
    public int getPowerCount() {
        return 1;
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

        // Respect cooldown
        if (this.lachrymaCooldown > 0) return;

        double range = 6.5;

        // Dual-raycast like vanilla BucketItem:
        // Raycast A (take): SOURCE_ONLY to detect a source fluid hit.
        BlockHitResult takeHit = player.getWorld().raycast(new net.minecraft.world.RaycastContext(
                player.getEyePos(),
                player.getEyePos().add(player.getRotationVec(1.0F).multiply(range)),
                net.minecraft.world.RaycastContext.ShapeType.OUTLINE,
                net.minecraft.world.RaycastContext.FluidHandling.SOURCE_ONLY,
                player
        ));

        // Raycast B (create): NONE to ignore fluids and target the solid face behind any flowing water.
        BlockHitResult createHit = player.getWorld().raycast(new net.minecraft.world.RaycastContext(
                player.getEyePos(),
                player.getEyePos().add(player.getRotationVec(1.0F).multiply(range)),
                net.minecraft.world.RaycastContext.ShapeType.OUTLINE,
                net.minecraft.world.RaycastContext.FluidHandling.NONE,
                player
        ));

        // Priority 1: TAKE water if Raycast A hit a source
        if (takeHit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = takeHit.getBlockPos();
            BlockState state = player.getWorld().getBlockState(pos);
            if (state.getFluidState().isStill() && state.getFluidState().isOf(Fluids.WATER)) {
                // It's a source block. Replace with air.
                player.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());

                // Vanilla-like sound and event
                player.getWorld().playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_FILL, net.minecraft.sound.SoundCategory.BLOCKS, 1.0F, 1.0F);
                player.getWorld().emitGameEvent(player, net.minecraft.world.event.GameEvent.FLUID_PICKUP, pos);

                float newStamina = Math.min(getMaxStamina(), getStamina() + 10f);
                this.setStamina(newStamina);
                this.lachrymaCooldown = 15;
                this.setPowerCooldown(0, this.lachrymaCooldown, 15);
                return;
            }
        }

        // Priority 2: Clear WATERLOGGED if Raycast B hit a waterlogged block
        if (createHit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = createHit.getBlockPos();
            BlockState state = player.getWorld().getBlockState(pos);
            if (state.contains(Properties.WATERLOGGED) && Boolean.TRUE.equals(state.get(Properties.WATERLOGGED))) {
                player.getWorld().setBlockState(pos, state.with(Properties.WATERLOGGED, false));

                // Sound and event
                player.getWorld().playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_FILL, net.minecraft.sound.SoundCategory.BLOCKS, 1.0F, 1.0F);
                player.getWorld().emitGameEvent(player, net.minecraft.world.event.GameEvent.FLUID_PICKUP, pos);

                float newStamina = Math.min(getMaxStamina(), getStamina() + 10f);
                this.setStamina(newStamina);
                this.lachrymaCooldown = 15;
                this.setPowerCooldown(0, this.lachrymaCooldown, 15);
                return;
            }
        }

        // Priority 3: CREATE water using Raycast B (solid only)
        if (createHit.getType() == HitResult.Type.BLOCK) {
            BlockPos hitPos = createHit.getBlockPos();
            Direction side = createHit.getSide();
            BlockPos targetPos = hitPos.offset(side);

            // Dynamic stamina cost: base 20, -2 per level, min 10
            int lvl = Math.max(1, this.getLvl());
            int createCostInt = Math.max(10, 20 - ((lvl - 1) * 2));
            float createCost = (float) createCostInt;

            if (this.getStamina() < createCost) return;

            BlockState targetState = player.getWorld().getBlockState(targetPos);

            // 1. Waterlogging
            if (targetState.contains(Properties.WATERLOGGED) && !targetState.get(Properties.WATERLOGGED)) {
                player.getWorld().setBlockState(targetPos, targetState.with(Properties.WATERLOGGED, true));
                completeCreation(player, targetPos, createCost);
                return;
            }

            // 2. Regular placement (replaces air/flowing water)
            boolean isReplaceable = targetState.isAir() || targetState.getCollisionShape(player.getWorld(), targetPos).isEmpty();
            if (isReplaceable) {
                // If it's a non-water fluid, abort (vanilla-like)
                if (!targetState.getFluidState().isEmpty() && !targetState.getFluidState().isOf(Fluids.WATER)) {
                    return;
                }
                // If it's already a water source, abort
                if (targetState.getFluidState().isStill() && targetState.getFluidState().isOf(Fluids.WATER)) {
                    return;
                }

                player.getWorld().setBlockState(targetPos, Blocks.WATER.getDefaultState());
                completeCreation(player, targetPos, createCost);
            }
        }
    }

    public void tearsForFears(ServerPlayerEntity player) {
        if (player == null || player.getServerWorld() == null) return;
        if (this.getLvl() < 2 && !this.getGestaltId().getPath().contains("_ii")) return; // Only for Tier 2+ or level >= 2? Wait, the issue says "once Spillways get's teir 2 and is usable for all it's tiers upwards from that"
        // Actually, GestaltTiers handle entity swapping. So if this is Spillways (Tier 1), it shouldn't have it unless it's level 2? 
        // No, "unlocks once Spillways get's teir 2". That means Spillways (Tier 1) NEVER has it. 
        // SpillwaysII and SpillwaysIII will have it.
        // But Spillways class is the base for SpillwaysII.
        
        if (this.getPowerCount() < 2) return; 

        if (this.tearsForFearsCooldown > 0) return;
        if (this.getStamina() < 6.0f) return;

        this.setStamina(this.getStamina() - 6.0f);

        TearsForFearsEntity bubble = new TearsForFearsEntity(Gestaltresonance.TEARS_FOR_FEARS, player.getWorld());
        bubble.setOwner(player);
        bubble.setSpillwayLevel(this.getLvl());
        
        // Spawn on player with slight right offset in about one block height
        Vec3d right = player.getRotationVec(1.0f).rotateY(-1.5708f).multiply(0.5); // 90 degrees right
        Vec3d spawnPos = player.getPos().add(right).add(0, 1.0, 0);
        
        bubble.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, player.getYaw(), player.getPitch());
        player.getWorld().spawnEntity(bubble);

        this.tearsForFearsMaxCooldown = 20; // 1 second cooldown? Or maybe more? Issue didn't specify.
        this.tearsForFearsCooldown = this.tearsForFearsMaxCooldown;
        this.setPowerCooldown(1, this.tearsForFearsCooldown, this.tearsForFearsMaxCooldown);
        
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sound.SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, net.minecraft.sound.SoundCategory.PLAYERS, 0.5f, 1.0f);
    }

    private void completeCreation(ServerPlayerEntity player, BlockPos pos, float cost) {
        player.getWorld().playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_EMPTY, net.minecraft.sound.SoundCategory.BLOCKS, 1.0F, 1.0F);
        player.getWorld().emitGameEvent(player, net.minecraft.world.event.GameEvent.FLUID_PLACE, pos);

        this.setStamina(this.getStamina() - cost);
        this.setExp(this.getExp() + 2);
        this.lachrymaCooldown = 15;
        this.setPowerCooldown(0, this.lachrymaCooldown, 15);
    }
}
