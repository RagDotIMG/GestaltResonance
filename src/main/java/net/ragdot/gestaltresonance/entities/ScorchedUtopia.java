package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;

public class ScorchedUtopia extends GestaltBase {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public ScorchedUtopia(EntityType<? extends ScorchedUtopia> type, World world) {
        super(type, world);
    }

    // Attributes specific to this stand (can override base)
    public static DefaultAttributeContainer.Builder createAttributes() {
        return GestaltBase.createBaseStandAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }


    @Override
    protected void updatePositionToOwner() {
        double playerX = owner.getX();
        double playerY = owner.getY();
        double playerZ = owner.getZ();

        float yaw = owner.getYaw();
        double backOffset = -0.9;
        double sideOffset = 0.8;
        double heightOffset = 0.3;

        double rad = Math.toRadians(yaw);

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double targetX = playerX + backOffset * backX + sideOffset * rightX;
        double targetZ = playerZ + backOffset * backZ + sideOffset * rightZ;
        double targetY = playerY + heightOffset;

        this.refreshPositionAndAngles(targetX, targetY, targetZ, yaw, this.getPitch());
    }

    // === Combat tuning for Scorched Utopia ===
    @Override
    protected double getMaxChaseRange() {
        return 5.0; // can chase a bit further
    }

    @Override
    protected double getAttackReach() {
        return 5.0; // slightly longer punch
    }

    @Override
    protected float getAttackDamage() {
        return 4.0f; // 2 hearts
    }

    @Override
    protected int getAttackCooldownTicks() {
        return 10; // faster punches than default
    }
}
