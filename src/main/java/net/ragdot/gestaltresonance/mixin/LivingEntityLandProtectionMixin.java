package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.ragdot.gestaltresonance.effect.ModStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityLandProtectionMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelOutOfWaterDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        
        if (self.hasStatusEffect(ModStatusEffects.LAND_PROTECTION)) {
            if (source.isOf(DamageTypes.OUT_OF_WORLD)) {
                // Do not cancel out of world damage for safety
                return;
            }
            
            // In 1.20.1+, suffocation/drowning/out of water damage usually has specific types.
            // Squids take damage from DRY_OUT (in some versions) or just generic suffocation.
            // Actually, in many versions it's DamageTypes.DRY_OUT.
            if (source.isOf(DamageTypes.DRY_OUT) || source.isOf(DamageTypes.IN_WALL) || source.isOf(DamageTypes.DROWN)) {
                cir.setReturnValue(false);
            }
        }
    }
}
