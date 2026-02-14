package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

/**
 * Tier III form of Spillways.
 */
public class SpillwaysIII extends SpillwaysII {
    private int ciriceCooldown = 0;
    private static final int CIRICE_MAX_COOLDOWN = 3600; // 3 minutes = 180 seconds = 3600 ticks

    public SpillwaysIII(EntityType<? extends SpillwaysIII> type, World world) {
        super(type, world);
    }

    @Override
    public void resetCooldowns() {
        super.resetCooldowns();
        this.ciriceCooldown = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (ciriceCooldown > 0) {
                ciriceCooldown--;
                this.setPowerCooldown(2, ciriceCooldown, CIRICE_MAX_COOLDOWN);
            }
        }
    }

    @Override
    public void setOwner(net.minecraft.entity.player.PlayerEntity owner) {
        super.setOwner(owner);
        if (owner != null && !this.getWorld().isClient) {
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            this.ciriceCooldown = gp.gestaltresonance$getGestaltPowerCooldownRemaining(getGestaltId(), 2);
        }
    }

    @Override
    public int getPowerCount() {
        return 3;
    }

    @Override
    public Identifier getGestaltId() {
        return Identifier.of("gestaltresonance", "spillways_iii");
    }

    @Override
    protected int getMaxLevel() {
        return 10;
    }

    public void cirice(ServerPlayerEntity player) {
        if (player == null || player.getServerWorld() == null) return;
        if (this.ciriceCooldown > 0) return;

        IGestaltPlayer gp = (IGestaltPlayer) player;
        float stamina = this.getStamina(); // SpillwaysIII inherits getStamina() from GestaltBase
        if (stamina <= 0) return;

        // lasts 0.5 seconds for each stamina consumed
        // 0.5 seconds = 10 ticks.
        int lifespan = (int) (stamina * 10);

        CiriceEntity cirice = new CiriceEntity(Gestaltresonance.CIRICE_ENTITY, player.getWorld());
        cirice.setOwner(player);
        cirice.setLifespan(lifespan);
        cirice.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0, 0);
        player.getWorld().spawnEntity(cirice);

        this.setStamina(0); // costs all remaining stamina
        this.ciriceCooldown = CIRICE_MAX_COOLDOWN;
        this.setPowerCooldown(2, this.ciriceCooldown, CIRICE_MAX_COOLDOWN);
    }
}
