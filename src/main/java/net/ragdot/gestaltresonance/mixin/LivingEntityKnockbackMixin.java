package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityKnockbackMixin {

    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelKnockbackDuringRedirection(double strength, double x, double z, CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player) {
            if (((IGestaltPlayer) player).gestaltresonance$isRedirectionActive()) {
                ci.cancel();
            }
        }
    }
}
