package net.ragdot.gestaltresonance.entities.gestaltframework;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.GestaltAssignments;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GestaltBase extends MobEntity {

    protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    protected static final TrackedData<Integer> TARGET_ID = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Boolean> IS_ATTACKING = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Boolean> IS_WINDING_UP = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Boolean> IS_PUNCHING = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Float> STAMINA = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Float> GUARD_REDUCTION = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Integer> EXP = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> LVL = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.INTEGER);
    // Sync flag for client to know when server-authoritative guard dash is active
    protected static final TrackedData<Boolean> IS_GUARD_DASHING = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.BOOLEAN);
    // Sync flag for client to know when we are frozen at post-dash hit position (to let Punch play)
    protected static final TrackedData<Boolean> IS_POST_DASH_STICKING = DataTracker.registerData(GestaltBase.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected PlayerEntity owner;
    protected UUID ownerUuid;

    public final GestaltAnimationHelper animationHelper = new GestaltAnimationHelper(this);
    public final GestaltAbilityHelper abilityHelper = new GestaltAbilityHelper(this);
    public final GestaltDamageHelper damageHelper = new GestaltDamageHelper(this);



    // === Combat state ===
    protected LivingEntity currentTarget;
    protected int attackCooldownTicks = 0; // simple per-stand cooldown
    protected int punchingTicks = 0;
    protected int windUpTicks = 0;
    protected int guardingTicks = 0;
    protected int staminaRegenDelay = 0;

    // Smooth movement state
    private static final double SMOOTH_FACTOR = 0.1; // How much to move towards target each tick (0.0 to 1.0)

    // Guard dash ability state
    private boolean guardDashActive = false;
    private double guardDashDistanceLeft = 0.0;
    private Vec3d guardDashDirection = Vec3d.ZERO;
    private boolean guardDashHitDone = false;

    // Post-dash stick (freeze in place to display punch animation)
    private boolean postDashStickActive = false;
    private int postDashStickTicksLeft = 0;
    private Vec3d postDashStickPos = Vec3d.ZERO;

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
        builder.add(IS_WINDING_UP, false);
        builder.add(IS_PUNCHING, false);
        builder.add(STAMINA, 26.0f);
        builder.add(GUARD_REDUCTION, 0.0f);
        builder.add(EXP, 0);
        builder.add(LVL, 1);
        builder.add(IS_GUARD_DASHING, false);
        builder.add(IS_POST_DASH_STICKING, false);
    }

    // ===== Shared attributes =====
    public static DefaultAttributeContainer.Builder createBaseStandAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 20.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 20.0);
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

    public float getStamina() {
        return this.dataTracker.get(STAMINA);
    }

    public void setStamina(float stamina) {
        float oldStamina = getStamina();
        float newStamina = Math.max(0, Math.min(stamina, getMaxStamina()));
        this.dataTracker.set(STAMINA, newStamina);

        if (newStamina <= 0 && oldStamina > 0) {
            this.staminaRegenDelay = 80; // 4 seconds delay when hitting 0
        }

        PlayerEntity owner = getOwner();
        if (owner != null && !this.getWorld().isClient) {
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            gp.gestaltresonance$setGestaltStamina(this.getGestaltId(), newStamina);
        }
    }

    public float getMaxStamina() {
        return 26.0f;
    }

    public float getGuardReduction() {
        return this.dataTracker.get(GUARD_REDUCTION);
    }

    public int getExp() {
        return this.dataTracker.get(EXP);
    }

    public void setExp(int exp) {
        int maxExp = getMaxExp();
        if (exp >= maxExp) {
            if (getLvl() < 5) {
                this.dataTracker.set(EXP, 0);
                setLvl(getLvl() + 1);
            } else {
                this.dataTracker.set(EXP, maxExp); // Keep it full if max level reached
            }
        } else {
            this.dataTracker.set(EXP, exp);
        }
        
        PlayerEntity owner = getOwner();
        if (owner != null && !this.getWorld().isClient) {
            ((IGestaltPlayer) owner).gestaltresonance$setGestaltExp(this.getGestaltId(), this.dataTracker.get(EXP));
        }
    }

    public int getLvl() {
        return this.dataTracker.get(LVL);
    }

    public void setLvl(int lvl) {
        int cappedLvl = Math.min(lvl, 5);
        this.dataTracker.set(LVL, cappedLvl);
        PlayerEntity owner = getOwner();
        if (owner != null && !this.getWorld().isClient) {
            ((IGestaltPlayer) owner).gestaltresonance$setGestaltLvl(this.getGestaltId(), cappedLvl);
        }
    }

    public net.minecraft.util.Identifier getGestaltId() {
        return net.minecraft.util.Identifier.of("gestaltresonance", "gestalt");
    }

    public int getMaxExp() {
        return 120;
    }

    protected void updateStamina() {
        if (staminaRegenDelay > 0) {
            staminaRegenDelay--;
            return;
        }

        if (owner != null) {
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            if (gp.gestaltresonance$isGuarding()) {
                guardingTicks++;
                // Update guard reduction directly corresponding to stamina percentage
                this.dataTracker.set(GUARD_REDUCTION, getStamina() / getMaxStamina());
                return; // Don't regenerate while guarding
            }
        }
        guardingTicks = 0;
        if (this.dataTracker.get(GUARD_REDUCTION) != 0.0f) {
            this.dataTracker.set(GUARD_REDUCTION, 0.0f);
        }

        float current = getStamina();
        float max = getMaxStamina();
        if (current < max) {
            setStamina(current + getStaminaRegenRate());
        }
    }

    protected float getStaminaRegenRate() {
        return 0.05f; // Refills 1 unit every 20 ticks (1 second)
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
        nbt.putInt("StaminaRegenDelay", this.staminaRegenDelay);
        if (this.ownerUuid != null) {
            nbt.putUuid("OwnerUUID", this.ownerUuid);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("StaminaRegenDelay")) {
            this.staminaRegenDelay = nbt.getInt("StaminaRegenDelay");
        }
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

        this.animationHelper.updateAnimationStates();
        this.abilityHelper.updateAbilities();
        this.setNoGravity(true);
        this.noClip = true;

        // Re-link owner if necessary
        if (this.owner == null || !this.owner.isAlive()) {
            this.getOwner();
        }

        if (!this.getWorld().isClient) {
            if (owner == null || !owner.isAlive()) {
                try {
                    clearOwnerPassiveBuffs(owner);
                } catch (Throwable ignored) {}
                this.discard();
                return;
            }

            // Ensure the owner is still assigned to this type of Gestalt
            Identifier assigned = GestaltAssignments.getAssignedGestalt(owner);
            Identifier thisId = net.minecraft.registry.Registries.ENTITY_TYPE.getId(this.getType());
            if (assigned == null || !assigned.equals(thisId)) {
                try {
                    clearOwnerPassiveBuffs(owner);
                } catch (Throwable ignored) {}
                this.discard();
                return;
            }

            // Apply any passive buffs this Gestalt grants to its owner while active
            try {
                applyOwnerPassiveBuffs(owner);
            } catch (Throwable ignored) {
                // Never let passive buff logic crash the entity tick
            }

            // If guard-dash is active, override normal follow/combat and update dash movement
            if (guardDashActive) {
                updateGuardDash();
            } else if (postDashStickActive) {
                updatePostDashStick();
            } else {
                updateOwnerFollowAndCombat();
            }
            updateStamina();

            // Sync target to client
            int targetId = currentTarget != null ? currentTarget.getId() : -1;
            if (this.dataTracker.get(TARGET_ID) != targetId) {
                this.dataTracker.set(TARGET_ID, targetId);
            }

            // Sync attack state to client
            boolean isAttacking = currentTarget != null && (attackCooldownTicks <= 0 || this.dataTracker.get(IS_PUNCHING));
            if (this.dataTracker.get(IS_ATTACKING) != isAttacking) {
                this.dataTracker.set(IS_ATTACKING, isAttacking);
            }

            // Sync winding up and punching states to client
            if (this.dataTracker.get(IS_WINDING_UP) != (windUpTicks > 0)) {
                this.dataTracker.set(IS_WINDING_UP, windUpTicks > 0);
            }
            if (this.dataTracker.get(IS_PUNCHING) != (punchingTicks > 0)) {
                this.dataTracker.set(IS_PUNCHING, punchingTicks > 0);
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

            // Positioning rule:
            // - Local player's Gestalt: keep client-side positioning unchanged (smooth and affixed to player model).
            // - Other players' Gestalts: do NOT override position client-side; rely on server-authoritative entity updates
            //   so everyone sees the same position/animations.
            if (owner != null && owner.isAlive()) {
                // Determine if this owner is the local client player
                boolean isLocalOwner = false;
                try {
                    net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
                    isLocalOwner = (mc != null && mc.player != null && owner.getId() == mc.player.getId());
                } catch (Throwable ignored) {
                    // In any unexpected context, default to not treating as local to avoid client-side overrides
                    isLocalOwner = false;
                }

                if (isLocalOwner) {
                    // During server-authoritative guard dash or post-dash stick, do not client-side reposition.
                    boolean isGuardDash = this.dataTracker.get(IS_GUARD_DASHING);
                    boolean isSticking = this.dataTracker.get(IS_POST_DASH_STICKING);
                    if (!isGuardDash && !isSticking) {
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
                // else: remote owner's Gestalt → let server-set position interpolate; no client-side repositioning
            }
        }
    }

    @Override
    public void onRemoved() {
        // Ensure passive buffs are always cleared when the Gestalt is removed by any means
        if (!this.getWorld().isClient) {
            try {
                // Resolve owner lazily in case the field is null at removal time
                clearOwnerPassiveBuffs(this.getOwner());
            } catch (Throwable ignored) {
            }
        }
        super.onRemoved();
    }

    /**
     * Public helper to reliably remove any owner-bound passive buffs and then discard this entity.
     * Use this instead of calling {@code discard()} directly when unsummoning.
     */
    public void despawnWithCleanup() {
        if (!this.getWorld().isClient) {
            try {
                clearOwnerPassiveBuffs(this.getOwner());
            } catch (Throwable ignored) {
            }
        }
        this.discard();
    }

    /**
     * Hook for subclasses to apply passive, continuous buffs to the owner while this Gestalt is active.
     * Called server-side every tick after ownership/assignment validation. Default no-op.
     */
    protected void applyOwnerPassiveBuffs(PlayerEntity owner) {
        // default: none
    }

    /**
     * Hook for subclasses to clear any previously applied passive buffs from the owner
     * right before this Gestalt is discarded or deactivated. Default no-op.
     */
    protected void clearOwnerPassiveBuffs(PlayerEntity owner) {
        // default: none
    }

    // ===== Guard Dash Punch ability =====
    protected double getGuardDashRange() { return 10.0; }
    protected double getGuardDashSpeed() { return 0.3; }
    protected float getGuardDashDamageMultiplier() { return 3.0f; }
    protected int getGuardDashStickTicks() { return 20; }
    protected double getGuardDashAutoAimRadius() { return 2.0; }

    public void startGuardDashPunch() {
        if (this.getWorld().isClient) return;
        PlayerEntity owner = getOwner();
        if (owner == null) return;
        // Safety: require sufficient stamina and not already dashing
        if (this.getStamina() < 12.0f || this.guardDashActive) return;

        // Determine dash direction using the player's full look vector (includes pitch & yaw)
        Vec3d look = owner.getRotationVector();
        if (look.lengthSquared() == 0) {
            // Fallback to horizontal forward if somehow zero
            float yaw = owner.getYaw();
            double rad = Math.toRadians(yaw);
            double dx = -Math.sin(rad);
            double dz = Math.cos(rad);
            guardDashDirection = new Vec3d(dx, 0, dz).normalize();
        } else {
            guardDashDirection = look.normalize();
        }
        guardDashDistanceLeft = getGuardDashRange();
        guardDashActive = true;
        guardDashHitDone = false;
        this.dataTracker.set(IS_GUARD_DASHING, true);

        // Initialize animation timers: drive via timers so data tracker sync remains authoritative
        // Start WindUp for dash duration; ensure no active punch
        this.windUpTicks = 1;
        this.punchingTicks = 0;

        // Ensure we are not considered guarding for movement rules
        IGestaltPlayer gp = (IGestaltPlayer) owner;
        gp.gestaltresonance$setGuarding(false);
    }

    private void stopGuardDash() {
        guardDashActive = false;
        guardDashDistanceLeft = 0.0;
        guardDashDirection = Vec3d.ZERO;
        if (!this.getWorld().isClient) {
            this.dataTracker.set(IS_GUARD_DASHING, false);
        }
        // Let timer-based sync clear/maintain animation flags; do not force-stop here.
    }

    private void updateGuardDash() {
        if (this.getWorld().isClient) return;
        if (!guardDashActive) return;

        // Move a step forward
        double step = Math.min(getGuardDashSpeed(), guardDashDistanceLeft);
        Vec3d current = this.getPos();
        // Auto-aim steering: if a valid entity is very close, steer toward it so we can land the hit
        {
            double aimRadius = getGuardDashAutoAimRadius();
            List<LivingEntity> closeEntities = this.getWorld().getEntitiesByClass(
                    LivingEntity.class,
                    this.getBoundingBox().expand(aimRadius),
                    e -> e.isAlive() && e != this && e != this.getOwner()
            );
            if (!closeEntities.isEmpty()) {
                LivingEntity closest = null;
                double closestDistSq = Double.MAX_VALUE;
                for (LivingEntity le : closeEntities) {
                    double d2 = le.squaredDistanceTo(this);
                    if (d2 < closestDistSq) {
                        closestDistSq = d2;
                        closest = le;
                    }
                }
                if (closest != null && closestDistSq <= aimRadius * aimRadius) {
                    Vec3d targetPos = closest.getPos().add(0, Math.max(0.3, closest.getHeight() * 0.5), 0);
                    Vec3d toTarget = targetPos.subtract(current);
                    if (toTarget.lengthSquared() > 1.0E-6) {
                        guardDashDirection = toTarget.normalize();
                    }
                }
            }
        }
        Vec3d next = current.add(guardDashDirection.multiply(step));

        // Face movement direction (update yaw & pitch to align with dash vector)
        double horizLen = Math.sqrt(guardDashDirection.x * guardDashDirection.x + guardDashDirection.z * guardDashDirection.z);
        float faceYaw = (float)(Math.toDegrees(Math.atan2(guardDashDirection.z, guardDashDirection.x)) - 90.0);
        float facePitch = (float)Math.toDegrees(Math.atan2(-guardDashDirection.y, horizLen));
        this.setYaw(faceYaw);
        this.setHeadYaw(faceYaw);
        this.bodyYaw = faceYaw;
        this.prevYaw = faceYaw;
        this.prevHeadYaw = faceYaw;
        this.prevBodyYaw = faceYaw;
        this.setPitch(facePitch);

        // Precise entity collision via raycast along the dash segment
        net.minecraft.util.hit.EntityHitResult entityHit = net.minecraft.entity.projectile.ProjectileUtil.getEntityCollision(
                this.getWorld(),
                this,
                current,
                next,
                this.getBoundingBox().expand(0.2),
                e -> e instanceof LivingEntity && e.isAlive() && e != this && e != this.getOwner() && ((LivingEntity)e).hurtTime <= 0,
                (float) step
        );

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
            // Snap to hit position for visual accuracy
            Vec3d hitPos = entityHit.getPos();
            if (hitPos != null) {
                this.refreshPositionAfterTeleport(hitPos);
            } else {
                this.refreshPositionAfterTeleport(next);
            }

            float damage = getAttackDamage() * getGuardDashDamageMultiplier();
            target.damage(this.getDamageSources().mobAttack(this), damage);

            // On successful hit: drain 12 stamina and grant 2 EXP
            this.setStamina(this.getStamina() - 12.0f);
            this.setExp(this.getExp() + 2);

            // Transition animations via timers: stop WindUp and start Punch
            this.windUpTicks = 0;
            // Stick in place for full animation visibility
            this.punchingTicks = Math.max(this.punchingTicks, getGuardDashStickTicks());

            // Begin post-dash stick phase at the exact hit position
            this.postDashStickPos = this.getPos();
            this.postDashStickTicksLeft = getGuardDashStickTicks();
            this.postDashStickActive = true;
            this.dataTracker.set(IS_POST_DASH_STICKING, true);

            guardDashHitDone = true;
            stopGuardDash();
            return;
        }

        // No hit: advance position along the path
        // Move with no gravity, allow noclip through blocks as Gestalts already do; rely on stop conditions
        this.refreshPositionAfterTeleport(next);

        guardDashDistanceLeft -= step;

        // Drive WindUp timer while dashing so client animates
        if (this.windUpTicks < Integer.MAX_VALUE) {
            this.windUpTicks++;
        }

        // Stop if we reached max distance
        if (guardDashDistanceLeft <= 0.0) {
            // End of dash with no hit: stop WindUp
            this.windUpTicks = 0;
            stopGuardDash();
        }
    }

    private void updatePostDashStick() {
        if (this.getWorld().isClient) return;
        if (!postDashStickActive) return;

        // Keep Gestalt frozen at the recorded hit position
        this.refreshPositionAfterTeleport(postDashStickPos);

        // Drive punch animation timer during stick phase
        if (this.punchingTicks > 0) {
            this.punchingTicks--;
        }

        // Countdown stick phase
        if (postDashStickTicksLeft > 0) {
            postDashStickTicksLeft--;
        }

        if (postDashStickTicksLeft <= 0) {
            postDashStickActive = false;
            this.dataTracker.set(IS_POST_DASH_STICKING, false);
            // End of stick: allow normal follow/combat to resume next tick
        }
    }



    private void updateOwnerFollowAndCombat() {
        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) owner;
        // 0) If ledge grabbing or guarding, don't do combat
        if (gestaltPlayer.gestaltresonance$isLedgeGrabbing() || gestaltPlayer.gestaltresonance$isGuarding()) {
            updatePositionToOwner();
            if (!this.getWorld().isClient) {
                this.dataTracker.set(IS_WINDING_UP, false);
                this.windUpTicks = 0;
                this.dataTracker.set(IS_PUNCHING, false);
                this.punchingTicks = 0;
                this.currentTarget = null;
                this.dataTracker.set(TARGET_ID, -1);
            }
            return;
        }

        // 1) Update target based on owner’s state (attacker first, then what owner is attacking)
        updateTargetFromOwner();

        // 2) If no target, just follow owner
        if (currentTarget == null || !currentTarget.isAlive()) {
            currentTarget = null;
            updatePositionToOwner();
            if (!this.getWorld().isClient) {
                this.dataTracker.set(IS_WINDING_UP, false);
                this.windUpTicks = 0;
                this.dataTracker.set(IS_PUNCHING, false);
                this.punchingTicks = 0;
            }
            return;
        }

        // 3) If target is too far from the OWNER, forget it and go back to owner
        // Exception: don't lose target if we are already in the middle of a punch animation
        double maxRange = getMaxChaseRange();
        double distSqToOwner = owner.squaredDistanceTo(currentTarget);
        if (distSqToOwner > maxRange * maxRange && !this.dataTracker.get(IS_PUNCHING)) {
            currentTarget = null;
            updatePositionToOwner();
            if (!this.getWorld().isClient) {
                this.dataTracker.set(IS_WINDING_UP, false);
                this.windUpTicks = 0;
                this.dataTracker.set(IS_PUNCHING, false);
                this.punchingTicks = 0;
            }
            return;
        }

        // 4) Position logic: move to target only if ready to attack, otherwise stay behind player
        boolean isWindingUp = false;
        boolean isPunching = this.dataTracker.get(IS_PUNCHING);
        if (canMeleeAttack() && (attackCooldownTicks <= 0 || isPunching)) {
            moveNearMeleeTarget();
            if (!isPunching) {
                if (this.windUpTicks < 40) {
                    isWindingUp = true;
                    this.windUpTicks++;
                } else {
                    updatePositionToOwner();
                }
            } else {
                this.windUpTicks = 0;
            }
        } else {
            updatePositionToOwner();
            this.windUpTicks = 0;
        }

        // Sync winding up state to client
        if (this.dataTracker.get(IS_WINDING_UP) != isWindingUp) {
            this.dataTracker.set(IS_WINDING_UP, isWindingUp);
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
            if (attacker instanceof HostileEntity hostile) {
                // Only consider if within chase range
                if (owner.squaredDistanceTo(hostile) <= maxRangeSq) {
                    boolean shouldSwitch = false;

                    if (currentTarget == null || !currentTarget.isAlive()) {
                        shouldSwitch = true;
                    } else {
                        double currentDistSq = owner.squaredDistanceTo(currentTarget);
                        double attackerDistSq = owner.squaredDistanceTo(hostile);
                        // Prefer closer attacker
                        if (attackerDistSq + 0.01 < currentDistSq) {
                            shouldSwitch = true;
                        }
                    }

                    if (shouldSwitch) {
                        currentTarget = hostile;
                        return; // attacker takes full priority
                    }
                }
            }
        }

        // === 2) Fallback: whoever the owner is attacking ===
        {
            var attacking = owner.getAttacking(); // last entity owner attacked
            if (attacking instanceof HostileEntity hostile) {
                if (owner.squaredDistanceTo(hostile) <= maxRangeSq) {
                    if (currentTarget != hostile || !currentTarget.isAlive()) {
                        currentTarget = hostile;
                        return;
                    }
                    if (currentTarget == hostile) return;
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

    protected void applySmoothPosition(double tx, double ty, double tz, float yaw, boolean smooth) {
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
        if (currentTarget == null || !currentTarget.isAlive()) {
            if (punchingTicks > 0) {
                punchingTicks = 0;
                this.dataTracker.set(IS_PUNCHING, false);
            }
            return;
        }

        if (punchingTicks > 0) {
            punchingTicks--;
            if (punchingTicks <= 0) {
                this.dataTracker.set(IS_PUNCHING, false);
            }
        }

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

        if (currentTarget.damage(source, damage)) {
            attackCooldownTicks = getAttackCooldownTicks();
            punchingTicks = 10;
            this.dataTracker.set(IS_PUNCHING, true);
        }
    }

    // Ledge Grab positioning
    public static Vec3d getLedgeGrabPosition(PlayerEntity player, net.minecraft.util.math.BlockPos ledgePos) {
        if (ledgePos == null) {
            return player.getEyePos();
        }

        // Default to top center of the ledge block
        double baseX = ledgePos.getX() + 0.5;
        double baseY = ledgePos.getY() + 1.0; // top surface of the ledge block
        double baseZ = ledgePos.getZ() + 0.5;

        // Determine player facing to push slightly away from wall if side is unknown here
        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);
        double lookX = -Math.sin(rad);
        double lookZ = Math.cos(rad);

        // Outset from wall edge so the Gestalt doesn’t clip into the block
        double edgeOutset = 0.25; // configurable if needed
        double outX = baseX + lookX * edgeOutset;
        double outZ = baseZ + lookZ * edgeOutset;

        // Vertical placement relative to the top surface:
        // If the top is above eye height (3-block case), reduce the drop so it doesn’t look too low.
        double eyeY = player.getEyeY();
        double delta = baseY - eyeY; // how far above eyes the top is (can be negative)
        // Desired hang offset below the top. Clamp between 0.9 and 1.4 blocks.
        double desiredHangOffset = 1.2 - Math.max(0.0, Math.min(delta, 1.5)) * 0.2; // slight reduction when top is high
        desiredHangOffset = Math.max(0.9, Math.min(desiredHangOffset, 1.4));

        double outY = baseY - desiredHangOffset;

        return new Vec3d(outX, outY, outZ);
    }

    public static float getLedgeGrabYaw(net.minecraft.util.math.BlockPos ledgePos, net.minecraft.util.math.Direction ledgeSide) {
        if (ledgeSide != null) {
            return ledgeSide.getOpposite().asRotation();
        }
        return 0; // Should ideally not happen if ledgeSide is provided
    }

    /**
     * Immediately snap the Gestalt to the guarding position in front of its owner,
     * matching the owner's yaw. Used when guard mode is toggled on to avoid smooth
     * interpolation from the previous position.
     */
    public void snapToGuardPosition() {
        if (owner == null) return;

        double playerX = owner.getX();
        double playerY = owner.getY();
        double playerZ = owner.getZ();
        float yaw = owner.getYaw();

        double heightOffset = getHeightOffset();

        double frontOffset = 0.8;
        double rad = Math.toRadians(yaw);
        double frontX = -Math.sin(rad);
        double frontZ = Math.cos(rad);

        double targetX = playerX + frontOffset * frontX;
        double targetZ = playerZ + frontOffset * frontZ;
        double targetY = playerY + heightOffset - 0.5;

        // Teleport/snap to position and align yaw instantly
        this.refreshPositionAndAngles(targetX, targetY, targetZ, yaw, this.getPitch());
        this.setHeadYaw(yaw);
        this.setBodyYaw(yaw);
        this.setVelocity(Vec3d.ZERO);
    }

    // ===== Following behavior (can be overridden per stand) =====
    protected void updatePositionToOwner() {
        if (owner == null) return;

        double playerX = owner.getX();
        double playerY = owner.getY();
        double playerZ = owner.getZ();
        float yaw = owner.getYaw();
        double heightOffset = getHeightOffset();

        IGestaltPlayer gp = (IGestaltPlayer) owner;
        boolean isGuarding = gp.gestaltresonance$isGuarding();
        boolean isLedgeGrabbing = gp.gestaltresonance$isLedgeGrabbing();
        boolean isThrowing = gp.gestaltresonance$isGestaltThrowActive();

        if (isThrowing) {
            // Throw mode: Directly behind the player
            double backOffset = -0.5;
            double rad = Math.toRadians(yaw);
            double backX = -Math.sin(rad);
            double backZ = Math.cos(rad);

            double targetX = playerX + backOffset * backX;
            double targetZ = playerZ + backOffset * backZ;
            double targetY = playerY + heightOffset;

            applySmoothPosition(targetX, targetY, targetZ, yaw, false);
            return;
        }

        if (isLedgeGrabbing) {
            net.minecraft.util.math.BlockPos ledgePos = gp.gestaltresonance$getLedgeGrabPos();
            net.minecraft.util.math.Direction ledgeSide = gp.gestaltresonance$getLedgeGrabSide();

            if (ledgePos != null && ledgeSide != null && ledgeSide.getAxis().isHorizontal()) {
                // Fixed snap to block face (no smoothing)
                // Adjust these if you want to tweak spacing/height:
                final double distanceFromFace = 1.2; // blocks away from the block face (user-requested approx)
                final double verticalDrop = 2.4;     // blocks below the top of the block (user-requested approx)

                // Block center and normal of the face we are grabbing
                Vec3d center = new Vec3d(ledgePos.getX() + 0.5, ledgePos.getY() + 0.5, ledgePos.getZ() + 0.5);
                Vec3d normal = Vec3d.of(ledgeSide.getVector()); // points outward from the face we hit

                // From block center to the face plane is 0.5; we then go an extra `distanceFromFace` beyond it
                double faceOffsetFromCenter = 0.5 + distanceFromFace; // == 1.0 when distanceFromFace == 0.5

                double outX = center.x + normal.x * faceOffsetFromCenter;
                double outZ = center.z + normal.z * faceOffsetFromCenter;
                double outY = ledgePos.getY() + 1.0 - verticalDrop; // slightly below block top

                float targetYaw = ledgeSide.getOpposite().asRotation(); // face toward the block

                // Hard snap (no smoothing) and align rotation
                this.refreshPositionAndAngles(outX, outY, outZ, targetYaw, this.getPitch());
                this.setHeadYaw(targetYaw);
                this.setBodyYaw(targetYaw);
                this.setVelocity(Vec3d.ZERO);
            } else {
                // Fallback to previous behavior if data missing
                Vec3d targetPos = getLedgeGrabPosition(owner, ledgePos);
                float targetYaw = getLedgeGrabYaw(ledgePos, ledgeSide);
                applySmoothPosition(targetPos.x, targetPos.y, targetPos.z, targetYaw, false);
            }
            return;
        }

        if (isGuarding) {
            // Guard mode: Locked in front of player
            double frontOffset = 0.8;
            double rad = Math.toRadians(yaw);
            double frontX = -Math.sin(rad);
            double frontZ = Math.cos(rad);

            double targetX = playerX + frontOffset * frontX;
            double targetZ = playerZ + frontOffset * frontZ;
            double targetY = playerY + heightOffset - 0.5;

            applySmoothPosition(targetX, targetY, targetZ, yaw, false);
        } else {
            // Normal mode: Slightly behind and to the side
            double backOffset = getFollowBackOffset();
            double sideOffset = getFollowSideOffset();

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

    protected double getHeightOffset() {
        return 0.3;
    }

    protected double getFollowBackOffset() {
        return -0.9;
    }

    protected double getFollowSideOffset() {
        return 0.9;
    }


    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        return this.damageHelper.handleDamage(source, amount);
    }

    @Override
    public void setVelocity(Vec3d velocity) {
        // Prevent knockback from being applied to the Gestalt
        super.setVelocity(Vec3d.ZERO);
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.entity.damage.DamageSource source) {
        if (this.damageHelper.isInvulnerableTo(source)) return true;
        return super.isInvulnerableTo(source);
    }
}