package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.Gestaltresonance;

import java.util.UUID;

public class GestaltBase extends MobEntity {

    protected PlayerEntity owner;
    protected UUID ownerUuid;


    // Super jump detection
    private boolean ownerWasOnGround = true;
    private boolean ownerWasSneakingOnGround = false;

    // === Combat state ===
    protected LivingEntity currentTarget;
    protected int attackCooldownTicks = 0; // simple per-stand cooldown

    public GestaltBase(EntityType<? extends MobEntity> type, World world) {
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

    // ===== Per-gestalt tuning hooks =====

    /** How far away the target can be before this Gestalt loses interest. */
    protected double getMaxChaseRange() {
        return 5.0; // default; override per Gestalt
    }

    /** Distance at which the Gestalt is allowed to punch. */
    protected double getAttackReach() {
        return 5.0; // default; override per Gestalt
    }

    /** Base damage per punch. */
    protected float getAttackDamage() {
        return 4.0f; // 2 hearts; override per Gestalt
    }

    /** Cooldown between punches in ticks (20 ticks = 1s). */
    protected int getAttackCooldownTicks() {
        return 10; // 0.5s; override per Gestalt
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


            updateOwnerFollowAndCombat();
        }
    }

    private void updateOwnerFollowAndCombat() {
        // 1) Update target based on owner’s state (attacker first, then what owner is attacking)
        updateTargetFromOwner();

        // 2) If no target, just follow owner
        if (currentTarget == null || !currentTarget.isAlive()) {
            currentTarget = null;
            updatePositionToOwner();
            return;
        }

        // 3) If target is too far from the OWNER, forget it and go back to owner
        double maxRange = getMaxChaseRange();
        double distSqToOwner = owner.squaredDistanceTo(currentTarget);
        if (distSqToOwner > maxRange * maxRange) {
            currentTarget = null;
            updatePositionToOwner();
            return;
        }

        // 4) Move near the target (floating, instant reposition for now)
        moveNearTarget();

        // 5) Attack target if in reach and not on cooldown
        handleAttacking();
    }

    /**
     * Pick / update current target:
     * 1) Prefer a hostile mob that recently attacked the owner.
     * 2) Otherwise, use the hostile mob the owner is attacking.
     */
    protected void updateTargetFromOwner() {
        if (owner == null) return;

        double maxRange = getMaxChaseRange();
        double maxRangeSq = maxRange * maxRange;

        // === 1) Prioritize whoever is attacking the owner ===
        {
            var attacker = owner.getAttacker(); // last entity that hurt the owner
            if (attacker instanceof LivingEntity living && attacker instanceof HostileEntity) {
                // Only consider if within chase range
                if (owner.squaredDistanceTo(living) <= maxRangeSq) {
                    boolean shouldSwitch = false;

                    if (currentTarget == null || !currentTarget.isAlive()) {
                        shouldSwitch = true;
                    } else {
                        double currentDistSq = owner.squaredDistanceTo(currentTarget);
                        double attackerDistSq = owner.squaredDistanceTo(living);
                        // Prefer closer attacker
                        if (attackerDistSq + 0.01 < currentDistSq) {
                            shouldSwitch = true;
                        }
                    }

                    if (shouldSwitch) {
                        currentTarget = living;
                        return; // attacker takes full priority
                    }
                }
            }
        }

        // === 2) Fallback: whoever the owner is attacking ===
        {
            var attacking = owner.getAttacking(); // last entity owner attacked
            if (!(attacking instanceof LivingEntity living)) return;
            if (!(attacking instanceof HostileEntity)) return; // only hostile mobs

            if (currentTarget == living && currentTarget.isAlive()) return;

            if (this.squaredDistanceTo(living) <= maxRangeSq) {
                currentTarget = living;
            }
        }
    }

    /** Teleport/position this Gestalt near the current target, biased around the owner. */
    protected void moveNearTarget() {
        if (currentTarget == null || owner == null) return;

        // Position stand between owner and target, a bit closer to the target
        Vec3d ownerPos = owner.getPos();
        Vec3d targetPos = currentTarget.getPos();

        Vec3d mid = ownerPos.lerp(targetPos, 0.65); // closer to target (65% toward target)
        double heightOffset = 0.4;

        double x = mid.x;
        double y = mid.y + heightOffset;
        double z = mid.z;

        float yaw = (float) Math.toDegrees(Math.atan2(targetPos.z - ownerPos.z, targetPos.x - ownerPos.x));

        this.refreshPositionAndAngles(x, y, z, yaw, 0.0f);
    }

    /** Try to punch the current target with a cooldown and reach check. */
    protected void handleAttacking() {
        if (currentTarget == null || !currentTarget.isAlive()) return;

        if (attackCooldownTicks > 0) {
            attackCooldownTicks--;
            return;
        }

        double reach = getAttackReach();
        double distSq = this.squaredDistanceTo(currentTarget);
        if (distSq > reach * reach) {
            return; // too far to punch
        }

        float damage = getAttackDamage();
        var source = this.getWorld().getDamageSources().mobAttack(this);

        currentTarget.damage(source, damage);

        attackCooldownTicks = getAttackCooldownTicks();
    }


    // ===== SuperJump =====
    /**
     * When the owner has this Gestalt summoned and does a crouch + jump,
     * give them a “super jump” by boosting their upward velocity.
     */
    protected void handleSuperJump() {
        if (owner == null) return;

        boolean onGroundNow = owner.isOnGround();
        boolean sneakingNow = owner.isSneaking();

        // Detect jump start: was on ground last tick, now off ground
        if (ownerWasOnGround && !onGroundNow) {
            // Only boost if the player was sneaking while still on the ground
            if (ownerWasSneakingOnGround) {
                // Extra upward velocity; vanilla jump is ~0.42
                double extraY = 0.42; // roughly +1 extra block of height
                owner.addVelocity(0.0, extraY, 0.0);
                owner.velocityDirty = true;

                // Optional: log once to confirm it fires
                Gestaltresonance.LOGGER.info("Super jump applied: extraY=" + extraY);
            }
        }

        // Update history for next tick
        ownerWasSneakingOnGround = onGroundNow && sneakingNow;
        ownerWasOnGround = onGroundNow;
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
            // If no owner, just ignore (or call super if you want stands to be hittable)
            return false;
        }

        // Only transfer a fraction of the damage to the player
        float transferred = amount / 3.0f;
        if (transferred <= 0.0f) {
            return false;
        }

        boolean result = owner.damage(source, transferred);

        // Keep Gestalt health in sync with owner
        this.setHealth(owner.getHealth());

        return result;
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.entity.damage.DamageSource source) {
        // Stand itself never actually loses health; all “real” damage goes to owner (scaled)
        return true;
    }
}

