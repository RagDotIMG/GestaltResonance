package net.ragdot.gestaltresonance.util;

public interface IGestaltPlayer {
    void gestaltresonance$setGestaltThrowActive(boolean active);
    boolean gestaltresonance$isGestaltThrowActive();

    void gestaltresonance$setGuarding(boolean guarding);
    boolean gestaltresonance$isGuarding();

    void gestaltresonance$setLedgeGrabbing(boolean grabbing);
    boolean gestaltresonance$isLedgeGrabbing();

    void gestaltresonance$setLedgeGrabPos(net.minecraft.util.math.BlockPos pos);
    net.minecraft.util.math.BlockPos gestaltresonance$getLedgeGrabPos();

    void gestaltresonance$setLedgeGrabGestaltPos(net.minecraft.util.math.Vec3d pos);
    net.minecraft.util.math.Vec3d gestaltresonance$getLedgeGrabGestaltPos();

    void gestaltresonance$setLedgeGrabGestaltYaw(float yaw);
    float gestaltresonance$getLedgeGrabGestaltYaw();

    void gestaltresonance$setLedgeGrabCooldown(int ticks);
    int gestaltresonance$getLedgeGrabCooldown();

    void gestaltresonance$setRedirectionActive(boolean active);
    boolean gestaltresonance$isRedirectionActive();
}