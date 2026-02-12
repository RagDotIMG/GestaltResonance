package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.ragdot.gestaltresonance.util.FuturamaManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityFuturamaRemovalCaptureMixin {

    @Inject(method = "remove(Lnet/minecraft/entity/Entity$RemovalReason;)V", at = @At("HEAD"))
    private void gestaltresonance$futuramaCaptureRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.getWorld().isClient) return;
        if (!(self.getWorld() instanceof ServerWorld sw)) return;
        FuturamaManager.onEntityRemove(sw, self);
    }
}
