package net.ragdot.featherfall.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.ragdot.featherfall.Featherfall;

public class ModModelLayers {
    public static final EntityModelLayer SCORCHED_UTOPIA =
            new EntityModelLayer(Identifier.of(Featherfall.MOD_ID, "scorched_utopia"), "main");
}
