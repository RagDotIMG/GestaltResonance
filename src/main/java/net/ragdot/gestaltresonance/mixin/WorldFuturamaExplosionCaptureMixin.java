package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.util.FuturamaManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldFuturamaExplosionCaptureMixin {

    @Inject(
            method = "createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;",
            at = @At("HEAD")
    )
    private void gestaltresonance$futuramaCaptureExplosion(Entity entity,
                                                          double x, double y, double z,
                                                          float power,
                                                          World.ExplosionSourceType explosionSourceType,
                                                          CallbackInfoReturnable<net.minecraft.world.explosion.Explosion> cir) {
        World self = (World) (Object) this;
        if (self.isClient) return;
        if (!(self instanceof ServerWorld sw)) return;
        FuturamaManager.onExplosion(sw, entity, x, y, z, power, explosionSourceType);
    }
}
