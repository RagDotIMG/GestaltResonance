package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.effect.ModStatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityRottingDeathMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void gestaltresonance$applyBonemealOnRottingDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        World world = self.getWorld();
        
        if (!world.isClient && self.hasStatusEffect(ModStatusEffects.ROTTING)) {
            BlockPos pos = self.getBlockPos();
            // Apply bonemeal effect
            if (BoneMealItem.useOnFertilizable(new ItemStack(Items.BONE_MEAL), world, pos)) {
                world.syncWorldEvent(2005, pos, 0);
            } else if (BoneMealItem.useOnFertilizable(new ItemStack(Items.BONE_MEAL), world, pos.down())) {
                world.syncWorldEvent(2005, pos.down(), 0);
            }
        }
    }
}
