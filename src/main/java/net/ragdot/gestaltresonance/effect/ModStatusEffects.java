package net.ragdot.gestaltresonance.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class ModStatusEffects {
    public static final RegistryEntry<StatusEffect> ROTTING = register("rotting", new RottingStatusEffect());
    public static final RegistryEntry<StatusEffect> BLASTED = register("blasted", new BlastedStatusEffect());
    public static final RegistryEntry<StatusEffect> INKED = register("inked", new InkedStatusEffect());
    public static final RegistryEntry<StatusEffect> LAND_PROTECTION = register("land_protection", new LandProtectionStatusEffect());

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(Gestaltresonance.MOD_ID, id), statusEffect);
    }

    public static void registerStatusEffects() {
        Gestaltresonance.LOGGER.info("Registering Status Effects for " + Gestaltresonance.MOD_ID);
    }
}
