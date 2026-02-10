package net.ragdot.gestaltresonance.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.ragdot.gestaltresonance.network.GestaltThrowPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.sound.SoundEvent;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void gestaltresonance$writeGestaltData(net.minecraft.nbt.NbtCompound nbt, CallbackInfo ci) {
        IGestaltPlayer gp = (IGestaltPlayer) this;
        net.minecraft.nbt.NbtCompound gestaltData = new net.minecraft.nbt.NbtCompound();
        
        // We need a list of all known Gestalt IDs to save them.
        // For now, we can use the ones we know or just iterate over what's in the maps.
        // Actually, since the maps are private and unique to the other mixin, 
        // we can't easily iterate them here without more trickery.
        // Better: let's use the known IDs: scorched_utopia, amen_break, and the base gestalt.
        net.minecraft.util.Identifier[] ids = {
            net.minecraft.util.Identifier.of("gestaltresonance", "gestalt"),
            net.minecraft.util.Identifier.of("gestaltresonance", "scorched_utopia"),
            net.minecraft.util.Identifier.of("gestaltresonance", "amen_break")
        };

        for (net.minecraft.util.Identifier id : ids) {
            net.minecraft.nbt.NbtCompound singleGestalt = new net.minecraft.nbt.NbtCompound();
            singleGestalt.putFloat("Stamina", gp.gestaltresonance$getGestaltStamina(id));
            singleGestalt.putInt("Exp", gp.gestaltresonance$getGestaltExp(id));
            singleGestalt.putInt("Lvl", gp.gestaltresonance$getGestaltLvl(id));
            gestaltData.put(id.toString(), singleGestalt);
        }
        nbt.put("GestaltResonanceData", gestaltData);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void gestaltresonance$readGestaltData(net.minecraft.nbt.NbtCompound nbt, CallbackInfo ci) {
        IGestaltPlayer gp = (IGestaltPlayer) this;
        if (nbt.contains("GestaltResonanceData")) {
            net.minecraft.nbt.NbtCompound gestaltData = nbt.getCompound("GestaltResonanceData");
            for (String key : gestaltData.getKeys()) {
                try {
                    net.minecraft.util.Identifier id = net.minecraft.util.Identifier.of(key);
                    net.minecraft.nbt.NbtCompound singleGestalt = gestaltData.getCompound(key);
                    if (singleGestalt.contains("Stamina")) gp.gestaltresonance$setGestaltStamina(id, singleGestalt.getFloat("Stamina"));
                    if (singleGestalt.contains("Exp")) gp.gestaltresonance$setGestaltExp(id, singleGestalt.getInt("Exp"));
                    if (singleGestalt.contains("Lvl")) gp.gestaltresonance$setGestaltLvl(id, singleGestalt.getInt("Lvl"));
                } catch (Exception ignored) {}
            }
        } else {
            // Legacy loading for backward compatibility if needed, 
            // but since we just added these in previous tasks, maybe not strictly necessary.
            // Let's keep it for one version transition.
            net.minecraft.util.Identifier defaultId = net.minecraft.util.Identifier.of("gestaltresonance", "gestalt");
            if (nbt.contains("GestaltStamina")) gp.gestaltresonance$setGestaltStamina(defaultId, nbt.getFloat("GestaltStamina"));
            if (nbt.contains("GestaltExp")) gp.gestaltresonance$setGestaltExp(defaultId, nbt.getInt("GestaltExp"));
            if (nbt.contains("GestaltLvl")) gp.gestaltresonance$setGestaltLvl(defaultId, nbt.getInt("GestaltLvl"));
        }
    }

    @Inject(method = "playSound(Lnet/minecraft/sound/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelSoundDuringRedirection(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        if (((IGestaltPlayer) this).gestaltresonance$isRedirectionActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleStatus", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelHurtAnimationDuringRedirection(byte status, CallbackInfo ci) {
        if (status == 2 && ((IGestaltPlayer) this).gestaltresonance$isRedirectionActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void gestaltresonance$cancelTravelDuringLedgeGrab(Vec3d movementInput, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (((IGestaltPlayer) player).gestaltresonance$isLedgeGrabbing()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float gestaltresonance$modifyDamageAmount(float amount, DamageSource source) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) player;

        if (gestaltPlayer.gestaltresonance$isGuarding()) {
            // Find active gestalt to check stamina
            GestaltBase activeGestalt = player.getWorld().getEntitiesByClass(
                    GestaltBase.class,
                    player.getBoundingBox().expand(4.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            ).stream().findFirst().orElse(null);

            if (activeGestalt != null) {
                Vec3d sourcePos = source.getPosition();
                if (sourcePos != null) {
                    Vec3d playerPos = player.getPos();
                    Vec3d dirToSource = sourcePos.subtract(playerPos).normalize();
                    Vec3d playerFacing = player.getRotationVec(1.0f);

                    double dot = dirToSource.dotProduct(new Vec3d(playerFacing.x, 0, playerFacing.z).normalize());
                    if (dot > 0) {
                        activeGestalt.setStamina(activeGestalt.getStamina() - 3.0f);
                        float reduction = activeGestalt.getGuardReduction();
                        return amount * (1.0f - reduction);
                    }
                }
            }
        }
        return amount;
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void gestaltresonance$onGestaltThrow(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!player.isSneaking()) return;

        boolean hasGestalt;
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            hasGestalt = !serverWorld.getEntitiesByClass(
                    GestaltBase.class,
                    player.getBoundingBox().expand(4.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            ).isEmpty();
        } else {
            // Client-side check
            hasGestalt = !player.getWorld().getEntitiesByClass(
                    GestaltBase.class,
                    player.getBoundingBox().expand(4.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            ).isEmpty();
        }

        if (!hasGestalt) return;

        double extraY = 0.38;
        double horizontalBoost = 0.9;

        float yaw = player.getYaw();
        double yawRad = Math.toRadians(yaw);
        double forwardX = -Math.sin(yawRad);
        double forwardZ =  Math.cos(yawRad);

        player.addVelocity(forwardX * horizontalBoost, extraY, forwardZ * horizontalBoost);
        player.velocityModified = true;

        // Mark this player as having an active GestaltThrow
        ((IGestaltPlayer) player).gestaltresonance$setGestaltThrowActive(true);
        
        // If on client, send packet to server
        if (player.getWorld().isClient) {
            ClientPlayNetworking.send(new GestaltThrowPayload(true));
        }

        // Removed ledge grab cooldown; input/airborne gating prevents same-press grabs
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void gestaltresonance$endThrowOnLanding(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        IGestaltPlayer gp = (IGestaltPlayer) player;

        if (!gp.gestaltresonance$isGestaltThrowActive()) return;

        // End the throw state only AFTER fall damage has been processed.
        // Vanilla resets fallDistance to 0 once handleFallDamage has run on landing.
        // This ensures our LivingEntityFallDamageMixin sees the flag and cancels damage properly.
        if ((player.isOnGround() || player.isTouchingWater()) && player.fallDistance <= 0.0f) {
            gp.gestaltresonance$setGestaltThrowActive(false);

            // If client-side, inform the server to keep states in sync
            if (player.getWorld().isClient) {
                ClientPlayNetworking.send(new GestaltThrowPayload(false));
            }
        }
    }
}
