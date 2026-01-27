package net.ragdot.featherfall.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.ragdot.featherfall.Featherfall;
import net.ragdot.featherfall.entities.CustomStand;
import net.ragdot.featherfall.entities.ScorchedUtopia;

public class FeatherfallClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Base / default stand
        EntityRendererRegistry.register(
                Featherfall.CUSTOM_STAND,
                ctx -> new StandRenderer<>(ctx, "stand")
        );

        // ScorchedUtopia stand
        EntityRendererRegistry.register(
                Featherfall.SCORCHED_UTOPIA,
                ctx -> new StandRenderer<>(ctx, "scorched_utopia")
        );
    }

    /**
     * Generic stand renderer usable for any CustomStand subclass.
     * Uses a biped model with a per-stand texture.
     */
    public static class StandRenderer<T extends CustomStand> extends MobEntityRenderer<T, BipedEntityModel<T>> {

        private final Identifier texture;

        public StandRenderer(EntityRendererFactory.Context ctx, String textureName) {
            super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)), 0.5f);
            this.texture = Identifier.of(
                    Featherfall.MOD_ID,
                    "textures/entity/" + textureName + ".png"
            );
        }

        // Hide stand when it's too close to its owner (your original logic, now generic)
        @Override
        public void render(T entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

            if (shouldHideForLocalPlayer(entity)) {
                return;
            }

            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }

        private boolean shouldHideForLocalPlayer(T stand) {
            PlayerEntity owner = stand.getOwner();
            if (owner == null) return false;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != owner) return false;

            double dx = stand.getX() - owner.getX();
            double dy = stand.getEyeY() - owner.getEyeY();
            double dz = stand.getZ() - owner.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;

            // hide if closer than ~1.0 block
            return distSq < 1.0;
        }

        @Override
        public Identifier getTexture(T entity) {
            return texture;
        }
    }
}

