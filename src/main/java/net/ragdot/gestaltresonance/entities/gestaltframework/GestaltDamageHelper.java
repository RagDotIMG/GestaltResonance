package net.ragdot.gestaltresonance.entities.gestaltframework;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

public class GestaltDamageHelper {
    private final GestaltBase gestalt;

    public GestaltDamageHelper(GestaltBase gestalt) {
        this.gestalt = gestalt;
    }

    public boolean handleDamage(DamageSource source, float amount) {
        if (gestalt.getWorld().isClient) return false;

        // Gestalten are invincible while attacking
        if (gestalt.getDataTracker().get(GestaltBase.IS_ATTACKING)) {
            return false;
        }

        // Gestalten do not take suffocation damage
        if (source.isOf(DamageTypes.IN_WALL)) {
            return false;
        }

        PlayerEntity owner = gestalt.getOwner();
        if (owner == null || !owner.isAlive()) {
            return false;
        }

        // Apply guard reduction
        float actualAmount = amount;
        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) owner;
        if (gestaltPlayer.gestaltresonance$isGuarding()) {
            Vec3d sourcePos = source.getPosition();
            if (sourcePos != null) {
                Vec3d playerPos = owner.getPos();
                Vec3d dirToSource = sourcePos.subtract(playerPos).normalize();
                Vec3d playerFacing = owner.getRotationVec(1.0f);
                double dot = dirToSource.dotProduct(new Vec3d(playerFacing.x, 0, playerFacing.z).normalize());
                if (dot > 0) {
                    actualAmount *= 0.2f; // 80% reduction
                }
            }
        }

        // New system: reduce damage by the factor and apply directly to owner health
        float transferred = actualAmount * gestalt.getDamageReductionFactor();

        // Check for owner invulnerability
        if (owner.isInvulnerableTo(source)) {
            return false;
        }

        // Apply health adjustment without hit feedback
        gestaltPlayer.gestaltresonance$setRedirectionActive(true);
        try {
            float newHealth = owner.getHealth() - transferred;
            owner.setHealth(Math.max(0, newHealth));

            if (newHealth <= 0) {
                owner.onDeath(source);
            }
        } finally {
            gestaltPlayer.gestaltresonance$setRedirectionActive(false);
        }

        return true;
    }

    public boolean isInvulnerableTo(DamageSource source) {
        if (source.isOf(DamageTypes.IN_WALL)) return true;
        return false;
    }
}
