package net.ragdot.gestaltresonance.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class ModModelLayers {
    public static final EntityModelLayer SCORCHED_UTOPIA =
            new EntityModelLayer(Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia"), "main");
}
