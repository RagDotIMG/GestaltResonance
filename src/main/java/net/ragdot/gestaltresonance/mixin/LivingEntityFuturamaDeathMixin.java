package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.ragdot.gestaltresonance.util.FuturamaManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFuturamaDeathMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$preventPlayerDeathDuringFuturamaRecording(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayerEntity player)) return;
        if (player.getWorld().isClient) return;

        // Only intervene for lethal damage.
        float effectiveHealth = player.getHealth() + player.getAbsorptionAmount();
        if (amount < effectiveHealth) return;

        if (FuturamaManager.tryPreventPlayerDeathDuringRecording(player)) {
            // Keep the player alive through the recording window.
            player.setHealth(Math.max(1.0f, Math.min(player.getMaxHealth(), player.getHealth())));
            player.setFireTicks(0);
            player.fallDistance = 0.0f;

            cir.setReturnValue(false);
        }
    }
}
