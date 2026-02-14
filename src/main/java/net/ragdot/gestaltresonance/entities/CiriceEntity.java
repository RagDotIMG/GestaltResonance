package net.ragdot.gestaltresonance.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.block.ModBlocks;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

import java.util.*;

public class CiriceEntity extends Entity {
    private static final TrackedData<Integer> LIFESPAN = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> P1_X = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> P1_Y = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> P1_Z = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> P2_X = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> P2_Y = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> P2_Z = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> P3_X = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> P3_Y = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> P3_Z = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(CiriceEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private UUID ownerUuid;
    private final Map<BlockPos, BlockState> replacedBlocks = new HashMap<>();
    private final List<PufferfishData> pufferfish = new ArrayList<>();
    private boolean initialized = false;

    public CiriceEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(LIFESPAN, 0);
        builder.add(P1_X, 0f); builder.add(P1_Y, 0f); builder.add(P1_Z, 0f);
        builder.add(P2_X, 0f); builder.add(P2_Y, 0f); builder.add(P2_Z, 0f);
        builder.add(P3_X, 0f); builder.add(P3_Y, 0f); builder.add(P3_Z, 0f);
        builder.add(OWNER_UUID, Optional.empty());
    }

    public void setLifespan(int ticks) {
        this.dataTracker.set(LIFESPAN, ticks);
    }

    public int getLifespan() {
        return this.dataTracker.get(LIFESPAN);
    }

    public void setOwner(PlayerEntity owner) {
        this.ownerUuid = owner.getUuid();
        this.dataTracker.set(OWNER_UUID, Optional.of(ownerUuid));
    }

