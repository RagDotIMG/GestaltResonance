package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class PlayerEntityGestaltThrowFlagMixin implements IGestaltPlayer {

    @Unique
    private static final net.minecraft.entity.data.TrackedData<Boolean> GESTALT_THROW_ACTIVE = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final net.minecraft.entity.data.TrackedData<Boolean> GUARDING = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final net.minecraft.entity.data.TrackedData<Boolean> LEDGE_GRABBING = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final net.minecraft.entity.data.TrackedData<java.util.Optional<net.minecraft.util.math.BlockPos>> LEDGE_GRAB_POS = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
    @Unique
    private static final net.minecraft.entity.data.TrackedData<Integer> LEDGE_GRAB_SIDE = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.INTEGER);
    @Unique
    private static final net.minecraft.entity.data.TrackedData<Boolean> REDIRECTION_ACTIVE = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final net.minecraft.entity.data.TrackedData<Boolean> MUFFLED_MOVEMENT_ACTIVE = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN);

    @org.spongepowered.asm.mixin.injection.Inject(method = "initDataTracker", at = @At("TAIL"))
    private void gestaltresonance$initGestaltDataTracker(net.minecraft.entity.data.DataTracker.Builder builder, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        builder.add(GESTALT_THROW_ACTIVE, false);
        builder.add(GUARDING, false);
        builder.add(LEDGE_GRABBING, false);
        builder.add(LEDGE_GRAB_POS, java.util.Optional.empty());
        builder.add(LEDGE_GRAB_SIDE, -1);
        builder.add(REDIRECTION_ACTIVE, false);
        builder.add(MUFFLED_MOVEMENT_ACTIVE, false);
    }

    @Unique
    private int gestaltresonance$ledgeGrabCooldown = 0;

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Float> gestaltresonance$staminaMap = new java.util.HashMap<>();

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Integer> gestaltresonance$expMap = new java.util.HashMap<>();

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Integer> gestaltresonance$lvlMap = new java.util.HashMap<>();

    @Override
    public void gestaltresonance$setGestaltThrowActive(boolean active) {
        ((PlayerEntity)(Object)this).getDataTracker().set(GESTALT_THROW_ACTIVE, active);
    }

    @Override
    public boolean gestaltresonance$isGestaltThrowActive() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(GESTALT_THROW_ACTIVE);
    }

    @Override
    public void gestaltresonance$setGuarding(boolean guarding) {
        ((PlayerEntity)(Object)this).getDataTracker().set(GUARDING, guarding);
    }

    @Override
    public boolean gestaltresonance$isGuarding() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(GUARDING);
    }

    @Override
    public void gestaltresonance$setLedgeGrabbing(boolean grabbing) {
        ((PlayerEntity)(Object)this).getDataTracker().set(LEDGE_GRABBING, grabbing);
    }

    @Override
    public boolean gestaltresonance$isLedgeGrabbing() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(LEDGE_GRABBING);
    }

    @Override
    public void gestaltresonance$setLedgeGrabPos(net.minecraft.util.math.BlockPos pos) {
        ((PlayerEntity)(Object)this).getDataTracker().set(LEDGE_GRAB_POS, java.util.Optional.ofNullable(pos));
    }

    @Override
    public net.minecraft.util.math.BlockPos gestaltresonance$getLedgeGrabPos() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(LEDGE_GRAB_POS).orElse(null);
    }

    @Override
    public void gestaltresonance$setLedgeGrabSide(net.minecraft.util.math.Direction side) {
        ((PlayerEntity)(Object)this).getDataTracker().set(LEDGE_GRAB_SIDE, side == null ? -1 : side.getId());
    }

    @Override
    public net.minecraft.util.math.Direction gestaltresonance$getLedgeGrabSide() {
        int sideId = ((PlayerEntity)(Object)this).getDataTracker().get(LEDGE_GRAB_SIDE);
        return sideId == -1 ? null : net.minecraft.util.math.Direction.byId(sideId);
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
        ((PlayerEntity)(Object)this).getDataTracker().set(REDIRECTION_ACTIVE, active);
    }

    @Override
    public boolean gestaltresonance$isRedirectionActive() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(REDIRECTION_ACTIVE);
    }

    // Amen Break passive: muffled movement flag
    @Override
    public void gestaltresonance$setMuffledMovementActive(boolean active) {
        ((PlayerEntity)(Object)this).getDataTracker().set(MUFFLED_MOVEMENT_ACTIVE, active);
    }

    @Override
    public boolean gestaltresonance$isMuffledMovementActive() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(MUFFLED_MOVEMENT_ACTIVE);
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
