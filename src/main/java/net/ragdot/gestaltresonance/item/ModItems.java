package net.ragdot.gestaltresonance.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class ModItems {
    public static final Item POKEY_FEATHER = registerItem("pokey_feather", new PokeyFeatherItem(new Item.Settings()));
    public static final Item ROTTEN_FEATHER = registerItem("rotten_feather", new RottenFeatherItem(new Item.Settings()));
    public static final Item BLASTED_FEATHER = registerItem("blasted_feather", new BlastedFeatherItem(new Item.Settings()));
    public static final Item INKED_FEATHER = registerItem("inked_feather", new InkedFeatherItem(new Item.Settings()));
    public static final Item RESONANT_DUST = registerItem("resonant_dust", new Item(new Item.Settings()));
    public static final Item NETHER_TEAR = registerItem("nether_tear", new NetherTearItem(new Item.Settings()));
    public static final Item RESONANT_FLESH = registerItem("resonant_flesh", new Item(new Item.Settings()));
    public static final Item RESONANT_INK = registerItem("resonant_ink", new Item(new Item.Settings()));
    public static final Item RESONANT_POWDER = registerItem("resonant_powder", new Item(new Item.Settings()));
    public static final Item ROTTEN_ESSENCE = registerItem("rotten_essence", new Item(new Item.Settings()));
    public static final Item INKED_ESSENCE = registerItem("inked_essence", new Item(new Item.Settings()));
    public static final Item BLASTED_ESSENCE = registerItem("blasted_essence", new Item(new Item.Settings()));
    public static final Item SOUL_DUST = registerItem("soul_dust", new Item(new Item.Settings()));
    public static final Item SOUL_TEAR = registerItem("soul_tear", new Item(new Item.Settings()));
    public static final Item SOUL_WART = registerItem("soul_wart", new Item(new Item.Settings()));
    public static final Item SOUL_ESSENCE = registerItem("soul_essence", new SoulEssenceItem(new Item.Settings()));
    public static final Item RESONANT_SOUL_STAR = registerItem("resonant_soul_star", new ResonantSoulStarItem(new Item.Settings()));
    public static final Item DEBUG_EXP_BOOSTER = registerItem("debug_exp_booster", new DebugExpBoosterItem(new Item.Settings()));
    public static final Item DEBUG_COOLDOWN_RESETTER = registerItem("debug_cooldown_resetter", new DebugCooldownResetItem(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Gestaltresonance.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Gestaltresonance.LOGGER.info("Registering Mod Items for " + Gestaltresonance.MOD_ID);
    }
}
