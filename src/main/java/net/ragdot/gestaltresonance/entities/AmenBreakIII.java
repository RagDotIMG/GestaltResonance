package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Tier III form of Amen Break.
 */
public class AmenBreakIII extends AmenBreakII {
    public AmenBreakIII(EntityType<? extends AmenBreakIII> type, World world) {
        super(type, world);
    }

    @Override
    public Identifier getGestaltId() {
        return Identifier.of("gestaltresonance", "amen_break_iii");
    }

    @Override
    protected int getMaxLevel() {
        return 10;
    }
}
