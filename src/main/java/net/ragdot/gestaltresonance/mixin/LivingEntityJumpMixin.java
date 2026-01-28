package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.ragdot.gestaltresonance.entities.GestaltBase;
import net.ragdot.gestaltresonance.util.IGestaltJumpPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class LivingEntityJumpMixin {

    @Inject(method = "jump", at = @At("TAIL"))
    private void gestaltresonance$onJump(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!(player.getWorld() instanceof ServerWorld serverWorld)) return;
        if (!player.isSneaking()) return;

        boolean hasGestalt = !serverWorld.getEntitiesByClass(
                GestaltBase.class,
                player.getBoundingBox().expand(4.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        ).isEmpty();

        if (!hasGestalt) return;

        double extraY = 0.47;
        double horizontalBoost = 0.7;

        float yaw = player.getYaw();
        double yawRad = Math.toRadians(yaw);
        double forwardX = -Math.sin(yawRad);
        double forwardZ =  Math.cos(yawRad);

        player.addVelocity(forwardX * horizontalBoost, extraY, forwardZ * horizontalBoost);
        player.velocityModified = true;

        // Mark this player as having an active Gestalt super jump
        ((IGestaltJumpPlayer) player).gestaltresonance$setGestaltJumpActive(true);
    }
}



