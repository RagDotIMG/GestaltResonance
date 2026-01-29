package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.ragdot.gestaltresonance.entities.GestaltBase;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.sound.SoundEvent;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "playSound(Lnet/minecraft/sound/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelSoundDuringRedirection(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        if (((IGestaltPlayer) this).gestaltresonance$isRedirectionActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleStatus", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelHurtAnimationDuringRedirection(byte status, CallbackInfo ci) {
        if (status == 2 && ((IGestaltPlayer) this).gestaltresonance$isRedirectionActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelTravelDuringLedgeGrab(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (((IGestaltPlayer) player).gestaltresonance$isLedgeGrabbing()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float gestaltresonance$modifyDamageAmount(float amount, DamageSource source) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) player;

        if (gestaltPlayer.gestaltresonance$isGuarding()) {
            Vec3d sourcePos = source.getPosition();
            if (sourcePos != null) {
                Vec3d playerPos = player.getPos();
                Vec3d dirToSource = sourcePos.subtract(playerPos).normalize();
                Vec3d playerFacing = player.getRotationVec(1.0f);

                double dot = dirToSource.dotProduct(new Vec3d(playerFacing.x, 0, playerFacing.z).normalize());
                if (dot > 0) {
                    return amount * 0.2f; // 80% reduction
                }
            }
        }
        return amount;
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void gestaltresonance$onGestaltThrow(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!(player.getWorld() instanceof ServerWorld serverWorld) || !player.isSneaking()) return;

        boolean hasGestalt = !serverWorld.getEntitiesByClass(
                GestaltBase.class,
                player.getBoundingBox().expand(4.0),
                stand -> player.getUuid().equals(stand.getOwnerUuid())
        ).isEmpty();

        if (!hasGestalt) return;

        double extraY = 0.38;
        double horizontalBoost = 0.9;

        float yaw = player.getYaw();
        double yawRad = Math.toRadians(yaw);
        double forwardX = -Math.sin(yawRad);
        double forwardZ =  Math.cos(yawRad);

        player.addVelocity(forwardX * horizontalBoost, extraY, forwardZ * horizontalBoost);
        player.velocityModified = true;

        // Mark this player as having an active GestaltThrow
        ((IGestaltPlayer) player).gestaltresonance$setGestaltThrowActive(true);

        // Set 1 second cooldown for ledge grab after jumping
        ((IGestaltPlayer) player).gestaltresonance$setLedgeGrabCooldown(20);
    }
}



