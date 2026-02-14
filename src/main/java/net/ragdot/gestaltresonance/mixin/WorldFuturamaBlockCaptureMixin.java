package net.ragdot.gestaltresonance.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.util.FuturamaManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldFuturamaBlockCaptureMixin {

    @Inject(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("HEAD")
    )
    private void gestaltresonance$futuramaCaptureOld(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        World self = (World) (Object) this;
        if (self.isClient) return;
        if (!(self instanceof ServerWorld sw)) return;

        BlockState old = self.getBlockState(pos);
        FuturamaManager.onBlockSetHead(sw, pos, old);
    }

    @Inject(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("RETURN")
    )
    private void gestaltresonance$futuramaCaptureNew(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        World self = (World) (Object) this;
        if (self.isClient) return;
        if (!(self instanceof ServerWorld sw)) return;

        if (cir.getReturnValue() != null && cir.getReturnValue()) {
            FuturamaManager.onBlockSetReturn(sw, pos, self.getBlockState(pos), true);
        }
    }
}
