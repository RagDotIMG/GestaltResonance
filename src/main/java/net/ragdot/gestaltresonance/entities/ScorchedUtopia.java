package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;

public class ScorchedUtopia extends CustomStand {

    public ScorchedUtopia(EntityType<? extends ScorchedUtopia> type, World world) {
        super(type, world);
    }

    // Attributes specific to this stand (can override base)
    public static DefaultAttributeContainer.Builder createAttributes() {
        return CustomStand.createBaseStandAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0); // example: more HP
        // add any extra attributes specific to this stand here
    }

    @Override
    protected void updatePositionToOwner() {
        // Optionally tweak offsets just for ScorchedUtopia
        // Example: a bit further back and higher
        double playerX = owner.getX();
        double playerY = owner.getY();
        double playerZ = owner.getZ();

        float yaw = owner.getYaw();
        double backOffset = -0.9;
        double sideOffset = 0.8;
        double heightOffset = 0.3;

        double rad = Math.toRadians(yaw);

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double targetX = playerX + backOffset * backX + sideOffset * rightX;
        double targetZ = playerZ + backOffset * backZ + sideOffset * rightZ;
        double targetY = playerY + heightOffset;

        this.refreshPositionAndAngles(targetX, targetY, targetZ, yaw, this.getPitch());
    }

    // Later: add special abilities for this stand (fire effects, attacks, etc.)
}
