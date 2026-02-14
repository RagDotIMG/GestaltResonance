package net.ragdot.gestaltresonance.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class ModBlocks {
    public static final Block POPSPROUT = registerBlock("popsprout", new PopSproutBlock());
    public static final Block POP_PAD = registerBlockNoItem("pod_pad", new PopPadBlock());
    public static final Block CIRICE_BLOCK = registerBlockNoItem("cirice_block", new CiriceBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.WATER_BLUE)
            .noCollision()
            .nonOpaque()
            .strength(-1.0f, 3600000.0f)
            .dropsNothing()
            .sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
            .pistonBehavior(PistonBehavior.BLOCK)));

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
