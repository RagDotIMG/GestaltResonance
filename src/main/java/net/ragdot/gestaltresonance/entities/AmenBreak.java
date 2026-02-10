package net.ragdot.gestaltresonance.entities;


import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;


public class AmenBreak extends GestaltBase {

    // Short cooldown for Jungle Bomber ability (ticks)
    private int jungleBomberCooldown = 0;


    public AmenBreak(EntityType<? extends AmenBreak> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        // Server-side cooldown decrement
        if (!this.getWorld().isClient) {
            if (jungleBomberCooldown > 0) {
                jungleBomberCooldown--;
            }
        }
    }

    @Override
    protected double getHeightOffset() {
        return 0.4;
    }

    @Override
    protected double getFollowBackOffset() {
        return -0.5;
    }

    @Override
    protected double getFollowSideOffset() {
        return 0.1;
    }

    @Override
    public net.minecraft.util.Identifier getGestaltId() {
        return net.minecraft.util.Identifier.of("gestaltresonance", "amen_break");
    }

    // Attributes specific to this stand (can override base)
    public static DefaultAttributeContainer.Builder createAttributes() {
        return GestaltBase.createBaseStandAttributes();
    }


    @Override
    protected boolean canMeleeAttack() {
        return false;
    }

    @Override
    protected double getMaxChaseRange() { return 1.0; }

    @Override
    protected double getAttackReach() { return 8.0; }

    @Override
    protected float getAttackDamage() { return 8.0f; }

    @Override
    protected int getAttackCooldownTicks() { return 40; }

    @Override
    protected float getDamageReductionFactor() {
        return 0.0f;
    }

    // === Passive: Muffle owner's movement so they are not detected by Warden/Sculk sensors ===
    @Override
    protected void applyOwnerPassiveBuffs(PlayerEntity owner) {
        if (owner == null || owner.getWorld().isClient) return;
        // Activate muffled movement flag; sound/vibration suppression is handled by mixins
        ((IGestaltPlayer) owner).gestaltresonance$setMuffledMovementActive(true);
    }

    @Override
    protected void clearOwnerPassiveBuffs(PlayerEntity owner) {
        if (owner == null) owner = this.getOwner();
        if (owner == null || owner.getWorld().isClient) return;
        ((IGestaltPlayer) owner).gestaltresonance$setMuffledMovementActive(false);
    }

    // === Ability 1: Jungle Bomber ===
    // Power 1: Shoot a Pop Bud projectile (snowball-like)
    public void jungleBomber(ServerPlayerEntity player) {
        if (player == null || player.getServerWorld() == null) return;
        // Enforce 30-tick cooldown and stamina requirement (>= 4)
        if (this.jungleBomberCooldown > 0) return;
        if (this.getStamina() < 4.0f) return;
        var world = player.getServerWorld();

        // Spawn projectile from near the player's right hand, slightly up and to the right
        net.ragdot.gestaltresonance.projectile.PopBud bud = new net.ragdot.gestaltresonance.projectile.PopBud(
                net.ragdot.gestaltresonance.Gestaltresonance.POP_BUD,
                world
        );
        bud.setOwner(player);
        // Compute spawn offset based on player yaw
        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);
        // Right vector on XZ plane
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);
        // Forward vector on XZ plane
        double fwdX = -Math.sin(rad);
        double fwdZ =  Math.cos(rad);

        double forwardOffset = 0.4; // slightly in front
        double sideOffset = -0.25;   // to the right
        double upOffset = 0.10;     // slightly up

        double spawnX = player.getX() + forwardOffset * fwdX + sideOffset * rightX;
        double spawnY = player.getEyeY() - 0.2 + upOffset; // close to hand height
        double spawnZ = player.getZ() + forwardOffset * fwdZ + sideOffset * rightZ;

        bud.refreshPositionAndAngles(spawnX, spawnY, spawnZ, player.getYaw(), player.getPitch());
        // Slightly slower than snowball default
        bud.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 1.25f, 1.0f);
        world.spawnEntity(bud);

        // Drain stamina on use
        this.setStamina(Math.max(0.0f, this.getStamina() - 4.0f));
        // Start cooldown (30 ticks ~= 1.5s)
        this.jungleBomberCooldown = 30;
    }
}
