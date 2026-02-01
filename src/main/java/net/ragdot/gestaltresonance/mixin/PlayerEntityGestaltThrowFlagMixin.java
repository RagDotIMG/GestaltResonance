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
    private net.minecraft.util.math.Direction gestaltresonance$ledgeGrabSide = null;

    @Unique
    private int gestaltresonance$ledgeGrabCooldown = 0;

    @Unique
    private boolean gestaltresonance$redirectionActive = false;

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Float> gestaltresonance$staminaMap = new java.util.HashMap<>();

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Integer> gestaltresonance$expMap = new java.util.HashMap<>();

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Integer> gestaltresonance$lvlMap = new java.util.HashMap<>();

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
    public void gestaltresonance$setLedgeGrabSide(net.minecraft.util.math.Direction side) {
        this.gestaltresonance$ledgeGrabSide = side;
    }

    @Override
    public net.minecraft.util.math.Direction gestaltresonance$getLedgeGrabSide() {
        return this.gestaltresonance$ledgeGrabSide;
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

    @Override
    public void gestaltresonance$setGestaltStamina(net.minecraft.util.Identifier id, float stamina) {
        this.gestaltresonance$staminaMap.put(id, stamina);
    }

    @Override
    public float gestaltresonance$getGestaltStamina(net.minecraft.util.Identifier id) {
        return this.gestaltresonance$staminaMap.getOrDefault(id, 26.0f);
    }

    @Override
    public void gestaltresonance$setGestaltExp(net.minecraft.util.Identifier id, int exp) {
        this.gestaltresonance$expMap.put(id, exp);
    }

    @Override
    public int gestaltresonance$getGestaltExp(net.minecraft.util.Identifier id) {
        return this.gestaltresonance$expMap.getOrDefault(id, 0);
    }

    @Override
    public void gestaltresonance$setGestaltLvl(net.minecraft.util.Identifier id, int lvl) {
        this.gestaltresonance$lvlMap.put(id, lvl);
    }

    @Override
    public int gestaltresonance$getGestaltLvl(net.minecraft.util.Identifier id) {
        return this.gestaltresonance$lvlMap.getOrDefault(id, 1);
    }

    @Override
    public void gestaltresonance$resetAllGestaltData() {
        this.gestaltresonance$staminaMap.clear();
        this.gestaltresonance$expMap.clear();
        this.gestaltresonance$lvlMap.clear();
    }
}
