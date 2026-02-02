package net.ragdot.gestaltresonance.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class InkedStatusEffect extends StatusEffect {
    public InkedStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x1a1a1a); // Dark black color
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Apply Blindness and Water Breathing for 20 seconds (400 ticks)
        // We use 400 ticks as requested. 
        // Note: The effect itself usually lasts as long as the Inked effect is active, 
        // but the prompt says "Inked blinds you and gives you water breathing for 20 seconds".
        // If Inked is a status effect, it should probably manage these internally or apply them.
        
        if (!entity.getWorld().isClient) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 400, 0, false, false, true));
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 400, 0, false, false, true));
        }
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Apply once at the start or periodically? 
        // Usually, if a custom effect "gives" other effects, it does it on the first tick or refreshes them.
        // Let's make it apply at the start (when duration is at its max if we knew it) 
        // or just keep it active while Inked is active.
        // If the user said "Inked blinds you... for 20 seconds", maybe Inked itself should be 20 seconds.
        return duration % 20 == 0; // Refresh every second
    }
}
