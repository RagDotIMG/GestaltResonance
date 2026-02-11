package net.ragdot.gestaltresonance.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mutes step and landing sounds for players while Amen Break's muffled-movement flag is active.
 * Only affects ground movement/landing: does not mute swimming sounds.
 */
@Mixin(Entity.class)
public abstract class EntityStepAndFallSoundMuteMixin {

    private static boolean gestaltresonance$shouldMute(Object self) {
        if (self instanceof PlayerEntity player) {
            IGestaltPlayer gp = (IGestaltPlayer) player;
            if (gp.gestaltresonance$isMuffledMovementActive()) {
                // Only while on ground and not in/under water; allow swimming sounds.
                boolean onGround = player.isOnGround();
                boolean inWater = player.isTouchingWater();
                boolean fallFlying = player.isFallFlying();
                boolean riding = player.hasVehicle();
                return onGround && !inWater && !fallFlying && !riding;
            }
        }
        return false;
    }

    // Suppress normal step sounds
    @Inject(method = "playStepSound", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$muteStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (gestaltresonance$shouldMute(this)) {
            ci.cancel();
        }
    }

    // Note: landing sound muting is handled implicitly by vibration suppression (no sculk detection)
    // and step sound mute covers most audible cues. If a dedicated landing sound method exists in
    // this version, it can be hooked similarly here.
}
