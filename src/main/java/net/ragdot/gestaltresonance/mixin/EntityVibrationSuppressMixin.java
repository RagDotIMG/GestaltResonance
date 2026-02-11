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
        // Determine the player tied to this event: either the calling entity or the explicit emitter
        PlayerEntity player = null;
        Object self = this;
        if (self instanceof PlayerEntity p) {
            player = p;
        } else if (emitter instanceof PlayerEntity p) {
            player = p;
        }

        if (player == null) return;

        // Only relevant on the logical server; client-side events don't affect sculk detection
        if (player.getWorld().isClient) return;

        IGestaltPlayer gp = (IGestaltPlayer) player;
        if (!gp.gestaltresonance$isMuffledMovementActive()) return;

        // Targeted suppression: only walking/sprinting and landing
        String eventId = eventEntry.getKey().map(key -> key.getValue().toString()).orElse("");
        if (eventEntry.matches(GameEvent.STEP)
                || eventEntry.matches(GameEvent.HIT_GROUND)
                || "minecraft:mob_step".equals(eventId)) {
            ci.cancel();
        }
    }
}
