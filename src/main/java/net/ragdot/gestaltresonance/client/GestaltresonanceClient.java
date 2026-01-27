package net.ragdot.gestaltresonance.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
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
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.client.model.ScorchedUtopiaModel;
import net.ragdot.gestaltresonance.entities.GestaltBase;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;

public class GestaltresonanceClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 1) register model layer for ScorchedUtopia Blockbench model
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.SCORCHED_UTOPIA,
                ScorchedUtopiaModel::getTexturedModelData
        );

        // 2) base / default stand uses vanilla biped
        EntityRendererRegistry.register(
                Gestaltresonance.CUSTOM_STAND,
                ctx -> new StandRenderer<>(ctx, "stand")
        );

        // 3) ScorchedUtopia uses custom Blockbench model
        EntityRendererRegistry.register(
                Gestaltresonance.SCORCHED_UTOPIA,
                ScorchedUtopiaRenderer::new
        );
    }

    // ===== Generic renderer for base stands (Biped model) =====
    public static class StandRenderer<T extends GestaltBase> extends MobEntityRenderer<T, BipedEntityModel<T>> {

        private final Identifier texture;

        public StandRenderer(EntityRendererFactory.Context ctx, String textureName) {
            super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)), 0.5f);
            this.texture = Identifier.of(
                    Gestaltresonance.MOD_ID,
                    "textures/entity/" + textureName + ".png"
            );
        }

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

            return distSq < 1.0;
        }

        @Override
        public Identifier getTexture(T entity) {
            return texture;
        }
    }

    // ===== Renderer that uses Blockbench model for ScorchedUtopia =====
    public static class ScorchedUtopiaRenderer
            extends MobEntityRenderer<ScorchedUtopia, ScorchedUtopiaModel> {

        private static final Identifier TEXTURE = Identifier.of(
                Gestaltresonance.MOD_ID,
                "textures/entity/scorched_utopia.png"
        );

        public ScorchedUtopiaRenderer(EntityRendererFactory.Context ctx) {
            super(ctx,
                    new ScorchedUtopiaModel(ctx.getPart(ModModelLayers.SCORCHED_UTOPIA)),
                    0.5f
            );
        }

        @Override
        public void render(ScorchedUtopia entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

            // reuse your “hide when too close” rule
            PlayerEntity owner = entity.getOwner();
            if (owner != null) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player == owner) {
                    double dx = entity.getX() - owner.getX();
                    double dy = entity.getEyeY() - owner.getEyeY();
                    double dz = entity.getZ() - owner.getZ();
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq < 1.0) return;
                }
            }

            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }

        @Override
        public Identifier getTexture(ScorchedUtopia entity) {
            return TEXTURE;
        }
    }
}


