package net.ragdot.gestaltresonance.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class ModItemGroups {
    public static final ItemGroup GESTALT_RESONANCE_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Gestaltresonance.MOD_ID, "gestalt_resonance"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.gestaltresonance.gestalt_resonance"))
                    .icon(() -> new ItemStack(ModItems.RESONANT_DUST)).entries((displayContext, entries) -> {
                        entries.add(ModItems.POKEY_FEATHER);
                        entries.add(ModItems.ROTTEN_FEATHER);
                        entries.add(ModItems.BLASTED_FEATHER);
                        entries.add(ModItems.ROTTEN_ESSENCE);
                        entries.add(ModItems.BLASTED_ESSENCE);
                        entries.add(ModItems.INKED_ESSENCE);
                        entries.add(ModItems.INKED_FEATHER);
                        entries.add(ModItems.RESONANT_DUST);
                        entries.add(ModItems.NEATHER_TEAR);
                        entries.add(ModItems.RESONANT_FLESH);
                        entries.add(ModItems.RESONANT_INK);
                        entries.add(ModItems.RESONANT_POWDER);
                    }).build());

    public static void registerItemGroups() {
        Gestaltresonance.LOGGER.info("Registering Item Groups for " + Gestaltresonance.MOD_ID);
    }
}
