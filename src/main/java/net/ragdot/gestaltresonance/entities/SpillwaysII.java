package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Tier II form of Spillways. Inherits Tier I behavior for now.
 */
public class SpillwaysII extends Spillways {
    public SpillwaysII(EntityType<? extends SpillwaysII> type, World world) {
        super(type, world);
    }

    @Override
    public int getPowerCount() {
        return 2;
    }

    @Override
    public Identifier getGestaltId() {
        return Identifier.of("gestaltresonance", "spillways_ii");
    }

    @Override
    protected int getMaxLevel() {
        return 8;
    }
}
