package net.ragdot.gestaltresonance.mixin.access;

import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PostEffectPass.class)
public interface PostEffectPassAccessor {
    @Accessor("program")
    JsonEffectShaderProgram gestaltresonance$getProgram();
}