    @Override
    public void tick() {
        super.tick();

        // Lock owner in place
        PlayerEntity owner = getOwner();
        if (owner != null) {
            owner.setPos(this.getX(), this.getY(), this.getZ());
            owner.setVelocity(Vec3d.ZERO);
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 2, 255, false, false, false));
            
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            gp.gestaltresonance$setIncapacitated(true);
        }

        if (getWorld().isClient) {
            if (!initialized) {
                initializeSphere();
                initialized = true;
            }
            return;
        }

        int life = getLifespan();
        if (life <= 0) {
            if (!getWorld().isClient) {
                cleanupBlocks();
            }
            this.discard();
            return;
        }

        if (!initialized) {
            initializeSphere();
            initialized = true;
        }

        handleEffects();
        updatePufferfish();

        setLifespan(life - 1);
    }

    private void cleanupBlocks() {
        for (Map.Entry<BlockPos, BlockState> entry : replacedBlocks.entrySet()) {
            if (getWorld().getBlockState(entry.getKey()).isOf(ModBlocks.CIRICE_BLOCK)) {
                getWorld().setBlockState(entry.getKey(), entry.getValue());
            }
        }
        getWorld().playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.AMBIENT, 1.0f, 1.0f);
        
        PlayerEntity owner = getOwner();
        if (owner != null) {
            IGestaltPlayer gp = (IGestaltPlayer) owner;
            gp.gestaltresonance$setIncapacitated(false);
        }
    }

    public PlayerEntity getOwner() {
        if (ownerUuid == null) return null;
        return getWorld().getPlayerByUuid(ownerUuid);
    }

    private void initializeSphere() {
        // No longer placing real blocks, just initializing pufferfish
        for (int i = 0; i < 3; i++) {
            pufferfish.add(new PufferfishData(getPos().add(
                random.nextDouble() * 4 - 2,
                random.nextDouble() * 4 - 2,
                random.nextDouble() * 4 - 2
            )));
        }
    }

    private void handleEffects() {
        Box sphereBox = getBoundingBox().expand(5.0);
        List<Entity> entities = getWorld().getOtherEntities(this, sphereBox);
        PlayerEntity owner = null;
        if (ownerUuid != null) {
            owner = getWorld().getPlayerByUuid(ownerUuid);
        }

        if (owner != null && owner.getPos().distanceTo(getPos()) <= 5.5) {
            // Fix owner in place
            owner.setVelocity(Vec3d.ZERO);
            owner.velocityDirty = true;
            // Similar to Futurama fake death, we might need a way to prevent action if required, 
            // but for now, fixing in place.
        }

        for (Entity entity : entities) {
            if (entity.getPos().distanceTo(getPos()) > 5.5) continue;

            if (entity instanceof LivingEntity living) {
                // Handle custom poison damage
                Set<String> tags = living.getCommandTags();
                for (String tag : tags) {
                    if (tag.startsWith("cirice_poisoned_")) {
                        try {
                            int ticks = Integer.parseInt(tag.substring("cirice_poisoned_".length()));
                            if (ticks < 600) {
                                if (ticks % 5 == 0) {
                                    living.damage(getWorld().getDamageSources().magic(), 4.0f); // 2 hearts
                                }
                                living.removeCommandTag(tag);
                                living.addCommandTag("cirice_poisoned_" + (ticks + 1));
                            } else {
                                living.removeCommandTag(tag);
                            }
                        } catch (NumberFormatException ignored) {}
                        break;
                    }
                }
            }

            if (entity instanceof HostileEntity hostile) {
                hostile.setVelocity(Vec3d.ZERO);
                hostile.velocityDirty = true;
                // Hostile mobs locked in place
            } else if (entity instanceof PlayerEntity player) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 21, 0, false, false, true)); // ~10% speed buff is Speed I (20%) or we can use attributes
                if (age % 20 == 0) {
                    player.heal(2.0f); // 1 heart = 2.0f
                }
            }
        }
    }

    private void updatePufferfish() {
        Box sphereBox = getBoundingBox().expand(5.0);
        List<HostileEntity> hostiles = getWorld().getEntitiesByClass(HostileEntity.class, sphereBox, h -> h.getPos().distanceTo(getPos()) <= 5.5);

        for (int i = 0; i < pufferfish.size(); i++) {
            PufferfishData fish = pufferfish.get(i);
            if (fish.target == null || !fish.target.isAlive() || fish.target.getPos().distanceTo(getPos()) > 5.5 || fish.hasAttackedTarget(fish.target)) {
                fish.target = findNewTarget(hostiles, fish);
            }

            if (fish.target != null) {
                Vec3d dir = fish.target.getPos().add(0, fish.target.getHeight()/2, 0).subtract(fish.pos).normalize();
                fish.pos = fish.pos.add(dir.multiply(0.3));
                
                if (fish.pos.distanceTo(fish.target.getPos().add(0, fish.target.getHeight()/2, 0)) < 1.0) {
                    applyPoison(fish.target);
                    fish.markAttacked(fish.target);
                    fish.target = null;
                }
            } else {
                // Idle movement: Circle around the center (where the user is)
                Vec3d center = getPos();
                Vec3d toFish = fish.pos.subtract(center);
                double dist = toFish.horizontalLength();
                
                // Desired orbit radius: between 2 and 4 blocks
                double targetRadius = 3.0 + Math.sin(age * 0.05 + i * 1.5) * 1.0;
                
                // Horizontal orbit
                double angle = Math.atan2(toFish.z, toFish.x);
                angle += 0.02; // Orbit speed
                
                double nextX = Math.cos(angle) * targetRadius;
                double nextZ = Math.sin(angle) * targetRadius;
                
                // Vertical oscillation
                double nextY = Math.sin(age * 0.03 + i * 2.0) * 1.5;
                
                Vec3d targetPos = center.add(nextX, nextY, nextZ);
                Vec3d moveDir = targetPos.subtract(fish.pos);
                
                fish.velocity = fish.velocity.add(moveDir.multiply(0.01));
                fish.velocity = fish.velocity.multiply(0.9);
                fish.pos = fish.pos.add(fish.velocity);
            }

            // Sync to DataTracker
            switch(i) {
                case 0 -> { dataTracker.set(P1_X, (float)fish.pos.x); dataTracker.set(P1_Y, (float)fish.pos.y); dataTracker.set(P1_Z, (float)fish.pos.z); }
                case 1 -> { dataTracker.set(P2_X, (float)fish.pos.x); dataTracker.set(P2_Y, (float)fish.pos.y); dataTracker.set(P2_Z, (float)fish.pos.z); }
                case 2 -> { dataTracker.set(P3_X, (float)fish.pos.x); dataTracker.set(P3_Y, (float)fish.pos.y); dataTracker.set(P3_Z, (float)fish.pos.z); }
            }
        }
    }

    public Vec3d getPufferfishPos(int index) {
        return switch(index) {
            case 0 -> new Vec3d(dataTracker.get(P1_X), dataTracker.get(P1_Y), dataTracker.get(P1_Z));
            case 1 -> new Vec3d(dataTracker.get(P2_X), dataTracker.get(P2_Y), dataTracker.get(P2_Z));
            case 2 -> new Vec3d(dataTracker.get(P3_X), dataTracker.get(P3_Y), dataTracker.get(P3_Z));
            default -> getPos();
        };
    }

    private HostileEntity findNewTarget(List<HostileEntity> hostiles, PufferfishData fish) {
        return hostiles.stream()
            .filter(h -> !fish.hasAttackedTarget(h))
            .min(Comparator.comparingDouble(h -> h.getPos().squaredDistanceTo(fish.pos)))
            .orElse(null);
    }

    private void applyPoison(LivingEntity target) {
        // dealing 2 heart of damage every 5 ticks for a total of 600 ticks (30 seconds)
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 600, 3, false, false, true)); 
        // Wither IV (amplifier 3) deals 1 damage (0.5 heart) every 10 ticks? No, it's faster.
        // Actually, to match "2 heart (4 damage) every 5 ticks", we really need custom logic.
        target.addCommandTag("cirice_poisoned_0"); // We'll use this to track ticks in handleEffects
    }

    @Override
    public void onRemoved() {
        if (!getWorld().isClient) {
            cleanupBlocks();
        }
        super.onRemoved();
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Lifespan")) setLifespan(nbt.getInt("Lifespan"));
        if (nbt.contains("Owner")) ownerUuid = nbt.getUuid("Owner");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Lifespan", getLifespan());
        if (ownerUuid != null) nbt.putUuid("Owner", ownerUuid);
    }

    private static class PufferfishData {
        Vec3d pos;
        Vec3d velocity = Vec3d.ZERO;
        HostileEntity target;
        Set<UUID> attackedTargets = new HashSet<>();

        PufferfishData(Vec3d pos) {
            this.pos = pos;
        }

        boolean hasAttackedTarget(LivingEntity entity) {
            return attackedTargets.contains(entity.getUuid());
        }

        void markAttacked(LivingEntity entity) {
            attackedTargets.add(entity.getUuid());
        }
    }
}
