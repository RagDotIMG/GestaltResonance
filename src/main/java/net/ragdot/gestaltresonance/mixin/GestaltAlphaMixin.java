package net.ragdot.gestaltresonance.mixin;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.ragdot.gestaltresonance.client.GestaltresonanceClient;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class GestaltAlphaMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Redirect(
        method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
        )
    )
    private void gestaltresonance$redirectModelRender(EntityModel<T> model, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color, T entity) {
        int finalColor = color;
        if (entity instanceof GestaltBase gestalt) {
            if (GestaltresonanceClient.GestaltRenderer.isBlockingFirstPersonView(gestalt)) {
                // Apply 50% alpha (0x80)
                finalColor = (0x80 << 24) | (color & 0xFFFFFF);
            }
        }
        model.render(matrices, vertices, light, overlay, finalColor);
    }
}
