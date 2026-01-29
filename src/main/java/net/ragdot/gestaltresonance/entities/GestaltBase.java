package net.ragdot.gestaltresonance.entities;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.AnimationState;
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

import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GestaltBase extends MobEntity {

    protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    protected static final TrackedData<Integer> TARGET_ID = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Boolean> IS_ATTACKING = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected PlayerEntity owner;
    protected UUID ownerUuid;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;


    // Gestalt Throw detection
    private boolean ownerWasOnGround = true;
    private boolean ownerWasSneakingOnGround = false;

    // === Combat state ===
    protected LivingEntity currentTarget;
    protected int attackCooldownTicks = 0; // simple per-stand cooldown
    
    // Smooth movement state
    private static final double SMOOTH_FACTOR = 0.3; // How much to move towards target each tick (0.0 to 1.0)

    public GestaltBase(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
        this.setAiDisabled(true);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(TARGET_ID, -1);
        builder.add(IS_ATTACKING, false);
    }

    // ===== Shared attributes =====
    public static DefaultAttributeContainer.Builder createBaseStandAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 30);
    }

    // ===== Owner handling =====
    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner != null ? owner.getUuid() : null;
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(this.ownerUuid));
    }

    public PlayerEntity getOwner() {
        if (owner == null) {
            this.dataTracker.get(OWNER_UUID).ifPresent(uuid -> {
                this.owner = this.getWorld().getPlayerByUuid(uuid);
                this.ownerUuid = uuid;
            });
        }
        return owner;
    }

    public UUID getOwnerUuid() {
        if (ownerUuid == null) {
            this.dataTracker.get(OWNER_UUID).ifPresent(uuid -> this.ownerUuid = uuid);
        }
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
        return 40; // 2s; override per Gestalt
    }

    /** The multiplier applied to incoming damage before it's transferred to the owner.
     *  Lower values mean more defense. Default is 0.3 (70% reduction). */
    protected float getDamageReductionFactor() {
        return 0.3f;
    }

    /** Whether this Gestalt can perform the standard melee strike-and-retreat logic. */
    protected boolean canMeleeAttack() {
        return false; // disabled by default
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
            this.dataTracker.set(OWNER_UUID, Optional.of(this.ownerUuid));
        } else {
            this.ownerUuid = null;
            this.owner = null;
            this.dataTracker.set(OWNER_UUID, Optional.empty());
        }
    }

    // ===== Tick =====
    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        this.setupAnimationStates();
        this.setNoGravity(true);
        this.noClip = true;
        this.velocityModified = true;
        this.setVelocity(Vec3d.ZERO);

        // Re-link owner if necessary
        if (this.owner == null || !this.owner.isAlive()) {
            this.getOwner();
        }

        if (!this.getWorld().isClient) {
            if (owner == null || !owner.isAlive()) {
                this.discard();
                return;
            }

            IGestaltPlayer gestaltPlayer = (IGestaltPlayer) owner;

            if (gestaltPlayer.gestaltresonance$isLedgeGrabbing()) {
                owner.setVelocity(Vec3d.ZERO);
                owner.velocityModified = true;
                owner.fallDistance = 0;
                owner.setNoGravity(true);
            } else {
                owner.setNoGravity(false);
            }

            // Blocking logic for Guard Mode
            if (gestaltPlayer.gestaltresonance$isGuarding()) {
                blockProjectilesAndEntities();
            }

            updateOwnerFollowAndCombat();
            
            // Sync target to client
            int targetId = currentTarget != null ? currentTarget.getId() : -1;
            if (this.dataTracker.get(TARGET_ID) != targetId) {
                this.dataTracker.set(TARGET_ID, targetId);
            }

            // Sync attack state to client
            boolean isAttacking = currentTarget != null && attackCooldownTicks <= 0;
            if (this.dataTracker.get(IS_ATTACKING) != isAttacking) {
                this.dataTracker.set(IS_ATTACKING, isAttacking);
            }
        } else {
            // Client-side: sync target and attack state from data tracker
            int targetId = this.dataTracker.get(TARGET_ID);
            boolean isAttacking = this.dataTracker.get(IS_ATTACKING);

            if (targetId == -1) {
                currentTarget = null;
            } else if (currentTarget == null || currentTarget.getId() != targetId) {
                var entity = this.getWorld().getEntityById(targetId);
                if (entity instanceof LivingEntity living) {
                    currentTarget = living;
                } else {
                    currentTarget = null;
                }
            }

            // Always update position to keep it in sync
            if (owner != null && owner.isAlive()) {
                IGestaltPlayer gp = (IGestaltPlayer) owner;
                boolean isGuarding = gp.gestaltresonance$isGuarding();
                
                if (canMeleeAttack() && currentTarget != null && currentTarget.isAlive() && !isGuarding) {
                    // Check if target is in range of owner on client too to avoid weird snaps
                    double maxRange = getMaxChaseRange();
                    if (owner.squaredDistanceTo(currentTarget) <= maxRange * maxRange && isAttacking) {
                        moveNearMeleeTarget();
                    } else {
                        updatePositionToOwner();
                    }
                } else {
                    updatePositionToOwner();
                }
            }
        }
    }

    private void blockProjectilesAndEntities() {
        if (owner == null) return;

        // Block entities and projectiles in a 180-degree area in front of the player
        // The Gestalt is already positioned 0.8 blocks in front of the player.
        // We expand its reach to push entities back.
        
        double radius = 1.5;
        var entities = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(radius));
        Vec3d playerFacing = owner.getRotationVec(1.0f);
        Vec3d playerFacingFlat = new Vec3d(playerFacing.x, 0, playerFacing.z).normalize();

        for (var entity : entities) {
            if (entity == owner) continue;
            
            Vec3d toEntity = entity.getPos().subtract(owner.getPos());
            Vec3d toEntityFlat = new Vec3d(toEntity.x, 0, toEntity.z).normalize();
            
            double dot = toEntityFlat.dotProduct(playerFacingFlat);
            
            // 180 degrees check (dot > 0 means in front)
            if (dot > 0) {
                if (entity instanceof net.minecraft.entity.projectile.ProjectileEntity projectile) {
                    // Deflect or destroy projectile
                    // We only want to deflect it if it's moving TOWARDS the player
                    Vec3d projVel = projectile.getVelocity();
                    if (projVel.dotProduct(playerFacingFlat) < 0) {
                        projectile.setVelocity(projVel.multiply(-0.5));
                        projectile.velocityModified = true;
                    }
                } else if (entity instanceof LivingEntity) {
                    // Push entities back if they are too close
                    if (toEntity.lengthSquared() < 2.25) { // 1.5 blocks
                        Vec3d pushDir = toEntityFlat.multiply(0.3);
                        entity.addVelocity(pushDir.x, 0.1, pushDir.z);
                        entity.velocityModified = true;
                    }
                }
            }
        }
    }


    private void updateOwnerFollowAndCombat() {
        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) owner;
        // 0) If ledge grabbing or guarding, don't do combat
        if (gestaltPlayer.gestaltresonance$isLedgeGrabbing() || gestaltPlayer.gestaltresonance$isGuarding()) {
            updatePositionToOwner();
            return;
        }

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

        // 4) Position logic: move to target only if ready to attack, otherwise stay behind player
        if (canMeleeAttack() && attackCooldownTicks <= 0) {
            moveNearMeleeTarget();
        } else {
            updatePositionToOwner();
        }

        // 5) Attack target if in reach and not on cooldown
        if (canMeleeAttack()) {
            handleMeleeAttack();
        }
    }

    /**
     * Pick / update current target:
     * 1) Prefer a hostile mob that recently attacked the owner.
     * 2) Use a hostile mob that recently attacked the owner and is within range.
     * 3) Use the hostile mob the owner is attacking.
     * 4) Automatically target nearby hostile mobs within chase range.
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
            if (attacking instanceof LivingEntity living && attacking instanceof HostileEntity) {
                if (owner.squaredDistanceTo(living) <= maxRangeSq) {
                    if (currentTarget != living || !currentTarget.isAlive()) {
                        currentTarget = living;
                        return;
                    }
                    if (currentTarget == living) return;
                }
            }
        }

        // === 3) Auto-target: search for nearby hostile mobs ===
        if (currentTarget == null || !currentTarget.isAlive() || owner.squaredDistanceTo(currentTarget) > maxRangeSq) {
            List<HostileEntity> nearbyHostiles = this.getWorld().getEntitiesByClass(
                    HostileEntity.class,
                    owner.getBoundingBox().expand(maxRange),
                    entity -> entity.isAlive() && owner.squaredDistanceTo(entity) <= maxRangeSq
            );

            if (!nearbyHostiles.isEmpty()) {
                // Target the closest one
                HostileEntity closest = null;
                double closestDistSq = Double.MAX_VALUE;
                for (HostileEntity hostile : nearbyHostiles) {
                    double distSq = owner.squaredDistanceTo(hostile);
                    if (distSq < closestDistSq) {
                        closestDistSq = distSq;
                        closest = hostile;
                    }
                }
                currentTarget = closest;
            }
        }
    }

    /** Teleport/position this Gestalt near the current target, biased around the owner. */
    protected void moveNearMeleeTarget() {
        if (currentTarget == null || owner == null) return;

        // Position stand between owner and target, a bit closer to the target
        Vec3d ownerPos = owner.getPos();
        Vec3d targetPos = currentTarget.getPos();

        Vec3d mid = ownerPos.lerp(targetPos, 0.65); // closer to target (65% toward target)
        double heightOffset = 0.4;

        double targetX = mid.x;
        double targetY = mid.y + heightOffset;
        double targetZ = mid.z;

        float yaw = (float) Math.toDegrees(Math.atan2(targetPos.z - ownerPos.z, targetPos.x - ownerPos.x)) - 90.0f;

        applySmoothPosition(targetX, targetY, targetZ, yaw, true);
    }

    private void applySmoothPosition(double tx, double ty, double tz, float yaw, boolean smooth) {
        Vec3d targetPos = new Vec3d(tx, ty, tz);
        
        if (smooth) {
            // Interpolate position
            Vec3d currentPos = this.getPos();
            Vec3d nextPos = currentPos.lerp(targetPos, SMOOTH_FACTOR);
            this.updatePosition(nextPos.x, nextPos.y, nextPos.z);
        } else {
            // Instantaneous position update
            this.updatePosition(tx, ty, tz);
        }
        
        this.setYaw(yaw);
        this.setPitch(0);
        this.setHeadYaw(yaw);
        this.bodyYaw = yaw;
        this.prevYaw = yaw;
        this.prevHeadYaw = yaw;
        this.prevBodyYaw = yaw;
    }

    /** Try to punch the current target with a cooldown and reach check. */
    protected void handleMeleeAttack() {
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


    // ===== Following behavior (can be overridden per stand) =====
    protected void updatePositionToOwner() {
        if (owner == null) return;

        double playerX = owner.getX();
        double playerY = owner.getY();
        double playerZ = owner.getZ();
        float yaw = owner.getYaw();
        double heightOffset = 0.3;

        boolean isGuarding = ((IGestaltPlayer) owner).gestaltresonance$isGuarding();
        boolean isLedgeGrabbing = ((IGestaltPlayer) owner).gestaltresonance$isLedgeGrabbing();

        if (isLedgeGrabbing) {
            Vec3d fixedPos = ((IGestaltPlayer) owner).gestaltresonance$getLedgeGrabGestaltPos();
            float fixedYaw = ((IGestaltPlayer) owner).gestaltresonance$getLedgeGrabGestaltYaw();
            if (fixedPos != null) {
                applySmoothPosition(fixedPos.x, fixedPos.y, fixedPos.z, fixedYaw, false);
                return;
            }
            
            // Fallback if fixedPos is not yet synced or available
            net.minecraft.util.math.BlockPos ledgePos = ((IGestaltPlayer) owner).gestaltresonance$getLedgeGrabPos();
            if (ledgePos != null) {
                Vec3d targetBlockCenter = new Vec3d(ledgePos.getX() + 0.5, ledgePos.getY() + 0.5, ledgePos.getZ() + 0.5);
                Vec3d playerPos = owner.getPos();
                
                // Calculate direction from player to target block (horizontal only)
                Vec3d dirToLedge = new Vec3d(targetBlockCenter.x - playerPos.x, 0, targetBlockCenter.z - playerPos.z).normalize();
                
                // 0.3 blocks closer to the target block
                double targetX = playerX + dirToLedge.x * 0.3;
                double targetZ = playerZ + dirToLedge.z * 0.3;
                
                // 1.1 blocks lower than the player eye level (was +0.2, lowered by 1.3)
                double targetY = owner.getY() + owner.getEyeHeight(owner.getPose()) - 1.1;

                applySmoothPosition(targetX, targetY, targetZ, yaw, false);
                return;
            }
        }

        if (isGuarding) {
            // Guard mode: Locked in front of player
            double frontOffset = 0.8;
            double rad = Math.toRadians(yaw);
            double frontX = -Math.sin(rad);
            double frontZ = Math.cos(rad);

            double targetX = playerX + frontOffset * frontX;
            double targetZ = playerZ + frontOffset * frontZ;
            double targetY = playerY + heightOffset;

            applySmoothPosition(targetX, targetY, targetZ, yaw, false);
        } else {
            // Normal mode: Slightly behind and to the side
            double backOffset = -0.9;
            double sideOffset = 0.9;  // to the right

            double rad = Math.toRadians(yaw);

            double backX = -Math.sin(rad);
            double backZ =  Math.cos(rad);
            double rightX =  Math.cos(rad);
            double rightZ =  Math.sin(rad);

            double targetX = playerX + backOffset * backX + sideOffset * rightX;
            double targetZ = playerZ + backOffset * backZ + sideOffset * rightZ;
            double targetY = playerY + heightOffset;

            applySmoothPosition(targetX, targetY, targetZ, yaw, false);
        }
    }

    // ===== idle behavior =====
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 60;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        if (this.getWorld().isClient) return false;

        // Gestalten are invincible while attacking
        if (this.dataTracker.get(IS_ATTACKING)) {
            return false;
        }

        // Gestalten do not take suffocation damage
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.IN_WALL)) {
            return false;
        }

        if (owner == null || !owner.isAlive()) {
            return false;
        }

        // Apply guard reduction
        float actualAmount = amount;
        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) owner;
        if (gestaltPlayer.gestaltresonance$isGuarding()) {
            Vec3d sourcePos = source.getPosition();
            if (sourcePos != null) {
                Vec3d playerPos = owner.getPos();
                Vec3d dirToSource = sourcePos.subtract(playerPos).normalize();
                Vec3d playerFacing = owner.getRotationVec(1.0f);
                double dot = dirToSource.dotProduct(new Vec3d(playerFacing.x, 0, playerFacing.z).normalize());
                if (dot > 0) {
                    actualAmount *= 0.2f; // 80% reduction
                }
            }
        }

        // New system: reduce damage by the factor and apply directly to owner health
        float transferred = actualAmount * getDamageReductionFactor();

        // Check for owner invulnerability
        if (owner.isInvulnerableTo(source)) {
            return false;
        }

        // Apply health adjustment without hit feedback
        gestaltPlayer.gestaltresonance$setRedirectionActive(true);
        try {
            float newHealth = owner.getHealth() - transferred;
            owner.setHealth(Math.max(0, newHealth));

            if (newHealth <= 0) {
                owner.onDeath(source);
            }
        } finally {
            gestaltPlayer.gestaltresonance$setRedirectionActive(false);
        }

        return true;
    }

    @Override
    public void setVelocity(Vec3d velocity) {
        // Prevent knockback from being applied to the Gestalt
        super.setVelocity(Vec3d.ZERO);
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.entity.damage.DamageSource source) {
        // Gestalten don't take direct damage anymore, but we need to return true
        // to some things to let 'damage' method handle redirection.
        // Actually, MobEntity.damage checks isInvulnerableTo.
        // If we want to handle it in 'damage', we should return false for things we want to redirect.
        
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.IN_WALL)) return true;

        return super.isInvulnerableTo(source);
    }
}

