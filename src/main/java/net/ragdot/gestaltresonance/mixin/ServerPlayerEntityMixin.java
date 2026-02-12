package net.ragdot.gestaltresonance.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void gestaltresonance$copyGestaltData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        IGestaltPlayer oldGp = (IGestaltPlayer) oldPlayer;
        IGestaltPlayer newGp = (IGestaltPlayer) this;

        // Note: we can't easily iterate maps if they are private in the other mixin,
        // but since we are on the same class (ServerPlayerEntity usually inherits from PlayerEntity),
        // and both implement IGestaltPlayer, we can just use the interface methods.

        // We need the list of Gestalt IDs again.
        net.minecraft.util.Identifier[] ids = {
                net.minecraft.util.Identifier.of("gestaltresonance", "gestalt"),
                net.minecraft.util.Identifier.of("gestaltresonance", "scorched_utopia"),
                net.minecraft.util.Identifier.of("gestaltresonance", "amen_break"),
                net.minecraft.util.Identifier.of("gestaltresonance", "scorched_utopia_ii"),
                net.minecraft.util.Identifier.of("gestaltresonance", "scorched_utopia_iii"),
                net.minecraft.util.Identifier.of("gestaltresonance", "amen_break_ii"),
                net.minecraft.util.Identifier.of("gestaltresonance", "amen_break_iii"),
                net.minecraft.util.Identifier.of("gestaltresonance", "spillways"),
                net.minecraft.util.Identifier.of("gestaltresonance", "spillways_ii"),
                net.minecraft.util.Identifier.of("gestaltresonance", "spillways_iii")
        };

        for (net.minecraft.util.Identifier id : ids) {
            newGp.gestaltresonance$setGestaltStamina(id, oldGp.gestaltresonance$getGestaltStamina(id));
            newGp.gestaltresonance$setGestaltExp(id, oldGp.gestaltresonance$getGestaltExp(id));
            newGp.gestaltresonance$setGestaltLvl(id, oldGp.gestaltresonance$getGestaltLvl(id));
            
            // Copy power cooldowns as well
            for (int i = 0; i < 3; i++) {
                int rem = oldGp.gestaltresonance$getGestaltPowerCooldownRemaining(id, i);
                int max = oldGp.gestaltresonance$getGestaltPowerCooldownMax(id, i);
                newGp.gestaltresonance$setGestaltPowerCooldown(id, i, rem, max);
            }
        }
    }
}
