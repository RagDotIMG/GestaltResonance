package net.ragdot.gestaltresonance.entities;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.effect.ModStatusEffects;

import java.util.List;

public class ScorchedUtopia extends GestaltBase {


    public ScorchedUtopia(EntityType<? extends ScorchedUtopia> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && this.age % 20 == 0) {
            applyRottingAura();
        }
    }

    private void applyRottingAura() {
        double range = 5.0;
        List<HostileEntity> hostiles = this.getWorld().getEntitiesByClass(
                HostileEntity.class,
                this.getBoundingBox().expand(range),
                entity -> entity.isAlive() && this.squaredDistanceTo(entity) <= range * range
        );

        for (HostileEntity hostile : hostiles) {
            hostile.addStatusEffect(new StatusEffectInstance(ModStatusEffects.ROTTING, 100, 0));
        }
    }

    // Attributes specific to this stand (can override base)
    public static DefaultAttributeContainer.Builder createAttributes() {
        return GestaltBase.createBaseStandAttributes();
    }


    @Override
    protected boolean canMeleeAttack() {
        return true;
    }

    @Override
    protected double getMaxChaseRange() {
        return 6.0; // can chase a bit further
    }

    @Override
    protected double getAttackReach() {
        return 2.0; // slightly shorter punch
    }

    @Override
    protected float getAttackDamage() {
        return 4.0f; // 2 hearts
    }

    @Override
    protected int getAttackCooldownTicks() {
        return 40; // 2s
    }

    @Override
    protected float getDamageReductionFactor() {
        return 0.15f; // Scorched Utopia has higher defense (85% reduction vs 70%)
    }
}
