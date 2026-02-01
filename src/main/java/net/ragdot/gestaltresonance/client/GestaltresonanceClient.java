package net.ragdot.gestaltresonance.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.ragdot.gestaltresonance.client.model.AmenBreakModel;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.ragdot.gestaltresonance.network.ToggleGestaltSummonPayload;
import net.ragdot.gestaltresonance.network.ToggleGuardModePayload;
import net.ragdot.gestaltresonance.network.ToggleLedgeGrabPayload;
import net.ragdot.gestaltresonance.network.UsePowerPayload;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.ragdot.gestaltresonance.client.gui.StaminaHudRenderer;
import org.lwjgl.glfw.GLFW;
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.client.model.ScorchedUtopiaModel;
import net.ragdot.gestaltresonance.entities.AmenBreak;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;

public class GestaltresonanceClient implements ClientModInitializer {

    // Client-side key binding: “summon / dismiss Gestalt”
    private static KeyBinding summonGestaltKey;
    private static KeyBinding power1Key;
    private static KeyBinding power2Key;
    private static KeyBinding power3Key;
    
    private static boolean wasSpacePressedLastTick = false;
    private static boolean wasOnGroundLastTick = true;

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
            if (client.player == null || client.options == null) return;
            IGestaltPlayer gestaltPlayer = (IGestaltPlayer) client.player;

            boolean isRightClickPressed = client.options.useKey.isPressed();
            boolean isSneaking = client.player.isSneaking();

            boolean isCurrentlyGuarding = gestaltPlayer.gestaltresonance$isGuarding();
            boolean shouldBeGuarding;

            if (isCurrentlyGuarding) {
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
        });

        // Detect right-click + crouch (hold-to-guard)
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.options == null) return;
            IGestaltPlayer gestaltPlayer = (IGestaltPlayer) client.player;

            boolean isRightClickPressed = client.options.useKey.isPressed();
            boolean isSneaking = client.player.isSneaking();

            boolean isCurrentlyGuarding = gestaltPlayer.gestaltresonance$isGuarding();
            boolean shouldBeGuarding;

            if (isCurrentlyGuarding) {
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
        });

        // Each client tick, check if the key was pressed and send a packet to the server
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                IGestaltPlayer gestaltPlayer = (IGestaltPlayer) client.player;
                int cooldown = gestaltPlayer.gestaltresonance$getLedgeGrabCooldown();
                if (cooldown > 0) {
                    gestaltPlayer.gestaltresonance$setLedgeGrabCooldown(cooldown - 1);
                }
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
            if (client.player != null && client.options != null) {
                IGestaltPlayer gestaltPlayer = (IGestaltPlayer) client.player;
                
                // Ledge grab logic
                boolean isSpacePressed = client.options.jumpKey.isPressed();
                boolean wasOnGround = client.player.isOnGround();
                
                boolean isLedgeGrabbing = gestaltPlayer.gestaltresonance$isLedgeGrabbing();
                boolean isInAir = !wasOnGround && !client.player.getAbilities().flying;
                boolean isCooldownActive = gestaltPlayer.gestaltresonance$getLedgeGrabCooldown() > 0;
                
                // Ledge grab should only activate if spacebar is triggered mid-air (was not on ground last tick either)
                if (isSpacePressed && !wasSpacePressedLastTick && isInAir && !wasOnGroundLastTick && !isLedgeGrabbing && !isCooldownActive) {
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
                    gestaltPlayer.gestaltresonance$setLedgeGrabCooldown(20);
                    ClientPlayNetworking.send(new ToggleLedgeGrabPayload(false, java.util.Optional.empty(), java.util.Optional.empty()));
                }
                
                wasSpacePressedLastTick = isSpacePressed;
                wasOnGroundLastTick = wasOnGround;
            }
        });

        // === Renderer / model setup ===

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

        // 3) ScorchedUtopia uses custom Blockbench model
        EntityRendererRegistry.register(
                Gestaltresonance.SCORCHED_UTOPIA,
                ScorchedUtopiaRenderer::new
        );

        // 4) AmenBreak uses custom Blockbench model
        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.AMEN_BREAK,
                AmenBreakModel::getTexturedModelData
        );

        EntityRendererRegistry.register(
                Gestaltresonance.AMEN_BREAK,
                AmenBreakRenderer::new
        );

        HudRenderCallback.EVENT.register(new StaminaHudRenderer());
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
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }

        @Override
        protected RenderLayer getRenderLayer(ScorchedUtopia entity, boolean showBody, boolean translucent, boolean showOutline) {
            if (GestaltRenderer.isBlockingFirstPersonView(entity)) {
                return RenderLayer.getEntityTranslucent(TEXTURE);
            }
            return super.getRenderLayer(entity, showBody, translucent, showOutline);
        }

        @Override
        public Identifier getTexture(ScorchedUtopia entity) {
            return TEXTURE;
        }
    }

    // ===== Renderer that uses Blockbench model for AmenBreak =====
    public static class AmenBreakRenderer
            extends MobEntityRenderer<AmenBreak, AmenBreakModel> {

        private static final Identifier TEXTURE = Identifier.of(
                Gestaltresonance.MOD_ID,
                "textures/entity/amen_break.png"
        );

        public AmenBreakRenderer(EntityRendererFactory.Context ctx) {
            super(ctx,
                    new AmenBreakModel(ctx.getPart(ModModelLayers.AMEN_BREAK)),
                    0.5f
            );
        }

        @Override
        public void render(AmenBreak entity, float yaw, float tickDelta,
                           MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }

        @Override
        protected RenderLayer getRenderLayer(AmenBreak entity, boolean showBody, boolean translucent, boolean showOutline) {
            if (GestaltRenderer.isBlockingFirstPersonView(entity)) {
                return RenderLayer.getEntityTranslucent(TEXTURE);
            }
            return super.getRenderLayer(entity, showBody, translucent, showOutline);
        }

        @Override
        public Identifier getTexture(AmenBreak entity) {
            return TEXTURE;
        }
    }
}