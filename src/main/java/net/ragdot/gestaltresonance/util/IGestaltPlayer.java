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

    void gestaltresonance$setLedgeGrabSide(net.minecraft.util.math.Direction side);
    net.minecraft.util.math.Direction gestaltresonance$getLedgeGrabSide();

    void gestaltresonance$setLedgeGrabCooldown(int ticks);
    int gestaltresonance$getLedgeGrabCooldown();

    void gestaltresonance$setRedirectionActive(boolean active);
    boolean gestaltresonance$isRedirectionActive();

    void gestaltresonance$setGestaltStamina(net.minecraft.util.Identifier id, float stamina);
    float gestaltresonance$getGestaltStamina(net.minecraft.util.Identifier id);

    void gestaltresonance$setGestaltExp(net.minecraft.util.Identifier id, int exp);
    int gestaltresonance$getGestaltExp(net.minecraft.util.Identifier id);

    void gestaltresonance$setGestaltLvl(net.minecraft.util.Identifier id, int lvl);
    int gestaltresonance$getGestaltLvl(net.minecraft.util.Identifier id);

    void gestaltresonance$resetAllGestaltData();
}