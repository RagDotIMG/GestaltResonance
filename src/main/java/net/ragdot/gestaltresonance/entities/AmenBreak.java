package net.ragdot.gestaltresonance.entities;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.world.World;


public class AmenBreak extends GestaltBase {


    public AmenBreak(EntityType<? extends AmenBreak> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    protected void updatePositionToOwner() {
        if (owner == null || !owner.isAlive()) return;

        IGestaltPlayer gp = (IGestaltPlayer) owner;
        boolean isGuarding = gp.gestaltresonance$isGuarding();

        double playerX = owner.getX();
        double playerY = owner.getY();
        double playerZ = owner.getZ();
        float yaw = owner.getYaw();

        double heightOffset = 0.4;

        if (isGuarding) {
            // Guard mode: Locked in front of player
            double frontOffset = 0.8;
            double rad = Math.toRadians(yaw);
            double frontX = -Math.sin(rad);
            double frontZ = Math.cos(rad);

            double targetX = playerX + frontOffset * frontX;
            double targetZ = playerZ + frontOffset * frontZ;
            double targetY = playerY + heightOffset;

            applySmoothPosition(targetX, targetY, targetZ, yaw, false);
        } else {
            // Normal mode: Slightly behind and FURTHER to the side for Amen Break
            double backOffset = -0.5;
            double sideOffset = 0.1;

            double rad = Math.toRadians(yaw);

            double backX = -Math.sin(rad);
            double backZ =  Math.cos(rad);
            double rightX =  Math.cos(rad);
            double rightZ =  Math.sin(rad);

            double targetX = playerX + backOffset * backX + sideOffset * rightX;
            double targetZ = playerZ + backOffset * backZ + sideOffset * rightZ;
            double targetY = playerY + heightOffset;

            applySmoothPosition(targetX, targetY, targetZ, yaw, false);
        }
    }

    // Attributes specific to this stand (can override base)
    public static DefaultAttributeContainer.Builder createAttributes() {
        return GestaltBase.createBaseStandAttributes();
    }


    @Override
    protected boolean canMeleeAttack() {
        return false;
    }

    @Override
    protected double getMaxChaseRange() { return 1.0; }

    @Override
    protected double getAttackReach() { return 8.0; }

    @Override
    protected float getAttackDamage() { return 8.0f; }

    @Override
    protected int getAttackCooldownTicks() { return 40; }

    @Override
    protected float getDamageReductionFactor() {
        return 0.0f;
    }
}
