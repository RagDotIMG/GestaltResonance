package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltJumpPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityGestaltJumpFlagMixin implements IGestaltJumpPlayer {

    @Unique
    private boolean gestaltresonance$gestaltJumpActive = false;

    @Override
    public void gestaltresonance$setGestaltJumpActive(boolean active) {
        this.gestaltresonance$gestaltJumpActive = active;
    }

    @Override
    public boolean gestaltresonance$isGestaltJumpActive() {
        return this.gestaltresonance$gestaltJumpActive;
    }
}
