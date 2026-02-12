package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Tier III form of Spillways.
 */
public class SpillwaysIII extends SpillwaysII {
    public SpillwaysIII(EntityType<? extends SpillwaysIII> type, World world) {
        super(type, world);
    }

    @Override
    public Identifier getGestaltId() {
        return Identifier.of("gestaltresonance", "spillways_iii");
    }

    @Override
    protected int getMaxLevel() {
        return 10;
    }
}
