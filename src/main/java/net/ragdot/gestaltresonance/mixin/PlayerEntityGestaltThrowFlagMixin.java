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
    @Unique
    private static final net.minecraft.entity.data.TrackedData<Boolean> INCAPACITATED = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private static final net.minecraft.entity.data.TrackedData<Integer> BREAK_CORE_TICS = net.minecraft.entity.data.DataTracker.registerData(PlayerEntity.class, net.minecraft.entity.data.TrackedDataHandlerRegistry.INTEGER);

    @org.spongepowered.asm.mixin.injection.Inject(method = "initDataTracker", at = @At("TAIL"))
    private void gestaltresonance$initGestaltDataTracker(net.minecraft.entity.data.DataTracker.Builder builder, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        builder.add(GESTALT_THROW_ACTIVE, false);
        builder.add(GUARDING, false);
        builder.add(LEDGE_GRABBING, false);
        builder.add(LEDGE_GRAB_POS, java.util.Optional.empty());
        builder.add(LEDGE_GRAB_SIDE, -1);
        builder.add(REDIRECTION_ACTIVE, false);
        builder.add(MUFFLED_MOVEMENT_ACTIVE, false);
        builder.add(INCAPACITATED, false);
        builder.add(BREAK_CORE_TICS, 0);
    }

    // Removed obsolete ledge grab cooldown state

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Float> gestaltresonance$staminaMap = new java.util.HashMap<>();

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Integer> gestaltresonance$expMap = new java.util.HashMap<>();

    @Unique
    private final java.util.Map<net.minecraft.util.Identifier, Integer> gestaltresonance$lvlMap = new java.util.HashMap<>();
    @Unique
    private final java.util.Map<String, Integer> gestaltresonance$powerRemainingCooldownMap = new java.util.HashMap<>();
    @Unique
    private final java.util.Map<String, Integer> gestaltresonance$powerMaxCooldownMap = new java.util.HashMap<>();

    // ===== Persistence of Gestalt stats (per-id) =====
    @org.spongepowered.asm.mixin.injection.Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void gestaltresonance$writeGestaltData(net.minecraft.nbt.NbtCompound nbt,
                                                   org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        net.minecraft.nbt.NbtCompound root = new net.minecraft.nbt.NbtCompound();

        // Stamina map
        net.minecraft.nbt.NbtCompound staminaTag = new net.minecraft.nbt.NbtCompound();
        for (var e : this.gestaltresonance$staminaMap.entrySet()) {
            staminaTag.putFloat(e.getKey().toString(), e.getValue());
        }
        root.put("stamina", staminaTag);

        // EXP map
        net.minecraft.nbt.NbtCompound expTag = new net.minecraft.nbt.NbtCompound();
        for (var e : this.gestaltresonance$expMap.entrySet()) {
            expTag.putInt(e.getKey().toString(), e.getValue());
        }
        root.put("exp", expTag);

        // LVL map
        net.minecraft.nbt.NbtCompound lvlTag = new net.minecraft.nbt.NbtCompound();
        for (var e : this.gestaltresonance$lvlMap.entrySet()) {
            lvlTag.putInt(e.getKey().toString(), e.getValue());
        }
        root.put("lvl", lvlTag);

        // Power Cooldown Remaining
        net.minecraft.nbt.NbtCompound pRemTag = new net.minecraft.nbt.NbtCompound();
        for (var e : this.gestaltresonance$powerRemainingCooldownMap.entrySet()) {
            pRemTag.putInt(e.getKey(), e.getValue());
        }
        root.put("power_cd_rem", pRemTag);

        // Power Cooldown Max
        net.minecraft.nbt.NbtCompound pMaxTag = new net.minecraft.nbt.NbtCompound();
        for (var e : this.gestaltresonance$powerMaxCooldownMap.entrySet()) {
            pMaxTag.putInt(e.getKey(), e.getValue());
        }
        root.put("power_cd_max", pMaxTag);

        nbt.put("GestaltStats", root);
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void gestaltresonance$readGestaltData(net.minecraft.nbt.NbtCompound nbt,
                                                  org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (nbt == null || !nbt.contains("GestaltStats", net.minecraft.nbt.NbtElement.COMPOUND_TYPE)) return;
        net.minecraft.nbt.NbtCompound root = nbt.getCompound("GestaltStats");

        this.gestaltresonance$staminaMap.clear();
        this.gestaltresonance$expMap.clear();
        this.gestaltresonance$lvlMap.clear();
        this.gestaltresonance$powerRemainingCooldownMap.clear();
        this.gestaltresonance$powerMaxCooldownMap.clear();

        // Stamina
        if (root.contains("stamina", net.minecraft.nbt.NbtElement.COMPOUND_TYPE)) {
            net.minecraft.nbt.NbtCompound staminaTag = root.getCompound("stamina");
            for (String key : staminaTag.getKeys()) {
                try {
                    this.gestaltresonance$staminaMap.put(net.minecraft.util.Identifier.of(key), staminaTag.getFloat(key));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        // EXP
        if (root.contains("exp", net.minecraft.nbt.NbtElement.COMPOUND_TYPE)) {
            net.minecraft.nbt.NbtCompound expTag = root.getCompound("exp");
            for (String key : expTag.getKeys()) {
                try {
                    this.gestaltresonance$expMap.put(net.minecraft.util.Identifier.of(key), expTag.getInt(key));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        // LVL
        if (root.contains("lvl", net.minecraft.nbt.NbtElement.COMPOUND_TYPE)) {
            net.minecraft.nbt.NbtCompound lvlTag = root.getCompound("lvl");
            for (String key : lvlTag.getKeys()) {
                try {
                    this.gestaltresonance$lvlMap.put(net.minecraft.util.Identifier.of(key), lvlTag.getInt(key));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        // Power Cooldown Remaining
        if (root.contains("power_cd_rem", net.minecraft.nbt.NbtElement.COMPOUND_TYPE)) {
            net.minecraft.nbt.NbtCompound pRemTag = root.getCompound("power_cd_rem");
            for (String key : pRemTag.getKeys()) {
                this.gestaltresonance$powerRemainingCooldownMap.put(key, pRemTag.getInt(key));
            }
        }

        // Power Cooldown Max
        if (root.contains("power_cd_max", net.minecraft.nbt.NbtElement.COMPOUND_TYPE)) {
            net.minecraft.nbt.NbtCompound pMaxTag = root.getCompound("power_cd_max");
            for (String key : pMaxTag.getKeys()) {
                this.gestaltresonance$powerMaxCooldownMap.put(key, pMaxTag.getInt(key));
            }
        }
    }

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

    // Cooldown mechanics removed; no-op retained method deleted


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
    public void gestaltresonance$setIncapacitated(boolean value) {
        ((PlayerEntity)(Object)this).getDataTracker().set(INCAPACITATED, value);
    }

    @Override
    public boolean gestaltresonance$isIncapacitated() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(INCAPACITATED);
    }

    @Override
    public void gestaltresonance$setBreakCoreTicks(int ticks) {
        ((PlayerEntity)(Object)this).getDataTracker().set(BREAK_CORE_TICS, ticks);
    }

    @Override
    public int gestaltresonance$getBreakCoreTicks() {
        return ((PlayerEntity)(Object)this).getDataTracker().get(BREAK_CORE_TICS);
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
    public void gestaltresonance$setGestaltPowerCooldown(net.minecraft.util.Identifier id, int powerIndex, int remainingTicks, int maxTicks) {
        String key = id.toString() + ":" + powerIndex;
        this.gestaltresonance$powerRemainingCooldownMap.put(key, remainingTicks);
        this.gestaltresonance$powerMaxCooldownMap.put(key, maxTicks);
    }

    @Override
    public int gestaltresonance$getGestaltPowerCooldownRemaining(net.minecraft.util.Identifier id, int powerIndex) {
        String key = id.toString() + ":" + powerIndex;
        return this.gestaltresonance$powerRemainingCooldownMap.getOrDefault(key, 0);
    }

    @Override
    public int gestaltresonance$getGestaltPowerCooldownMax(net.minecraft.util.Identifier id, int powerIndex) {
        String key = id.toString() + ":" + powerIndex;
        return this.gestaltresonance$powerMaxCooldownMap.getOrDefault(key, 0);
    }

    @Override
    public void gestaltresonance$resetAllGestaltData() {
        this.gestaltresonance$staminaMap.clear();
        this.gestaltresonance$expMap.clear();
        this.gestaltresonance$lvlMap.clear();
        this.gestaltresonance$powerRemainingCooldownMap.clear();
        this.gestaltresonance$powerMaxCooldownMap.clear();
    }
}
