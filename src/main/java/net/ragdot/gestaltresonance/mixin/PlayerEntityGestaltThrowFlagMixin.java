package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityGestaltThrowFlagMixin implements IGestaltPlayer {

    @Unique
    private boolean gestaltresonance$gestaltThrowActive = false;

    @Unique
    private boolean gestaltresonance$guarding = false;

    @Unique
    private boolean gestaltresonance$ledgeGrabbing = false;

    @Unique
    private net.minecraft.util.math.BlockPos gestaltresonance$ledgeGrabPos = null;

    @Unique
    private net.minecraft.util.math.Vec3d gestaltresonance$ledgeGrabGestaltPos = null;

    @Unique
    private float gestaltresonance$ledgeGrabGestaltYaw = 0;

    @Unique
    private int gestaltresonance$ledgeGrabCooldown = 0;

    @Unique
    private boolean gestaltresonance$redirectionActive = false;

    @Override
    public void gestaltresonance$setGestaltThrowActive(boolean active) {
        this.gestaltresonance$gestaltThrowActive = active;
    }

    @Override
    public boolean gestaltresonance$isGestaltThrowActive() {
        return this.gestaltresonance$gestaltThrowActive;
    }

    @Override
    public void gestaltresonance$setGuarding(boolean guarding) {
        this.gestaltresonance$guarding = guarding;
    }

    @Override
    public boolean gestaltresonance$isGuarding() {
        return this.gestaltresonance$guarding;
    }

    @Override
    public void gestaltresonance$setLedgeGrabbing(boolean grabbing) {
        this.gestaltresonance$ledgeGrabbing = grabbing;
    }

    @Override
    public boolean gestaltresonance$isLedgeGrabbing() {
        return this.gestaltresonance$ledgeGrabbing;
    }

    @Override
    public void gestaltresonance$setLedgeGrabPos(net.minecraft.util.math.BlockPos pos) {
        this.gestaltresonance$ledgeGrabPos = pos;
    }

    @Override
    public net.minecraft.util.math.BlockPos gestaltresonance$getLedgeGrabPos() {
        return this.gestaltresonance$ledgeGrabPos;
    }

    @Override
    public void gestaltresonance$setLedgeGrabGestaltPos(net.minecraft.util.math.Vec3d pos) {
        this.gestaltresonance$ledgeGrabGestaltPos = pos;
    }

    @Override
    public net.minecraft.util.math.Vec3d gestaltresonance$getLedgeGrabGestaltPos() {
        return this.gestaltresonance$ledgeGrabGestaltPos;
    }

    @Override
    public void gestaltresonance$setLedgeGrabGestaltYaw(float yaw) {
        this.gestaltresonance$ledgeGrabGestaltYaw = yaw;
    }

    @Override
    public float gestaltresonance$getLedgeGrabGestaltYaw() {
        return this.gestaltresonance$ledgeGrabGestaltYaw;
    }

    @Override
    public void gestaltresonance$setLedgeGrabCooldown(int ticks) {
        this.gestaltresonance$ledgeGrabCooldown = ticks;
    }

    @Override
    public int gestaltresonance$getLedgeGrabCooldown() {
        return this.gestaltresonance$ledgeGrabCooldown;
    }

    @Override
    public void gestaltresonance$setRedirectionActive(boolean active) {
        this.gestaltresonance$redirectionActive = active;
    }

    @Override
    public boolean gestaltresonance$isRedirectionActive() {
        return this.gestaltresonance$redirectionActive;
    }
}
