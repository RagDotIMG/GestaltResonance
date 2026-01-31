package net.ragdot.gestaltresonance.entities.gestaltframework;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

public class GestaltAbilityHelper {
    private final GestaltBase gestalt;

    public GestaltAbilityHelper(GestaltBase gestalt) {
        this.gestalt = gestalt;
    }

    public void updateAbilities() {
        PlayerEntity owner = gestalt.getOwner();
        if (owner == null || !owner.isAlive()) return;

        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) owner;

        if (gestalt.getWorld().isClient) {
            // Client-side: update IS_THROWING from local owner state for zero-latency animation
            boolean isThrowing = gestaltPlayer.gestaltresonance$isGestaltThrowActive();
            if (gestalt.getDataTracker().get(GestaltBase.IS_THROWING) != isThrowing) {
                gestalt.getDataTracker().set(GestaltBase.IS_THROWING, isThrowing);
            }
        } else {
            // Server-side
            if (gestaltPlayer.gestaltresonance$isLedgeGrabbing()) {
                owner.setVelocity(Vec3d.ZERO);
                owner.velocityModified = true;
                owner.fallDistance = 0;
                owner.setNoGravity(true);
            } else {
                owner.setNoGravity(false);
            }

            // Blocking logic for Guard Mode
            if (gestaltPlayer.gestaltresonance$isGuarding()) {
                blockProjectilesAndEntities();
            }

            // Sync throw state to client
            boolean isThrowing = gestaltPlayer.gestaltresonance$isGestaltThrowActive();
            if (gestalt.getDataTracker().get(GestaltBase.IS_THROWING) != isThrowing) {
                gestalt.getDataTracker().set(GestaltBase.IS_THROWING, isThrowing);
            }
        }
    }

    private void blockProjectilesAndEntities() {
        PlayerEntity owner = gestalt.getOwner();
        if (owner == null) return;

        double radius = 2.0;
        var entities = gestalt.getWorld().getOtherEntities(gestalt, gestalt.getBoundingBox().expand(radius));
        Vec3d playerFacing = owner.getRotationVec(1.0f);
        Vec3d playerFacingFlat = new Vec3d(playerFacing.x, -0.5, playerFacing.z).normalize();

        for (var entity : entities) {
            if (entity == owner) continue;
            
            Vec3d toEntity = entity.getPos().subtract(owner.getPos());
            Vec3d toEntityFlat = new Vec3d(toEntity.x, 0, toEntity.z).normalize();
            
            double dot = toEntityFlat.dotProduct(playerFacingFlat);
            
            if (dot > 0) {
                if (entity instanceof ProjectileEntity projectile) {
                    Vec3d projVel = projectile.getVelocity();
                    if (projVel.dotProduct(playerFacingFlat) < 0) {
                        projectile.setVelocity(projVel.multiply(-0.5));
                        projectile.velocityModified = true;
                    }
                } else if (entity instanceof HostileEntity) {
                    if (toEntity.lengthSquared() < 4.0) { // 2.0 blocks
                        Vec3d pushDir = toEntityFlat.multiply(0.3);
                        entity.addVelocity(pushDir.x, 0.1, pushDir.z);
                        entity.velocityModified = true;
                    }
                }
            }
        }
    }
}
