package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Suppress only walk/sprint/jump/land vibrations for muffled players at the sculk callback layer.
 * This ensures sculk sensors/warden do not detect those movements while Amen Break is active,
 * regardless of how/where the event originated.
 */
@Mixin(targets = "net.minecraft.block.entity.SculkSensorBlockEntity$VibrationCallback")
public abstract class SculkSensorVibrationCallbackMixin {

    @Inject(method = "accepts", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$denyMuffledPlayerMovement(ServerWorld world,
                                                            BlockPos pos,
                                                            RegistryEntry<GameEvent> event,
                                                            @Nullable GameEvent.Emitter emitter,
                                                            CallbackInfoReturnable<Boolean> cir) {
        if (world == null || world.isClient) return;

        Entity src = emitter == null ? null : emitter.sourceEntity();
        if (!(src instanceof PlayerEntity player)) return;

        if (!((IGestaltPlayer) player).gestaltresonance$isMuffledMovementActive()) return;

        String id = event.getKey().map(k -> k.getValue().toString()).orElse("");
        boolean isStep = event.matches(GameEvent.STEP) || "minecraft:step".equals(id);
        boolean isHitGround = event.matches(GameEvent.HIT_GROUND) || "minecraft:hit_ground".equals(id);
        boolean isMobStep = "minecraft:mob_step".equals(id);

        if (isStep || isHitGround || isMobStep) {
            // Return false so the sensor ignores the vibration from this player.
            cir.setReturnValue(false);
        }
    }
}
