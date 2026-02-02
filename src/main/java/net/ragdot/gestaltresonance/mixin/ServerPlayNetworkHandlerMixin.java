package net.ragdot.gestaltresonance.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onPlayerInteractBlock", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelBlockPlacementWhileGuarding(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        if (((IGestaltPlayer) player).gestaltresonance$isGuarding()) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelBlockBreakingWhileGuarding(PlayerActionC2SPacket packet, CallbackInfo ci) {
        if (((IGestaltPlayer) player).gestaltresonance$isGuarding()) {
            PlayerActionC2SPacket.Action action = packet.getAction();
            if (action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK || action == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK || action == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
                ci.cancel();
            }
        }
    }
}
