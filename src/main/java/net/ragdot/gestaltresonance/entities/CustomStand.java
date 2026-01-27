package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class CustomStand extends MobEntity {

    protected PlayerEntity owner;
    protected UUID ownerUuid;

    public CustomStand(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.setAiDisabled(true);
    }

    // ===== Shared attributes =====
    public static DefaultAttributeContainer.Builder createBaseStandAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0);
    }

    // ===== Owner handling =====
    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner != null ? owner.getUuid() : null;
    }

    public PlayerEntity getOwner() {
        return owner;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    // ===== Persistence =====
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.ownerUuid != null) {
            nbt.putUuid("OwnerUUID", this.ownerUuid);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) {
            this.ownerUuid = nbt.getUuid("OwnerUUID");
        } else {
            this.ownerUuid = null;
            this.owner = null;
        }
    }

    // ===== Tick =====
    @Override
    public void tick() {
        super.tick();

        this.setNoGravity(true);
        this.noClip = true;
        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;

        if (!this.getWorld().isClient) {
            // Re-link owner from UUID after reload
            if (this.owner == null && this.ownerUuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
                this.owner = serverWorld.getServer().getPlayerManager().getPlayer(this.ownerUuid);
            }

            if (owner == null || !owner.isAlive()) {
                this.discard();
                return;
            }

            this.setHealth(owner.getHealth());
            updatePositionToOwner();
        }
    }

    // ===== Following behavior (can be overridden per stand) =====
    protected void updatePositionToOwner() {
        double playerX = owner.getX();
        double playerY = owner.getY();
        double playerZ = owner.getZ();

        float yaw = owner.getYaw();
        double backOffset = -0.9;
        double sideOffset = 0.9;  // to the right
        double heightOffset = 0.3;

        double rad = Math.toRadians(yaw);

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double targetX = playerX + backOffset * backX + sideOffset * rightX;
        double targetZ = playerZ + backOffset * backZ + sideOffset * rightZ;
        double targetY = playerY + heightOffset;

        this.refreshPositionAndAngles(targetX, targetY, targetZ, yaw, this.getPitch());
    }

    // ===== Damage redirect to owner =====
    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        if (this.getWorld().isClient) return false;
        if (owner == null || !owner.isAlive()) {
            return super.damage(source, amount);
        }

        boolean result = owner.damage(source, amount);
        this.setHealth(owner.getHealth());
        return result;
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.entity.damage.DamageSource source) {
        // Stand itself never takes “real” damage; all redirected
        return true;
    }
}