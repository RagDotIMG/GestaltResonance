package net.ragdot.gestaltresonance.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class BlastedStatusEffect extends StatusEffect {
    public BlastedStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0xffa500); // Orange color

        // +10% Speed
        this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                Identifier.of(Gestaltresonance.MOD_ID, "blasted_speed"),
                0.1,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        // -30% Defense (Armor)
        // Armor reduction is tricky because armor is a flat value. 
        // However, ADD_MULTIPLIED_TOTAL works on armor too.
        this.addAttributeModifier(EntityAttributes.GENERIC_ARMOR,
                Identifier.of(Gestaltresonance.MOD_ID, "blasted_defense"),
                -0.3,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
