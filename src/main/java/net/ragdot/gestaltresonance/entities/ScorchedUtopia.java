package net.ragdot.gestaltresonance.entities;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.effect.ModStatusEffects;

import java.util.List;

public class ScorchedUtopia extends GestaltBase {
    protected static final TrackedData<Boolean> IS_AURA_ACTIVE = DataTracker.registerData(ScorchedUtopia.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ScorchedUtopia(EntityType<? extends ScorchedUtopia> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_AURA_ACTIVE, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isAuraActive()) {
            if (!this.getWorld().isClient) {
                if (this.age % 20 == 0) {
                    applyRottingAura();
                }
            } else {
                spawnAuraParticles();
            }
        }
    }

    private void spawnAuraParticles() {
        double range = 5.0;
        if (this.random.nextFloat() < 0.3f) {
            double angle = this.random.nextDouble() * Math.PI * 2;
            double dist = this.random.nextDouble() * range;
            double offsetX = Math.cos(angle) * dist;
            double offsetZ = Math.sin(angle) * dist;

            BlockPos floorPos = BlockPos.ofFloored(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ);
            // Try to find the floor
            for (int i = 0; i < 3; i++) {
                if (this.getWorld().getBlockState(floorPos).isSolidBlock(this.getWorld(), floorPos)) {
                    break;
                }
                floorPos = floorPos.down();
            }
            
            double spawnY = floorPos.getY() + 1.05; // slightly above floor
            
            // Brownish red color: R=0.5, G=0.1, B=0.1
            this.getWorld().addParticle(
                EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0.5f, 0.1f, 0.1f),
                this.getX() + offsetX,
                spawnY,
                this.getZ() + offsetZ,
                0, 0, 0
            );
        }
    }

    public boolean isAuraActive() {
        return this.dataTracker.get(IS_AURA_ACTIVE);
    }

    public void setAuraActive(boolean active) {
        this.dataTracker.set(IS_AURA_ACTIVE, active);
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
