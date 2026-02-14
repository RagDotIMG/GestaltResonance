package net.ragdot.gestaltresonance.entities;


import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import net.minecraft.server.network.ServerPlayerEntity;


public class AmenBreak extends GestaltBase {

    // Short cooldown for Jungle Bomber ability (ticks)
    private int jungleBomberCooldown = 0;


    public AmenBreak(EntityType<? extends AmenBreak> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        // Server-side cooldown decrement
        if (!this.getWorld().isClient) {
            if (jungleBomberCooldown > 0) {
                jungleBomberCooldown--;
                // Sync to client HUD
                this.setPowerCooldown(0, jungleBomberCooldown, 15);
            }
        }
    }

    @Override
    public void setOwner(PlayerEntity owner) {
        super.setOwner(owner);
        if (owner != null && !this.getWorld().isClient) {
            // Restore persistent cooldowns when re-summoned
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            this.jungleBomberCooldown = gp.gestaltresonance$getGestaltPowerCooldownRemaining(getGestaltId(), 0);
            this.loadPowerCooldownsFromOwner(owner);
        }
    }

    @Override
    public int getPowerCount() {
        // Tier I Amen Break only has its first power.
        return 1;
    }

    @Override
    protected double getHeightOffset() {
        return 0.4;
    }

    @Override
    protected double getFollowBackOffset() {
        return -0.5;
    }

    @Override
    protected double getFollowSideOffset() {
        return 0.1;
    }

    @Override
    public net.minecraft.util.Identifier getGestaltId() {
        return net.minecraft.util.Identifier.of("gestaltresonance", "amen_break");
    }

    // Attributes specific to this stand (can override base)
    public static DefaultAttributeContainer.Builder createAttributes() {
        return GestaltBase.createBaseStandAttributes();
    }


    @Override
    protected double getMaxChaseRange() { return 1.0; }

    @Override
    protected double getAttackReach() { return 8.0; }

    @Override
    protected float getAttackDamage() { return 8.0f; }

    // Use default attack cooldown from base

    @Override
    protected float getDamageReductionFactor() {
        return 0.0f;
    }

    // === Passive: Muffle owner's movement so they are not detected by Warden/Sculk sensors ===
    @Override
    protected void applyOwnerPassiveBuffs(PlayerEntity owner) {
        if (owner == null || owner.getWorld().isClient) return;
        // Activate muffled movement flag; sound/vibration suppression is handled by mixins
        ((IGestaltPlayer) owner).gestaltresonance$setMuffledMovementActive(true);
    }

    @Override
    protected void clearOwnerPassiveBuffs(PlayerEntity owner) {
        if (owner == null) owner = this.getOwner();
        if (owner == null || owner.getWorld().isClient) return;
        ((IGestaltPlayer) owner).gestaltresonance$setMuffledMovementActive(false);
    }

    // === Ability 1: Jungle Bomber ===
    // Power 1: Shoot a Pop Bud projectile (snowball-like)
    public void jungleBomber(ServerPlayerEntity player) {
        if (player == null || player.getServerWorld() == null) return;
        // Enforce 30-tick cooldown and stamina requirement (>= 4)
        if (this.jungleBomberCooldown > 0) return;

        IGestaltPlayer gp = (IGestaltPlayer) player;
        boolean breakCoreActive = gp.gestaltresonance$getBreakCoreTicks() > 0;

        if (!breakCoreActive && this.getStamina() < 4.0f) return;
        var world = player.getServerWorld();

        if (breakCoreActive) {
            // Marking entities on touch instead of firing pop buds
            // Since we can't "touch" them traditionally during Break Core if we block interactions,
            // we should probably do a raycast or check for entities in front.
            // But the description says "marking entities on touch". 
            // Usually "Jungle Bomber" is a projectile.
            // If it's "on touch", maybe it means when the player walks into them?
            // "while break core is active, jungle bomber changes it's ability effect to marking entities on touch instead of fireing pop buds"
            // This suggests that triggering the ability marks entities nearby or something.
            // Or maybe it's still a "use" action that marks what you are looking at.
            
            // Let's implement it as a close-range mark for now if they are "touching" (very close).
            List<net.minecraft.entity.LivingEntity> targets = world.getEntitiesByClass(
                    net.minecraft.entity.LivingEntity.class,
                    player.getBoundingBox().expand(2.0),
                    e -> e != player && e.isAlive() && !(e instanceof GestaltBase)
            );
            for (net.minecraft.entity.LivingEntity target : targets) {
                if (this.getStamina() < 6.0f) break; // Not enough stamina for more marks
                if (markEntityForBreakCore(target)) {
                    this.setStamina(Math.max(0.0f, this.getStamina() - 6.0f));
                }
            }
            
            // Still uses some stamina? Or 0? Break Core itself uses 0.
            // Let's say marking is free during Break Core.
        } else {
            // Spawn projectile from near the player's right hand, slightly up and to the right
            net.ragdot.gestaltresonance.projectile.PopBud bud = new net.ragdot.gestaltresonance.projectile.PopBud(
                    net.ragdot.gestaltresonance.Gestaltresonance.POP_BUD,
                    world
            );
            bud.setOwner(player);
            // Compute spawn offset based on player yaw
            float yaw = player.getYaw();
            double rad = Math.toRadians(yaw);
            // Right vector on XZ plane
            double rightX =  Math.cos(rad);
            double rightZ =  Math.sin(rad);
            // Forward vector on XZ plane
            double fwdX = -Math.sin(rad);
            double fwdZ =  Math.cos(rad);

            double forwardOffset = 0.5; // slightly in front
            double sideOffset = -0.50;   // to the right
            double upOffset = 0.15;     // slightly up

            double spawnX = player.getX() + forwardOffset * fwdX + sideOffset * rightX;
            double spawnY = player.getEyeY() - 0.3 + upOffset; // close to hand height
            double spawnZ = player.getZ() + forwardOffset * fwdZ + sideOffset * rightZ;

            bud.refreshPositionAndAngles(spawnX, spawnY, spawnZ, player.getYaw(), player.getPitch());
            // Slightly slower than snowball default
            bud.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 1.25f, 1.0f);
            world.spawnEntity(bud);

            // Drain stamina on use
            this.setStamina(Math.max(0.0f, this.getStamina() - 4.0f));
        }

        // Start cooldown (15 ticks ~= 0.75s)
        this.jungleBomberCooldown = 15;
        // Sync to client HUD trackers immediately
        this.setPowerCooldown(0, this.jungleBomberCooldown, 15);
    }

    protected boolean markEntityForBreakCore(net.minecraft.entity.LivingEntity entity) {
        if (entity.getWorld().isClient) return false;
        if (entity instanceof GestaltBase) return false; // Fail-safe: don't mark any Gestalt entity
        // Add a custom tag or use a helper class to manage marked entities.
        // We'll use a tag for now, and a global manager or a mixin to handle the logic.
        if (!((net.minecraft.entity.Entity)entity).getCommandTags().contains("gestaltresonance$break_core_marked")) {
            ((net.minecraft.entity.Entity)entity).addCommandTag("gestaltresonance$break_core_marked");
            // Visual feedback: 0xBF00FF (Vibrant Violet) matching ghost images
            if (entity.getWorld() instanceof net.minecraft.server.world.ServerWorld sw) {
                sw.spawnParticles(
                    new net.minecraft.particle.DustParticleEffect(new org.joml.Vector3f(0.75f, 0.0f, 1.0f), 1.5f),
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    20, 0.5, 0.5, 0.5, 0.05
                );
            }
            return true;
        }
        return false;
    }

    // HUD power state is now provided via synced trackers in GestaltBase
}
