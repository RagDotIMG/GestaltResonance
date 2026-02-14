package net.ragdot.gestaltresonance.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.ragdot.gestaltresonance.client.model.AmenBreakIIModel;
import net.ragdot.gestaltresonance.client.model.AmenBreakIIIModel;
import net.ragdot.gestaltresonance.client.model.AmenBreakModel;
import net.ragdot.gestaltresonance.client.model.SpillwaysModel;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.ragdot.gestaltresonance.network.FuturamaRecordingPayload;
import net.ragdot.gestaltresonance.network.FuturamaSyncPayload;
import net.ragdot.gestaltresonance.network.ToggleGestaltSummonPayload;
import net.ragdot.gestaltresonance.network.ToggleGuardModePayload;
import net.ragdot.gestaltresonance.network.ToggleLedgeGrabPayload;
import net.ragdot.gestaltresonance.network.UsePowerPayload;
import net.ragdot.gestaltresonance.network.DashGuardPunchPayload;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.ragdot.gestaltresonance.client.gui.StaminaHudRenderer;
import org.lwjgl.glfw.GLFW;
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.client.model.ScorchedUtopiaModel;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.PufferfishEntity;
import net.ragdot.gestaltresonance.entities.CiriceEntity;
import net.ragdot.gestaltresonance.entities.AmenBreak;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;
import net.ragdot.gestaltresonance.entities.Spillways;
import net.ragdot.gestaltresonance.block.ModBlocks;
import net.ragdot.gestaltresonance.projectile.PopBud;
import net.ragdot.gestaltresonance.client.model.PopBudModel;
import net.minecraft.util.math.MathHelper;

public class GestaltresonanceClient implements ClientModInitializer {

    // Client-side key binding: “summon / dismiss Gestalt”
    private static KeyBinding summonGestaltKey;
    private static KeyBinding power1Key;
    private static KeyBinding power2Key;
    private static KeyBinding power3Key;
    
    private static boolean wasSpacePressedLastTick = false;
    private static boolean wasOnGroundLastTick = true;
    private static boolean wasAttackPressedLastTick = false;

