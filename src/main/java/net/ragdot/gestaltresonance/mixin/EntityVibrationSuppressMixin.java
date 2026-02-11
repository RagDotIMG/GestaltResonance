package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppress vibration-emitting game events (STEP, HIT_GROUND) for players while Amen Break's
 * muffled movement is active, so Warden/Sculk sensors do not detect them.
 */
@Mixin(Entity.class)
public abstract class EntityVibrationSuppressMixin {

    @Inject(method = "emitGameEvent", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$suppressSpecificVibrations(RegistryEntry<GameEvent> eventEntry, Entity emitter, CallbackInfo ci) {
        Object self = this;
        if (self instanceof PlayerEntity player) {
            IGestaltPlayer gp = (IGestaltPlayer) player;
            if (gp.gestaltresonance$isMuffledMovementActive()) {
                // Only suppress ground movement/landing; keep swimming etc.
                boolean onGround = player.isOnGround();
                boolean inWater = player.isTouchingWater();
                boolean fallFlying = player.isFallFlying();
                boolean riding = player.hasVehicle();
                if (onGround && !inWater && !fallFlying && !riding) {
                    if (eventEntry.matches(GameEvent.STEP) || eventEntry.matches(GameEvent.HIT_GROUND)) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
