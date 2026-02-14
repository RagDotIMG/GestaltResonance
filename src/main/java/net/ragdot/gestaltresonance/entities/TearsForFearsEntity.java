package net.ragdot.gestaltresonance.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.Gestaltresonance;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TearsForFearsEntity extends Entity {
    private static final TrackedData<Integer> AGE = DataTracker.registerData(TearsForFearsEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public int age;
    private UUID ownerUuid;
    private int spillwayLevel = 1;
    private Vec3d targetPos;
    private LivingEntity targetEntity;
    private boolean falling = false;

    public TearsForFearsEntity(EntityType<? extends TearsForFearsEntity> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    public void setOwner(PlayerEntity owner) {
        if (owner != null) {
            this.ownerUuid = owner.getUuid();
        }
    }

    public void setSpillwayLevel(int level) {
        this.spillwayLevel = level;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(AGE, 0);
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.age = nbt.getInt("Age");
        if (nbt.contains("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        this.spillwayLevel = nbt.getInt("SpillwayLevel");
        this.falling = nbt.getBoolean("Falling");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Age", this.age);
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
        nbt.putInt("SpillwayLevel", this.spillwayLevel);
        nbt.putBoolean("Falling", this.falling);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            this.age++;
            this.move(MovementType.SELF, this.getVelocity());
            return;
        }

        if (this.age == 0) {
            // No sound here, Spillways.java handles it
        }

        this.age++;
        this.dataTracker.set(AGE, this.age);

        if (this.age > 300) { // 15 seconds
            this.pop();
            return;
        }

        if (this.age <= 40) { // 2 seconds delay
            this.initialBehavior();
        } else if (!this.falling) {
            this.homingBehavior();
        } else {
            this.fallingBehavior();
        }
    }

    private void initialBehavior() {
        if (this.targetPos == null) {
            // Target is 1 block forward and +0.5 blocks up from current height (reaching 1.5 blocks height upwards) relative to spawn
            this.targetPos = this.getPos().add(this.getRotationVector().multiply(1.0)).add(0, 0.5, 0);
        }

        Vec3d currentPos = this.getPos();
        Vec3d diff = this.targetPos.subtract(currentPos);
        if (diff.length() > 0.1) {
            Vec3d desiredVelocity = diff.normalize().multiply(0.04); // Slowed down by 60% (0.1 * 0.4 = 0.04)
            Vec3d currentVelocity = this.getVelocity();
            double lerpFactor = 0.05;
            this.setVelocity(
                    currentVelocity.x + (desiredVelocity.x - currentVelocity.x) * lerpFactor,
                    currentVelocity.y + (desiredVelocity.y - currentVelocity.y) * lerpFactor,
                    currentVelocity.z + (desiredVelocity.z - currentVelocity.z) * lerpFactor
            );
        } else {
            // Bobbing animation
            double bob = Math.sin(this.age * 0.2) * 0.02;
            this.setVelocity(0, bob, 0);
        }
        this.move(MovementType.SELF, this.getVelocity());
    }

    private void homingBehavior() {
        if (this.targetEntity == null || !this.targetEntity.isAlive()) {
            this.targetEntity = findClosestTarget();
        }

        if (this.targetEntity != null) {
            Vec3d diff = this.targetEntity.getEyePos().subtract(this.getPos());
            if (diff.length() < 0.5) {
                this.onImpact(this.targetEntity);
                return;
            }
            
            Vec3d desiredVelocity = diff.normalize().multiply(0.08); // Slowed down by 60% (0.2 * 0.4 = 0.08)
            Vec3d currentVelocity = this.getVelocity();
            
            // Smoother movement: interpolate towards desired velocity
            double lerpFactor = 0.1;
            
            // Add slight bobbing to movement
            double bob = Math.sin(this.age * 0.2) * 0.01;
            
            this.setVelocity(
                    currentVelocity.x + (desiredVelocity.x - currentVelocity.x) * lerpFactor,
                    currentVelocity.y + (desiredVelocity.y - currentVelocity.y) * lerpFactor + bob,
                    currentVelocity.z + (desiredVelocity.z - currentVelocity.z) * lerpFactor
            );
            
            this.move(MovementType.SELF, this.getVelocity());
        } else {
            this.falling = true;
        }
    }

    private void fallingBehavior() {
        Vec3d desiredVelocity = new Vec3d(0, -0.02, 0); // Slowed down by 60% (0.05 * 0.4 = 0.02)
        Vec3d currentVelocity = this.getVelocity();
        double lerpFactor = 0.05;
        
        // Add slight bobbing even while falling
        double bob = Math.sin(this.age * 0.2) * 0.01;
        
        this.setVelocity(
                currentVelocity.x + (desiredVelocity.x - currentVelocity.x) * lerpFactor,
                currentVelocity.y + (desiredVelocity.y - currentVelocity.y) * lerpFactor + bob,
                currentVelocity.z + (desiredVelocity.z - currentVelocity.z) * lerpFactor
        );
        this.move(MovementType.SELF, this.getVelocity());
        if (this.isOnGround() || this.getWorld().getBlockState(this.getBlockPos()).isSolidBlock(this.getWorld(), this.getBlockPos())) {
            this.pop();
        }
    }

    private LivingEntity findClosestTarget() {
        List<LivingEntity> entities = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(10), 
            e -> e.isAlive() 
                && (this.ownerUuid == null || !e.getUuid().equals(this.ownerUuid))
                && !(e instanceof GestaltBase));
        
        if (entities.isEmpty()) return null;

        // Priority 1: Players
        LivingEntity target = findClosestInList(entities.stream().filter(e -> e instanceof PlayerEntity).collect(Collectors.toList()));
        if (target != null) return target;

        // Priority 2: Passive Mobs
        target = findClosestInList(entities.stream().filter(e -> e instanceof PassiveEntity).collect(Collectors.toList()));
        if (target != null) return target;

        // Priority 3: Neutral Mobs (remaining mobs that are not hostile)
        // Actually, the prompt says: priorities players then passive mobs then neutral mobs
        // So we need to distinguish between neutral and hostile if possible.
        // In Minecraft, "Neutral" often refers to mobs like wolves or iron golems.
        // If we filter out HostileEntity, what's left are Players, PassiveEntity, and "Neutral" entities (like Golems, Neutral mobs).
        // Since we already checked Players and PassiveEntity, we can check for non-hostile mobs.
        
        target = findClosestInList(entities.stream()
                .filter(e -> !(e instanceof HostileEntity) && !(e instanceof PlayerEntity) && !(e instanceof PassiveEntity))
                .collect(Collectors.toList()));
        if (target != null) return target;

        // Final fallback: any remaining (Hostile mobs)
        return findClosestInList(entities);
    }

    private LivingEntity findClosestInList(List<LivingEntity> list) {
        LivingEntity closest = null;
        double minDist = Double.MAX_VALUE;
        for (LivingEntity e : list) {
            double dist = e.squaredDistanceTo(this);
            if (dist < minDist) {
                minDist = dist;
                closest = e;
            }
        }
        return closest;
    }

    private void onImpact(LivingEntity entity) {
        float scale = 1.0f + 0.5f * (this.spillwayLevel - 1);
        
        // Extinguish fire on mob
        if (entity.isOnFire()) {
            entity.extinguish();
        }

        if (entity instanceof HostileEntity) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100));
            entity.damage(this.getWorld().getDamageSources().magic(), 2.0f * scale); // 1 heart = 2 damage
            this.popSound();
        } else {
            // Clear negative buffs
            List<StatusEffectInstance> toRemove = entity.getStatusEffects().stream()
                    .filter(effect -> effect.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL)
                    .collect(Collectors.toList());
            for (StatusEffectInstance effect : toRemove) {
                entity.removeStatusEffect(effect.getEffectType());
            }

            entity.heal(2.0f * scale); // 1 heart = 2 healing

            if (entity instanceof PlayerEntity player) {
                player.setAir(player.getMaxAir());
            } else if (entity instanceof WaterCreatureEntity || entity.getType().equals(EntityType.SQUID) || entity.getType().equals(EntityType.DOLPHIN)) {
                // Apply Water Breathing and Land Protection for 1 minute (1200 ticks) to prevent land suffocation
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 1200, 0));
                entity.addStatusEffect(new StatusEffectInstance(net.ragdot.gestaltresonance.effect.ModStatusEffects.LAND_PROTECTION, 1200, 0));
            }

            this.expSound();
        }

        this.discard();
    }

    private void pop() {
        if (this.falling || this.age >= 300) {
            this.splashSound();
            this.expSound();
            handleBlockInteractions();
            applyBoneMealEffect();
        } else {
            this.popSound();
        }
        this.discard();
    }

    private void handleBlockInteractions() {
        BlockPos pos = this.getBlockPos();
        World world = this.getWorld();
        
        // Extinguish fire on block
        if (world.getBlockState(pos).isOf(Blocks.FIRE)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        // Check the block it hit (at or below)
        BlockPos[] targets = {pos, pos.down()};
        for (BlockPos targetPos : targets) {
            BlockState state = world.getBlockState(targetPos);
            
            if (state.isOf(Blocks.OBSIDIAN)) {
                if (world.random.nextFloat() < 0.10f) {
                    world.setBlockState(targetPos, Blocks.CRYING_OBSIDIAN.getDefaultState());
                }
            } else if (state.isOf(Blocks.SPONGE)) {
                world.setBlockState(targetPos, Blocks.WET_SPONGE.getDefaultState());
            } else if (state.isOf(Blocks.DIRT)) {
                if (world.random.nextFloat() < 0.10f) {
                    world.setBlockState(targetPos, Blocks.MUD.getDefaultState());
                }
            } else if (state.isOf(Blocks.MUD)) {
                if (world.random.nextFloat() < 0.05f) {
                    world.setBlockState(targetPos, Blocks.CLAY.getDefaultState());
                }
            } else if (state.isOf(Blocks.COBBLESTONE)) {
                if (world.random.nextFloat() < 0.10f) {
                    world.setBlockState(targetPos, Blocks.MOSSY_COBBLESTONE.getDefaultState());
                }
            } else if (state.isOf(Blocks.STONE_BRICKS)) {
                if (world.random.nextFloat() < 0.10f) {
                    world.setBlockState(targetPos, Blocks.MOSSY_STONE_BRICKS.getDefaultState());
                }
            } else if (state.isOf(Blocks.CACTUS)) {
                if (world.random.nextFloat() < 0.20f) {
                    BlockPos above = targetPos.up();
                    if (world.getBlockState(above).isAir()) {
                        // Using PINK_PETALS as a placeholder for "Cactus Flower" if it doesn't exist.
                        // In 1.20+ Pink Petals are a thing.
                        world.setBlockState(above, Blocks.PINK_PETALS.getDefaultState());
                    }
                }
            }
        }
    }

    private void splashSound() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.NEUTRAL, 0.5f, 1.5f);
    }

    private void expSound() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 0.5f, 1.0f);
    }

    private void popSound() {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 0.5f, 2.0f);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }

    private void applyBoneMealEffect() {
        BlockPos center = this.getBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = center.add(x, 0, z);
                // Try applying bone meal
                BoneMealItem.useOnFertilizable(new ItemStack(Items.BONE_MEAL), this.getWorld(), pos);
                // Also try one block down in case it's floating slightly
                BoneMealItem.useOnFertilizable(new ItemStack(Items.BONE_MEAL), this.getWorld(), pos.down());
            }
        }
    }
}
