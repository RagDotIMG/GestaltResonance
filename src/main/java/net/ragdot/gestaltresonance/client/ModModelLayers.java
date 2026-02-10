package net.ragdot.gestaltresonance.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public class ModModelLayers {
    public static final EntityModelLayer SCORCHED_UTOPIA =
            new EntityModelLayer(Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia"), "main");
    public static final EntityModelLayer AMEN_BREAK =
            new EntityModelLayer(Identifier.of(Gestaltresonance.MOD_ID, "amen_break"), "main");
    public static final EntityModelLayer SPILLWAYS =
            new EntityModelLayer(Identifier.of(Gestaltresonance.MOD_ID, "spillways"), "main");
    public static final EntityModelLayer POP_BUD =
            new EntityModelLayer(Identifier.of(Gestaltresonance.MOD_ID, "pop_bud"), "main");
}
