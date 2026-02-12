package net.ragdot.gestaltresonance.entities;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Tier II form of Amen Break. Currently inherits Tier I behavior and tuning.
 * Separate class/EntityType allows distinct assets/attributes later.
 */
public class AmenBreakII extends AmenBreak {
    private int futuramaCooldown = 0;
    private int futuramaMaxCooldown = 0;

    public AmenBreakII(EntityType<? extends AmenBreakII> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (futuramaCooldown > 0) {
                futuramaCooldown--;
                this.setPowerCooldown(1, futuramaCooldown, futuramaMaxCooldown);
            }
        }
    }

    @Override
    public void setOwner(net.minecraft.entity.player.PlayerEntity owner) {
        super.setOwner(owner);
        if (owner != null && !this.getWorld().isClient) {
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            this.futuramaCooldown = gp.gestaltresonance$getGestaltPowerCooldownRemaining(getGestaltId(), 1);
            this.futuramaMaxCooldown = gp.gestaltresonance$getGestaltPowerCooldownMax(getGestaltId(), 1);
        }
    }

    @Override
    public int getPowerCount() {
        // Tier II+ exposes Futurama as the second power.
        return 2;
    }

    @Override
    public Identifier getGestaltId() {
        return Identifier.of("gestaltresonance", "amen_break_ii");
    }

    @Override
    protected int getMaxLevel() {
        return 8;
    }

    public void futurama(ServerPlayerEntity player) {
        if (player == null || player.getServerWorld() == null) return;
        if (this.futuramaCooldown > 0) return;
        if (this.getStamina() < 20.0f) return;

        // Prevent overlapping sessions per-player.
        if (net.ragdot.gestaltresonance.util.FuturamaManager.hasActiveSession(player.getUuid())) return;

        // Start Futurama session (record 6s, reset, replay 6s).
        net.ragdot.gestaltresonance.util.FuturamaManager.tryStart(player);

        this.setStamina(Math.max(0.0f, this.getStamina() - 20.0f));
        this.futuramaMaxCooldown = getFuturamaMaxCooldownTicks();
        this.futuramaCooldown = futuramaMaxCooldown;
        this.setPowerCooldown(1, this.futuramaCooldown, futuramaMaxCooldown);
    }

    private int getFuturamaMaxCooldownTicks() {
        // Base 1200 ticks, reduced by 50 ticks per gestalt level.
        return Math.max(0, 1200 - (50 * this.getLvl()));
    }
}
