package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFallDamageMixin {

    @Inject(
            method = "handleFallDamage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gestaltresonance$cancelGestaltThrowFallDamage(
            float fallDistance, float damageMultiplier,
            DamageSource damageSource,
            CallbackInfoReturnable<Boolean> cir
    ) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!(self instanceof PlayerEntity player)) return;

        IGestaltPlayer throwData = (IGestaltPlayer) player;
        if (!throwData.gestaltresonance$isGestaltThrowActive()) return;

        // Cancel damage once
        throwData.gestaltresonance$setGestaltThrowActive(false);

        // Cancel the fall damage: return false and stop vanilla logic
        cir.setReturnValue(false);
    }
}
