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
