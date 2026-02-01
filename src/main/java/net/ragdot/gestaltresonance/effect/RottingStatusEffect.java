package net.ragdot.gestaltresonance.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class RottingStatusEffect extends StatusEffect {
    public RottingStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x4d5d2c); // Dark brownish-green color
        
        // -20% Speed
        this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                Identifier.of(Gestaltresonance.MOD_ID, "rotting_speed"),
                -0.2,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        
        // -20% Attack Damage
        this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                Identifier.of(Gestaltresonance.MOD_ID, "rotting_damage"),
                -0.2,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Base 2.0 damage + 1.0 per amplifier level
        float damage = 2.0f + amplifier;
        entity.damage(entity.getDamageSources().magic(), damage);
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // 5 seconds = 100 ticks
        return duration % 100 == 0;
    }
}
