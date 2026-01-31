package net.ragdot.gestaltresonance.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelItemUseWhileGuarding(CallbackInfo ci) {
        if (player != null && ((IGestaltPlayer) player).gestaltresonance$isGuarding()) {
            ci.cancel();
        }
    }
}
