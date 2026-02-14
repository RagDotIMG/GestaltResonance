package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public class PopSprout extends PathAwareEntity {
    public PopSprout(EntityType<? extends PopSprout> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushAway(net.minecraft.entity.Entity entity) {
    }
}
