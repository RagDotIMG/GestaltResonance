package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Tier III form of Scorched Utopia.
 */
public class ScorchedUtopiaIII extends ScorchedUtopiaII {
    public ScorchedUtopiaIII(EntityType<? extends ScorchedUtopiaIII> type, World world) {
        super(type, world);
    }

    @Override
    public int getPowerCount() {
        return 3;
    }

    @Override
    public Identifier getGestaltId() {
        return Identifier.of("gestaltresonance", "scorched_utopia_iii");
    }

    @Override
    protected int getMaxLevel() {
        return 10;
    }
}