    @Override
    public void onInitializeClient() {
        // === Keybind setup ===
        String category = "category.gestaltresonance.gestalt";
        
        summonGestaltKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.gestaltresonance.summon_gestalt", // translation key
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,                         // default key: G
                        category      // controls category
                )
        );

        power1Key = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.gestaltresonance.power1",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_Z,
                        category
                )
        );

        power2Key = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.gestaltresonance.power2",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_X,
                        category
                )
        );

        power3Key = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.gestaltresonance.power3",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_C,
                        category
                )
        );

        // Detect right-click + crouch (hold-to-guard)
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                FuturamaClientManager.tick();
            }
            if (client.player == null || client.options == null) return;
            IGestaltPlayer gestaltPlayer = (IGestaltPlayer) client.player;

            boolean isRightClickPressed = client.options.useKey.isPressed();
            boolean isAttackPressed = client.options.attackKey.isPressed();
            boolean isSneaking = client.player.isSneaking();

            boolean isCurrentlyGuarding = gestaltPlayer.gestaltresonance$isGuarding();
            boolean isIncapacitated = gestaltPlayer.gestaltresonance$isIncapacitated();
            boolean shouldBeGuarding;

            if (isIncapacitated) {
                shouldBeGuarding = false;
            } else if (isCurrentlyGuarding) {
                // If we are already guarding, stay in guard mode as long as right click is held
                shouldBeGuarding = isRightClickPressed;
            } else {
                // To start guarding, we need both right click and sneak
                shouldBeGuarding = isRightClickPressed && isSneaking;
            }

            if (shouldBeGuarding != isCurrentlyGuarding) {
                gestaltPlayer.gestaltresonance$setGuarding(shouldBeGuarding);
                ClientPlayNetworking.send(new ToggleGuardModePayload(shouldBeGuarding));
                if (shouldBeGuarding) {
                    client.player.setSprinting(false);
                }
            }

            // Trigger universal guard dash-punch: on left-click while guarding
            // Do NOT cancel guard client-side; let the server decide based on stamina.
            if (isCurrentlyGuarding && isAttackPressed && !wasAttackPressedLastTick) {
                ClientPlayNetworking.send(new DashGuardPunchPayload());
            }

            wasAttackPressedLastTick = isAttackPressed;
        });

        // Duplicated guard detection removed.

        // Each client tick, check if the key was pressed and send a packet to the server
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && ((IGestaltPlayer) client.player).gestaltresonance$isIncapacitated()) {
                return;
            }

            while (summonGestaltKey.wasPressed()) {
                if (client.player != null && client.world != null) {
                    ClientPlayNetworking.send(new ToggleGestaltSummonPayload());
                }
            }

            while (power1Key.wasPressed()) {
                if (client.player != null && client.world != null) {
                    ClientPlayNetworking.send(new UsePowerPayload(0));
                }
            }

            while (power2Key.wasPressed()) {
                if (client.player != null && client.world != null) {
                    ClientPlayNetworking.send(new UsePowerPayload(1));
                }
            }

            while (power3Key.wasPressed()) {
                if (client.player != null && client.world != null) {
                    ClientPlayNetworking.send(new UsePowerPayload(2));
                }
            }

            // Ledge grab logic
            if (client.player != null && client.options != null && client.world != null) {
                IGestaltPlayer gestaltPlayer = (IGestaltPlayer) client.player;
                
                // Ledge grab logic
                boolean isSpacePressed = client.options.jumpKey.isPressed();
                boolean wasOnGround = client.player.isOnGround();
                
                boolean isLedgeGrabbing = gestaltPlayer.gestaltresonance$isLedgeGrabbing();
                boolean isInAir = !wasOnGround && !client.player.getAbilities().flying;
                // Ledge grab should only activate if spacebar is triggered mid-air (was not on ground last tick either)
                if (isSpacePressed && !wasSpacePressedLastTick && isInAir && !wasOnGroundLastTick && !isLedgeGrabbing) {
                    // Only allow if Gestalt is active
                    boolean hasActiveGestalt = !client.world.getEntitiesByClass(
                            GestaltBase.class,
                            client.player.getBoundingBox().expand(256.0),
                            stand -> {
                                var uuid = stand.getOwnerUuid();
                                return uuid != null && uuid.equals(client.player.getUuid());
                            }
                    ).isEmpty();

                    if (hasActiveGestalt) {
                        // Try to find a ledge
                        net.minecraft.util.hit.HitResult hit = client.player.raycast(2.5, 0.0f, false);
                        if (hit.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                            net.minecraft.util.hit.BlockHitResult blockHit = (net.minecraft.util.hit.BlockHitResult) hit;
                            net.minecraft.util.math.BlockPos pos = blockHit.getBlockPos();
                            net.minecraft.util.math.Direction side = blockHit.getSide();
                            
                            if (client.world.getBlockState(pos.up()).isAir() && side.getAxis().isHorizontal()) {
                                // Valid ledge!
                                gestaltPlayer.gestaltresonance$setLedgeGrabbing(true);
                                gestaltPlayer.gestaltresonance$setLedgeGrabPos(pos);
                                gestaltPlayer.gestaltresonance$setLedgeGrabSide(side);

                                ClientPlayNetworking.send(new ToggleLedgeGrabPayload(true, java.util.Optional.of(pos), java.util.Optional.of(side)));
                            }
                        }
                    }
                } else if (isLedgeGrabbing && !isSpacePressed) {
                    // Release ledge grab
                    gestaltPlayer.gestaltresonance$setLedgeGrabbing(false);
                    ClientPlayNetworking.send(new ToggleLedgeGrabPayload(false, java.util.Optional.empty(), java.util.Optional.empty()));
                }
                
                wasSpacePressedLastTick = isSpacePressed;
                wasOnGroundLastTick = wasOnGround;
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(FuturamaSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> FuturamaClientManager.handleSync(payload));
        });

        ClientPlayNetworking.registerGlobalReceiver(FuturamaRecordingPayload.ID, (payload, context) -> {
            context.client().execute(() -> FuturamaClientManager.handleRecording(payload));
        });

        // === Renderer / model setup ===

        // Ensure correct transparency for flower-like blocks and water-surface pad
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POPSPROUT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POP_PAD, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CIRICE_BLOCK, RenderLayer.getTranslucent());

        EntityRendererRegistry.register(Gestaltresonance.TEARS_FOR_FEARS, TearsForFearsRenderer::new);

        // 1) register model layer for ScorchedUtopia Blockbench model
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.SCORCHED_UTOPIA,
                ScorchedUtopiaModel::getTexturedModelData
        );

        // 2) base / default gestalt uses vanilla biped
        EntityRendererRegistry.register(
                Gestaltresonance.GESTALT_BASE_ENTITY_TYPE,
                ctx -> new GestaltRenderer<>(ctx, "gestalt")
        );

        // 3) ScorchedUtopia uses custom Blockbench model (all tiers share renderer for now)
        EntityRendererRegistry.register(
                Gestaltresonance.SCORCHED_UTOPIA,
                ScorchedUtopiaRenderer::new
        );
        EntityRendererRegistry.register(
                Gestaltresonance.SCORCHED_UTOPIA_II,
                ScorchedUtopiaRenderer::new
        );
        EntityRendererRegistry.register(
                Gestaltresonance.SCORCHED_UTOPIA_III,
                ScorchedUtopiaRenderer::new
        );

        // 4) AmenBreak uses custom Blockbench model
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.AMEN_BREAK,
                AmenBreakModel::getTexturedModelData
        );
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.AMEN_BREAK_II,
                AmenBreakIIModel::getTexturedModelData
        );
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.AMEN_BREAK_III,
                AmenBreakIIIModel::getTexturedModelData
        );

        EntityRendererRegistry.register(
                Gestaltresonance.AMEN_BREAK,
                AmenBreakRenderer::new
        );
        EntityRendererRegistry.register(
                Gestaltresonance.AMEN_BREAK_II,
                AmenBreakRenderer::new
        );
        EntityRendererRegistry.register(
                Gestaltresonance.AMEN_BREAK_III,
                AmenBreakRenderer::new
        );

        // 5) Spillways uses custom Blockbench model
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.SPILLWAYS,
                SpillwaysModel::getTexturedModelData
        );

        EntityRendererRegistry.register(
                Gestaltresonance.SPILLWAYS,
                SpillwaysRenderer::new
        );
        EntityRendererRegistry.register(
                Gestaltresonance.SPILLWAYS_II,
                SpillwaysRenderer::new
        );
        EntityRendererRegistry.register(
                Gestaltresonance.SPILLWAYS_III,
                SpillwaysRenderer::new
        );

        // 6) Pop Bud projectile renderer/model
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.POP_BUD,
                PopBudModel::getTexturedModelData
        );

        EntityRendererRegistry.register(
                Gestaltresonance.POP_BUD,
                PopBudRenderer::new
        );

        EntityRendererRegistry.register(
                Gestaltresonance.POP_SPROUT,
                PopSproutRenderer::new
        );

        HudRenderCallback.EVENT.register(new StaminaHudRenderer());

        EntityRendererRegistry.register(
                Gestaltresonance.CIRICE_ENTITY,
                CiriceRenderer::new
        );
    }

    // ===== Renderer for Cirice Entity (Water Sphere + Pufferfish) =====
    public static class CiriceRenderer extends net.minecraft.client.render.entity.EntityRenderer<CiriceEntity> {
        private final EntityModel<PufferfishEntity> pufferModelSmall;
        private final EntityModel<PufferfishEntity> pufferModelMedium;
        private final EntityModel<PufferfishEntity> pufferModelLarge;
        private static final Identifier PUFFER_TEXTURE = Identifier.of("minecraft", "textures/entity/fish/pufferfish.png");
        private static final Identifier WATER_TEXTURE = Identifier.of(Gestaltresonance.MOD_ID, "textures/block/cirice_water.png");

        public CiriceRenderer(EntityRendererFactory.Context ctx) {
            super(ctx);
            this.pufferModelSmall = new net.minecraft.client.render.entity.model.EntityModel<PufferfishEntity>() {
                private final net.minecraft.client.model.ModelPart root = ctx.getPart(EntityModelLayers.PUFFERFISH_SMALL);
                @Override public void setAngles(PufferfishEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}
                @Override public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
                    root.render(matrices, vertices, light, overlay, color);
                }
            };
            this.pufferModelMedium = new net.minecraft.client.render.entity.model.EntityModel<PufferfishEntity>() {
                private final net.minecraft.client.model.ModelPart root = ctx.getPart(EntityModelLayers.PUFFERFISH_MEDIUM);
                @Override public void setAngles(PufferfishEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}
                @Override public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
                    root.render(matrices, vertices, light, overlay, color);
                }
            };
            this.pufferModelLarge = new net.minecraft.client.render.entity.model.EntityModel<PufferfishEntity>() {
                private final net.minecraft.client.model.ModelPart root = ctx.getPart(EntityModelLayers.PUFFERFISH_BIG);
                @Override public void setAngles(PufferfishEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}
                @Override public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
                    root.render(matrices, vertices, light, overlay, color);
                }
            };
        }

        @Override
        public void render(CiriceEntity entity, float entityYaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            super.render(entity, entityYaw, tickDelta, matrices, vertexConsumers, light);
            
            float age = (float)entity.age + tickDelta;
            float pulseFreq = (float)Math.PI * 0.2f;
            float pulse = MathHelper.sin(age * pulseFreq);
            float alpha = 0.4f + pulse * 0.1f; 
            float boost = 1.0f + (pulse + 1.0f) * 0.2f;

            // Render 10x10 Sphere (radius 5)
            matrices.push();
            VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(WATER_TEXTURE));
            renderSphere(matrices, vc, 5.0f, age, alpha, boost);
            // Render a second slightly larger layer with offset distortion
            renderSphere(matrices, vc, 5.1f, age + 50.0f, alpha * 0.5f, boost);
            matrices.pop();

            // Render pufferfish
            for (int i = 0; i < 3; i++) {
                Vec3d fishPos = entity.getPufferfishPos(i);
                matrices.push();
                matrices.translate(fishPos.x - entity.getX(), fishPos.y - entity.getY(), fishPos.z - entity.getZ());
                
                // Rotations to make it look slightly more natural
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(age * 0.1f + i) * 360f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(age * 0.05f + i) * 20f));

                float size = 0.6f;
                matrices.scale(size, size, size);
                
                VertexConsumer vcFish = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(PUFFER_TEXTURE));
                
                // Switch model based on pulse to mimic pufferfish behavior (or just pick one)
                // Let's make them "puff" up periodically
                EntityModel<PufferfishEntity> model;
                float puff = MathHelper.sin(age * 0.05f + i * 2f);
                if (puff > 0.7f) {
                    model = pufferModelLarge;
                } else if (puff > 0.3f) {
                    model = pufferModelMedium;
                } else {
                    model = pufferModelSmall;
                }
                
                model.render(matrices, vcFish, light, OverlayTexture.DEFAULT_UV, -1);
                matrices.pop();
            }
        }

        private void renderSphere(MatrixStack matrices, VertexConsumer vc, float radius, float age, float alpha, float boost) {
            int segments = 16;
            MatrixStack.Entry entry = matrices.peek();
            int a = (int)(alpha * 255.0f);
            int rgb = MathHelper.clamp((int)(255.0f * boost), 0, 255);
            int light = 15728880;

            for (int i = 0; i < segments; i++) {
                float lat0 = (float)Math.PI * (-0.5f + (float)i / segments);
                float z0 = (float)Math.sin(lat0) * radius;
                float r0 = (float)Math.cos(lat0) * radius;

                float lat1 = (float)Math.PI * (-0.5f + (float)(i + 1) / segments);
                float z1 = (float)Math.sin(lat1) * radius;
                float r1 = (float)Math.cos(lat1) * radius;

                for (int j = 0; j <= segments; j++) {
                    float lng = (float)(2.0 * Math.PI * (float)j / segments);
                    float x = (float)Math.cos(lng);
                    float y = (float)Math.sin(lng);

                    // Apply UV distortion
                    float uDist = MathHelper.sin(age * 0.4f) * 0.01f;
                    float vDist = MathHelper.cos(age * 0.5f) * 0.01f;
                    
                    float u = (float)j / segments + uDist;
                    float v = (float)i / segments + vDist;

                    vc.vertex(entry, x * r0, y * r0, z0).color(rgb, rgb, rgb, a).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, x, y, (float)Math.sin(lat0));
                    vc.vertex(entry, x * r1, y * r1, z1).color(rgb, rgb, rgb, a).texture(u, v + 1.0f/segments).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, x, y, (float)Math.sin(lat1));
                }
            }
        }

        @Override
        public Identifier getTexture(CiriceEntity entity) {
            return PUFFER_TEXTURE;
        }
    }

    // ===== Renderer for Pop Sprout =====
    public static class PopSproutRenderer extends MobEntityRenderer<net.ragdot.gestaltresonance.entities.PopSprout, BipedEntityModel<net.ragdot.gestaltresonance.entities.PopSprout>> {
        private static final Identifier TEXTURE = Identifier.of(Gestaltresonance.MOD_ID, "textures/entity/pop_sprout.png");

        public PopSproutRenderer(EntityRendererFactory.Context ctx) {
            super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)), 0.25f);
        }

        @Override
        public Identifier getTexture(net.ragdot.gestaltresonance.entities.PopSprout entity) {
            return TEXTURE;
        }
    }

    // ===== Generic renderer for base Gestalten (Biped model) =====
    public static class GestaltRenderer<T extends GestaltBase> extends MobEntityRenderer<T, BipedEntityModel<T>> {

        private final Identifier texture;

        public GestaltRenderer(EntityRendererFactory.Context ctx, String textureName) {
            super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)), 0.5f);
            this.texture = Identifier.of(
                    Gestaltresonance.MOD_ID,
                    "textures/entity/" + textureName + ".png"
            );
        }

        @Override
        public void render(T entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }

        @Override
        protected int getBlockLight(T entity, net.minecraft.util.math.BlockPos pos) {
            return super.getBlockLight(entity, pos);
        }

        @Override
        protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale) {
            super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta, scale);
        }

        @Override
        protected RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline) {
            if (isBlockingFirstPersonView(entity)) {
                return RenderLayer.getEntityTranslucent(this.getTexture(entity));
            }
            return super.getRenderLayer(entity, showBody, translucent, showOutline);
        }

        public static <T extends GestaltBase> boolean isBlockingFirstPersonView(T stand) {
            PlayerEntity owner = stand.getOwner();
            if (owner == null) return false;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != owner) return false;
            if (!client.options.getPerspective().isFirstPerson()) return false;

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

        private static Identifier resolveTexture(ScorchedUtopia entity) {
            // Use the entity's Gestalt id path to pick tier-specific texture
            String path = entity.getGestaltId().getPath();
            return Identifier.of(Gestaltresonance.MOD_ID, "textures/entity/" + path + ".png");
        }

        public ScorchedUtopiaRenderer(EntityRendererFactory.Context ctx) {
            super(ctx,
                    new ScorchedUtopiaModel(ctx.getPart(ModModelLayers.SCORCHED_UTOPIA)),
                    0.5f
            );
        }

        @Override
        public void render(ScorchedUtopia entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }

        @Override
        protected RenderLayer getRenderLayer(ScorchedUtopia entity, boolean showBody, boolean translucent, boolean showOutline) {
            if (GestaltRenderer.isBlockingFirstPersonView(entity)) {
                return RenderLayer.getEntityTranslucent(resolveTexture(entity));
            }
            return super.getRenderLayer(entity, showBody, translucent, showOutline);
        }

        @Override
        public Identifier getTexture(ScorchedUtopia entity) {
            return resolveTexture(entity);
        }
    }

    // ===== Renderer that uses Blockbench model for AmenBreak =====
    public static class AmenBreakRenderer
            extends MobEntityRenderer<AmenBreak, SinglePartEntityModel<AmenBreak>> {

        private final AmenBreakModel modelI;
        private final AmenBreakIIModel modelII;
        private final AmenBreakIIIModel modelIII;

        private static Identifier resolveTexture(AmenBreak entity) {
            String path = entity.getGestaltId().getPath();
            return Identifier.of(Gestaltresonance.MOD_ID, "textures/entity/" + path + ".png");
        }

        public AmenBreakRenderer(EntityRendererFactory.Context ctx) {
            super(ctx,
                    new AmenBreakModel(ctx.getPart(ModModelLayers.AMEN_BREAK)),
                    0.5f
            );
            this.modelI = (AmenBreakModel) this.getModel();
            this.modelII = new AmenBreakIIModel(ctx.getPart(ModModelLayers.AMEN_BREAK_II));
            this.modelIII = new AmenBreakIIIModel(ctx.getPart(ModModelLayers.AMEN_BREAK_III));
        }

        @Override
        public void render(AmenBreak entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            String path = entity.getGestaltId().getPath();
            if (path.contains("_iii")) {
                this.model = modelIII;
            } else if (path.contains("_ii")) {
                this.model = modelII;
            } else {
                this.model = modelI;
            }
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }

        @Override
        protected RenderLayer getRenderLayer(AmenBreak entity, boolean showBody, boolean translucent, boolean showOutline) {
            if (GestaltRenderer.isBlockingFirstPersonView(entity)) {
                return RenderLayer.getEntityTranslucent(resolveTexture(entity));
            }
            return super.getRenderLayer(entity, showBody, translucent, showOutline);
        }

        @Override
        public Identifier getTexture(AmenBreak entity) {
            return resolveTexture(entity);
        }
    }

    // ===== Renderer that uses Blockbench model for Spillways =====
    public static class SpillwaysRenderer
            extends MobEntityRenderer<Spillways, SpillwaysModel> {

        private static Identifier resolveTexture(Spillways entity) {
            String path = entity.getGestaltId().getPath();
            return Identifier.of(Gestaltresonance.MOD_ID, "textures/entity/" + path + ".png");
        }

        public SpillwaysRenderer(EntityRendererFactory.Context ctx) {
            super(ctx,
                    new SpillwaysModel(ctx.getPart(ModModelLayers.SPILLWAYS)),
                    0.5f
            );
        }

        @Override
        public void render(Spillways entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }

        @Override
        protected RenderLayer getRenderLayer(Spillways entity, boolean showBody, boolean translucent, boolean showOutline) {
            if (GestaltRenderer.isBlockingFirstPersonView(entity)) {
                return RenderLayer.getEntityTranslucent(resolveTexture(entity));
            }
            return super.getRenderLayer(entity, showBody, translucent, showOutline);
        }

        @Override
        public Identifier getTexture(Spillways entity) {
            return resolveTexture(entity);
        }
    }

    // ===== Renderer for Tears for Fears bubble =====
    public static class TearsForFearsRenderer extends net.minecraft.client.render.entity.EntityRenderer<net.ragdot.gestaltresonance.entities.TearsForFearsEntity> {
        private static final Identifier TEXTURE = Identifier.of(
                Gestaltresonance.MOD_ID,
                "textures/entity/tears_for_fears.png"
        );
        private static final Identifier EFFECT_TEXTURE = Identifier.of(
                Gestaltresonance.MOD_ID,
                "textures/entity/tears_for_fears_effect.png"
        );

        public TearsForFearsRenderer(EntityRendererFactory.Context ctx) {
            super(ctx);
        }

        @Override
        public void render(net.ragdot.gestaltresonance.entities.TearsForFearsEntity entity, float entityYaw, float tickDelta, MatrixStack matrices,
                           VertexConsumerProvider vertexConsumers, int light) {
            matrices.push();

            float age = (float)entity.age + tickDelta;
            
            // Billboard effect: rotate to face camera
            matrices.multiply(this.dispatcher.getRotation());
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));

            float scale = 0.5f;
            matrices.scale(scale, scale, scale);

            MatrixStack.Entry entry = matrices.peek();

            // Opacity: 50% to 80% every 10 ticks
            // 10 ticks = full cycle (5 ticks up, 5 ticks down) -> frequency = 2*PI / 10
            float pulseFreq = (float)Math.PI * 0.2f;
            float pulse = MathHelper.sin(age * pulseFreq);
            
            float alpha = 0.65f + pulse * 0.15f; // Oscillates between 0.5 and 0.8
            
            // Saturation/Brightness boost: let's say 1.0 to 1.4
            float boost = 1.0f + (pulse + 1.0f) * 0.2f; // pulse is -1 to 1, so (pulse+1) is 0 to 2. boost is 1.0 to 1.4

            // Render base texture
            net.minecraft.client.render.VertexConsumer vc1 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));
            drawDistortedQuad(entry, vc1, age, 1.0f, 1.0f, alpha, boost);
            
            // Render effect texture on top with desynced distortion
            net.minecraft.client.render.VertexConsumer vc2 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(EFFECT_TEXTURE));
            drawDistortedQuad(entry, vc2, age + 50.0f, 1.2f, 0.8f, alpha, boost);

            matrices.pop();
            // super.render(entity, entityYaw, tickDelta, matrices, vertexConsumers, light); // Intentionally skip to avoid shadow casting if possible
        }

        private void drawDistortedQuad(MatrixStack.Entry entry, net.minecraft.client.render.VertexConsumer vc, float age, float speedMult, float ampMult, float alpha, float boost) {
            float min = -0.5f;
            float max = 0.5f;

            // Toned down to 10% of original (0.1 -> 0.01)
            float uDist = MathHelper.sin(age * 0.4f * speedMult) * 0.01f * ampMult;
            float vDist = MathHelper.cos(age * 0.5f * speedMult) * 0.01f * ampMult;

            float u0 = 0.15f + uDist;
            float u1 = 0.85f + uDist;
            float v0 = 0.15f + vDist;
            float v1 = 0.85f + vDist;

            int a = (int)(alpha * 255.0f);
            int rgb = MathHelper.clamp((int)(255.0f * boost), 0, 255);
            int fullBright = 15728880;

            vc.vertex(entry, min, min, 0).color(rgb, rgb, rgb, a).texture(u0, v1).overlay(net.minecraft.client.render.OverlayTexture.DEFAULT_UV).light(fullBright).normal(entry, 0, 0, 1);
            vc.vertex(entry, max, min, 0).color(rgb, rgb, rgb, a).texture(u1, v1).overlay(net.minecraft.client.render.OverlayTexture.DEFAULT_UV).light(fullBright).normal(entry, 0, 0, 1);
            vc.vertex(entry, max, max, 0).color(rgb, rgb, rgb, a).texture(u1, v0).overlay(net.minecraft.client.render.OverlayTexture.DEFAULT_UV).light(fullBright).normal(entry, 0, 0, 1);
            vc.vertex(entry, min, max, 0).color(rgb, rgb, rgb, a).texture(u0, v0).overlay(net.minecraft.client.render.OverlayTexture.DEFAULT_UV).light(fullBright).normal(entry, 0, 0, 1);
        }

        @Override
        public Identifier getTexture(net.ragdot.gestaltresonance.entities.TearsForFearsEntity entity) {
            return TEXTURE;
        }
    }

    // ===== Renderer for Pop Bud projectile using Blockbench model =====
    public static class PopBudRenderer extends net.minecraft.client.render.entity.EntityRenderer<PopBud> {
        private static final Identifier TEXTURE = Identifier.of(
                Gestaltresonance.MOD_ID,
                "textures/projectile/popbud_texture.png"
        );
        private final PopBudModel model;

        public PopBudRenderer(EntityRendererFactory.Context ctx) {
            super(ctx);
            this.model = new PopBudModel(ctx.getPart(ModModelLayers.POP_BUD));
        }

        @Override
        public void render(PopBud entity, float entityYaw, float tickDelta, MatrixStack matrices,
                           VertexConsumerProvider vertexConsumers, int light) {
            matrices.push();
            // Orient the model so its TOP faces forward along the flight direction.
            // 1) Yaw to face camera-forward, 2) Pitch to align with trajectory,
            // 3) Rotate -90° around X so model's +Y (top) points forward (-Z in render space).
            float pitch = entity.getPitch(tickDelta);
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - entityYaw));
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(-pitch));
            matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));

            // If floating on a liquid, stop spinning and apply a gentle bobbing motion.
            if (entity.isFloating()) {
                // Bobbing: amplitude ~0.03 blocks, period ~24 ticks
                float t = (entity.age + tickDelta);
                float bob = MathHelper.sin(t * (2.0f * (float)Math.PI / 24.0f)) * 0.03f;
                matrices.translate(0.0, bob, 0.0);
            } else {
                // Spin around local Z axis while airborne
                if (!entity.isOnGround()) {
                    // ~1 rotation every 5 ticks => 72 deg per tick
                    float spinDegreesPerTick = 72.0f;
                    float age = (float) entity.age + tickDelta;
                    float roll = (age * spinDegreesPerTick) % 360.0f;
                    matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(roll));
                }
            }

            // Center and scale if needed (default 1:1)
            var vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
            this.model.render(matrices, vc, light, net.minecraft.client.render.OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
            matrices.pop();
            super.render(entity, entityYaw, tickDelta, matrices, vertexConsumers, light);
        }

        @Override
        public Identifier getTexture(PopBud entity) {
            return TEXTURE;
        }
    }
}