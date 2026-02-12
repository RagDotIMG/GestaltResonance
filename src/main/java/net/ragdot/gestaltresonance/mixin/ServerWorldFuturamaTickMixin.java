package net.ragdot.gestaltresonance.mixin;

import net.minecraft.server.world.ServerWorld;
import net.ragdot.gestaltresonance.util.FuturamaManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldFuturamaTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void gestaltresonance$tickFuturama(CallbackInfo ci) {
        FuturamaManager.tick((ServerWorld) (Object) this);
    }
}
