package net.ragdot.gestaltresonance.mixin;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.client.FuturamaClientManager;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.PostEffectPass;
import net.ragdot.gestaltresonance.mixin.access.PostEffectProcessorAccessor;
import net.ragdot.gestaltresonance.mixin.access.PostEffectPassAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow @Final MinecraftClient client;
    @Shadow public abstract void loadPostProcessor(Identifier id);
    @Shadow public abstract void disablePostProcessor();
    @Shadow private boolean postProcessorEnabled;
    @Shadow private PostEffectProcessor postProcessor;

    @Unique
    private float gestaltresonance$shaderTime = 0.0f;

    @Unique
    private boolean gestaltresonance$wasIncapacitated = false;
    @Unique
    private boolean gestaltresonance$wasRecording = false;
    @Unique
    private boolean gestaltresonance$wasBreakCore = false;
    @Unique
    private boolean gestaltresonance$wasCirice = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void gestaltresonance$handleShaders(CallbackInfo ci) {
        if (client.player == null) return;

        boolean isIncapacitated = ((IGestaltPlayer) client.player).gestaltresonance$isIncapacitated();
        boolean isRecording = FuturamaClientManager.isRecording();
        boolean isBreakCore = ((IGestaltPlayer) client.player).gestaltresonance$getBreakCoreTicks() > 0;
        
        boolean isCirice = false;
        List<net.ragdot.gestaltresonance.entities.CiriceEntity> ciriceEntities = client.world.getEntitiesByClass(net.ragdot.gestaltresonance.entities.CiriceEntity.class, client.player.getBoundingBox().expand(10.0), e -> true);
        for (net.ragdot.gestaltresonance.entities.CiriceEntity ce : ciriceEntities) {
            if (client.player.getEyePos().distanceTo(ce.getPos()) <= 5.0) {
                isCirice = true;
                break;
            }
        }

        if (isIncapacitated != gestaltresonance$wasIncapacitated || isRecording != gestaltresonance$wasRecording || isBreakCore != gestaltresonance$wasBreakCore || isCirice != gestaltresonance$wasCirice) {
            updateShader(isIncapacitated, isRecording, isBreakCore, isCirice);
            gestaltresonance$wasIncapacitated = isIncapacitated;
            gestaltresonance$wasRecording = isRecording;
            gestaltresonance$wasBreakCore = isBreakCore;
            gestaltresonance$wasCirice = isCirice;
        } else if ((isIncapacitated || isRecording || isBreakCore || isCirice) && !this.postProcessorEnabled) {
            // Re-load if it was disabled by something else (like F4 or resizing)
            updateShader(isIncapacitated, isRecording, isBreakCore, isCirice);
        }

        if (isBreakCore || isCirice || isRecording) {
            gestaltresonance$shaderTime += 0.05f;
            if (this.postProcessorEnabled && this.postProcessor != null) {
                for (PostEffectPass pass : ((PostEffectProcessorAccessor)this.postProcessor).gestaltresonance$getPasses()) {
                    JsonEffectShaderProgram shader = ((PostEffectPassAccessor)pass).gestaltresonance$getProgram();
                    if (shader != null && shader.getUniformByName("Time") != null) {
                        shader.getUniformByName("Time").set(gestaltresonance$shaderTime);
                    }
                }
            }
        } else {
            gestaltresonance$shaderTime = 0.0f;
        }
    }

    @Unique
    private void updateShader(boolean incapacitated, boolean recording, boolean breakCore, boolean cirice) {
        if (incapacitated) {
            System.out.println("[DEBUG_LOG] Loading invert shader");
            this.loadPostProcessor(Identifier.of("shaders/post/invert.json"));
        } else if (breakCore) {
            System.out.println("[DEBUG_LOG] Loading break_core shader");
            this.loadPostProcessor(Identifier.of("shaders/post/break_core.json"));
        } else if (recording || cirice) {
            System.out.println("[DEBUG_LOG] Loading violet shader");
            this.loadPostProcessor(Identifier.of("shaders/post/violet.json"));
        } else {
            System.out.println("[DEBUG_LOG] Disabling post processor");
            this.disablePostProcessor();
        }
    }
}
