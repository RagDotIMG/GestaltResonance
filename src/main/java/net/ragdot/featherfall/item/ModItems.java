package net.ragdot.featherfall.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ragdot.featherfall.Featherfall;

public class ModItems {
    public static final Item POKEY_FEATHER = registerItem("pokey_feather", new Item(new Item.Settings()));
    public static final Item ROTTEN_FEATHER = registerItem("rotten_feather", new RottenFeatherItem(new Item.Settings()));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Featherfall.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Featherfall.LOGGER.info("Registering Mod Items for" + Featherfall.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(POKEY_FEATHER);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(ROTTEN_FEATHER);
        });

    }
}
