package net.ragdot.gestaltresonance.mixin;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.ragdot.gestaltresonance.client.FuturamaClientManager;
import net.ragdot.gestaltresonance.network.FuturamaSyncPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityFuturamaGhostMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow protected M model;

    @Shadow protected abstract float getAnimationProgress(T entity, float tickDelta);

    @Shadow protected abstract void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale);

    @Shadow protected abstract float getAnimationCounter(T entity, float tickDelta);

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("RETURN")
    )
    private void gestaltresonance$renderFuturamaGhosts(T entity, float yaw, float tickDelta, MatrixStack matrices, net.minecraft.client.render.VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity.isInvisible()) return;

        // Skip ghosts for the local player and their own Gestalt
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (entity.equals(client.player)) return;
        if (entity instanceof net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase gestalt) {
            if (client.player != null && client.player.getUuid().equals(gestalt.getOwnerUuid())) {
                return;
            }
        }

        FuturamaClientManager.GhostData ghostData = FuturamaClientManager.getGhostData(entity.getUuid());
        if (ghostData == null) return;

        // Render ghosts at 15 and 30 ticks ahead
        renderGhost(entity, ghostData, 15, 0.4f, yaw, tickDelta, matrices, vertexConsumers, light);
        renderGhost(entity, ghostData, 30, 0.2f, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void renderGhost(T entity, FuturamaClientManager.GhostData ghostData, int ticksAhead, float alpha, float yaw, float tickDelta, MatrixStack matrices, net.minecraft.client.render.VertexConsumerProvider vertexConsumers, int light) {
        FuturamaSyncPayload.GhostFrame futureFrame = ghostData.getFutureFrame(ticksAhead);
        if (futureFrame == null) return;

        matrices.push();

        // Calculate interpolation between current and future frame would be better for smoothness,
        // but for now let's just use the absolute position relative to the entity's ACTUAL interpolated position.
        double renderX = net.minecraft.util.math.MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
        double renderY = net.minecraft.util.math.MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
        double renderZ = net.minecraft.util.math.MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());

        double dx = futureFrame.pos().x - renderX;
        double dy = futureFrame.pos().y - renderY;
        double dz = futureFrame.pos().z - renderZ;
        matrices.translate(dx, dy, dz);

        float animationProgress = this.getAnimationProgress(entity, tickDelta);
        this.setupTransforms(entity, matrices, animationProgress, futureFrame.yaw(), tickDelta, 1.0f);

        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(0.0f, -1.501f, 0.0f);

        // Violet tint: 0xA020F0 or 0x8A2BE2 (BlueViolet)
        // Let's go with a vibrant violet: 0xBF00FF
        int color = ((int)(alpha * 255) << 24) | 0xBF00FF;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(net.minecraft.client.render.RenderLayer.getEntityTranslucent(((LivingEntityRenderer)(Object)this).getTexture(entity)));
        this.model.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f), color);

        matrices.pop();
    }
}
