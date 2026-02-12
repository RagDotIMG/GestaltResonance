package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Tier II form of Scorched Utopia. Inherits Tier I behavior for now.
 */
public class ScorchedUtopiaII extends ScorchedUtopia {
    public ScorchedUtopiaII(EntityType<? extends ScorchedUtopiaII> type, World world) {
        super(type, world);
    }

    @Override
    public Identifier getGestaltId() {
        return Identifier.of("gestaltresonance", "scorched_utopia_ii");
    }

    @Override
    protected int getMaxLevel() {
        return 8;
    }
}
