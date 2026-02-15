package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

/**
 * Tier III form of Amen Break.
 */
public class AmenBreakIII extends AmenBreakII {
    private int breakCoreCooldown = 0;
    private static final int BREAK_CORE_MAX_COOLDOWN = 3600; // 3 minutes = 180 seconds = 3600 ticks

    public AmenBreakIII(EntityType<? extends AmenBreakIII> type, World world) {
        super(type, world);
    }

    @Override
    public void resetCooldowns() {
        super.resetCooldowns();
        this.breakCoreCooldown = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (breakCoreCooldown > 0) {
                breakCoreCooldown--;
                this.setPowerCooldown(2, breakCoreCooldown, BREAK_CORE_MAX_COOLDOWN);
            }
        }
    }

    @Override
    public void setOwner(net.minecraft.entity.player.PlayerEntity owner) {
        super.setOwner(owner);
        if (owner != null && !this.getWorld().isClient) {
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            this.breakCoreCooldown = gp.gestaltresonance$getGestaltPowerCooldownRemaining(getGestaltId(), 2);
        }
    }

    @Override
    public int getPowerCount() {
        return 3;
    }

    @Override
    public Identifier getGestaltId() {
        return Identifier.of("gestaltresonance", "amen_break_iii");
    }

    @Override
    protected int getMaxLevel() {
        return 10;
    }

    public void breakCore(ServerPlayerEntity player) {
        if (player == null || player.getServerWorld() == null) return;

        IGestaltPlayer gp = (IGestaltPlayer) player;

        // Toggle off if already active
        if (gp.gestaltresonance$getBreakCoreTicks() > 0) {
            gp.gestaltresonance$setBreakCoreTicks(0);
            player.removeStatusEffect(net.minecraft.entity.effect.StatusEffects.INVISIBILITY);

            // Stop the sound on the client
            player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.StopSoundS2CPacket(
                    net.ragdot.gestaltresonance.sound.ModSounds.BREAK_CORE.getId(),
                    net.minecraft.sound.SoundCategory.PLAYERS
            ));
            return;
        }

        if (this.breakCoreCooldown > 0) return;

        // Trigger Break Core: 10 seconds = 200 ticks
        gp.gestaltresonance$setBreakCoreTicks(200);

        // Apply invisibility
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.INVISIBILITY,
                200,
                0,
                false,
                false,
                true
        ));

        // Play the sound
        player.getWorld().playSoundFromEntity(null, player,
                net.ragdot.gestaltresonance.sound.ModSounds.BREAK_CORE,
                net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);

        this.breakCoreCooldown = BREAK_CORE_MAX_COOLDOWN;
        this.setPowerCooldown(2, this.breakCoreCooldown, BREAK_CORE_MAX_COOLDOWN);
    }
}
