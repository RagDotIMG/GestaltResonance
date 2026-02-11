package net.ragdot.gestaltresonance.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class ModBlocks {
    public static final Block POPSPROUT = registerBlock("popsprout", new PopSproutBlock());
    public static final Block POP_PAD = registerBlockNoItem("pod_pad", new PopPadBlock());

    private static Block registerBlock(String name, Block block) {
        // Register block
        Block registered = Registry.register(Registries.BLOCK, Identifier.of(Gestaltresonance.MOD_ID, name), block);
        // Register corresponding block item for placement/testing
        Registry.register(Registries.ITEM, Identifier.of(Gestaltresonance.MOD_ID, name), new BlockItem(registered, new Item.Settings()));
        return registered;
    }

    private static Block registerBlockNoItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Gestaltresonance.MOD_ID, name), block);
    }

    public static void registerModBlocks() {
        Gestaltresonance.LOGGER.info("Registering Mod Blocks for " + Gestaltresonance.MOD_ID);
    }
}
