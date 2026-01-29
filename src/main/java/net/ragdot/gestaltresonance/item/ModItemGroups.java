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
                    .icon(() -> new ItemStack(ModItems.ROTTEN_ESSENCE)).entries((displayContext, entries) -> {
                        entries.add(ModItems.POKEY_FEATHER);
                        entries.add(ModItems.ROTTEN_FEATHER);
                        entries.add(ModItems.ROTTEN_ESSENCE);
                    }).build());

    public static void registerItemGroups() {
        Gestaltresonance.LOGGER.info("Registering Item Groups for " + Gestaltresonance.MOD_ID);
    }
}
