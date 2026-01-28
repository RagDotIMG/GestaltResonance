package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltJumpPlayer;
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
    private void gestaltresonance$cancelGestaltFallDamage(
            float fallDistance, float damageMultiplier,
            DamageSource damageSource,
            CallbackInfoReturnable<Boolean> cir
    ) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (!(self instanceof PlayerEntity player)) return;

        IGestaltJumpPlayer jumpData = (IGestaltJumpPlayer) player;
        if (!jumpData.gestaltresonance$isGestaltJumpActive()) return;

        // Cancel damage once
        jumpData.gestaltresonance$setGestaltJumpActive(false);

        // Cancel the fall damage: return false and stop vanilla logic
        cir.setReturnValue(false);
    }
}
