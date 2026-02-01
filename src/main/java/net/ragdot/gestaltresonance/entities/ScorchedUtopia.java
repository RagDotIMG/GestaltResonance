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

import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import java.util.List;

public class ScorchedUtopia extends GestaltBase {
    protected static final TrackedData<Boolean> IS_AURA_ACTIVE = DataTracker.registerData(ScorchedUtopia.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int auraActiveTicks = 0;

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
                IGestaltPlayer gp = (IGestaltPlayer) getOwner();
                if (gp != null && gp.gestaltresonance$isGuarding()) {
                    // Don't apply aura while guarding
                    return;
                }

                float currentStamina = this.getStamina();
                if (currentStamina > 0) {
                    // Drain 2 stamina per second -> 0.1 per tick
                    this.setStamina(currentStamina - 0.1f);

                    // Award 1 EXP for every 5 seconds (100 ticks) active
                    auraActiveTicks++;
                    if (auraActiveTicks >= 100) {
                        this.setExp(this.getExp() + 1);
                        auraActiveTicks = 0;
                    }

                    if (this.age % 20 == 0) {
                        applyRottingAura();
                    }
                } else {
                    // Auto-disable if out of stamina
                    this.setAuraActive(false);
                    auraActiveTicks = 0;
                }
            } else {
                spawnAuraParticles();
            }
        } else {
            if (!this.getWorld().isClient) {
                auraActiveTicks = 0;
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

    @Override
    public net.minecraft.util.Identifier getGestaltId() {
        return net.minecraft.util.Identifier.of("gestaltresonance", "scorched_utopia");
    }

    private void applyRottingAura() {
        double range = 5.0;
        List<HostileEntity> hostiles = this.getWorld().getEntitiesByClass(
                HostileEntity.class,
                this.getBoundingBox().expand(range),
                entity -> entity.isAlive() && this.squaredDistanceTo(entity) <= range * range
        );

        for (HostileEntity hostile : hostiles) {
            int amplifier = Math.max(0, this.getLvl() - 1);
            hostile.addStatusEffect(new StatusEffectInstance(ModStatusEffects.ROTTING, 100, amplifier));
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
        return 0.10f; // Scorched Utopia has higher defense (85% reduction vs 70%)
    }
}
