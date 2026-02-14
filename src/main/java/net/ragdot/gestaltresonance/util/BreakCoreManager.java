package net.ragdot.gestaltresonance.util;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.Gestaltresonance;

import java.util.EnumSet;
import java.util.List;

public class BreakCoreManager {

    public static void tick(ServerWorld world) {
        // Process all marked entities in the world.
        // We iterate through all entities of the world to find marked ones.
        for (net.minecraft.entity.Entity entity : world.iterateEntities()) {
            if (entity instanceof LivingEntity living && entity.getCommandTags().contains("gestaltresonance$break_core_marked")) {
                processMarkedEntity(living);
            }
        }
    }

    private static void processMarkedEntity(LivingEntity entity) {
        if (entity.getWorld().isClient) return;

        int timer = getMarkedTimer(entity);
        if (timer < 200) {
            setMarkedTimer(entity, timer + 1);
            return;
        }

        // Timer ended, apply behavior
        int behaviorTimer = getBehaviorTimer(entity);
        setBehaviorTimer(entity, behaviorTimer + 1);

        // Fail-safe: 30 seconds after behavior started = 600 ticks
        if (behaviorTimer >= 600) {
            triggerFailSafeExplosion(entity);
            return;
        }

        if (entity instanceof MobEntity mob) {
             // We can't easily stop goals without a mixin, but we can try to force navigation more aggressively.
             applyBreakCoreBehavior(mob);
        } else {
            // Non-mob living entities just stand still and take damage
            entity.getVelocity().multiply(0, 1, 0); // slow down horizontally
            applyDamage(entity);
        }
    }

    private static int getMarkedTimer(LivingEntity entity) {
        for (String tag : entity.getCommandTags()) {
            if (tag.startsWith("gestaltresonance$break_core_timer:")) {
                try {
                    return Integer.parseInt(tag.substring("gestaltresonance$break_core_timer:".length()));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private static void setMarkedTimer(LivingEntity entity, int ticks) {
        entity.getCommandTags().removeIf(tag -> tag.startsWith("gestaltresonance$break_core_timer:"));
        entity.addCommandTag("gestaltresonance$break_core_timer:" + ticks);
    }

    private static int getBehaviorTimer(LivingEntity entity) {
        for (String tag : entity.getCommandTags()) {
            if (tag.startsWith("gestaltresonance$break_core_behavior_timer:")) {
                try {
                    return Integer.parseInt(tag.substring("gestaltresonance$break_core_behavior_timer:".length()));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private static void setBehaviorTimer(LivingEntity entity, int ticks) {
        entity.getCommandTags().removeIf(tag -> tag.startsWith("gestaltresonance$break_core_behavior_timer:"));
        entity.addCommandTag("gestaltresonance$break_core_behavior_timer:" + ticks);
    }

    private static void triggerFailSafeExplosion(LivingEntity entity) {
        ServerWorld world = (ServerWorld) entity.getWorld();
        // Fixed damage of 150, no terrain damage
        world.createExplosion(null, entity.getX(), entity.getY(), entity.getZ(), 0.0f, false, World.ExplosionSourceType.NONE);
        entity.damage(entity.getDamageSources().explosion(null, null), 150.0f);
        
        // Remove tags so we don't repeat this
        entity.getCommandTags().remove("gestaltresonance$break_core_marked");
        entity.getCommandTags().removeIf(tag -> tag.startsWith("gestaltresonance$break_core_timer:"));
        entity.getCommandTags().removeIf(tag -> tag.startsWith("gestaltresonance$break_core_behavior_timer:"));
        entity.getCommandTags().removeIf(tag -> tag.startsWith("gestaltresonance$break_core_target_block:"));
        entity.getCommandTags().removeIf(tag -> tag.startsWith("gestaltresonance$break_core_priority:"));
        
        if (entity.isDead()) {
            if (shouldSpawnPopSprout(entity)) {
                spawnPopSprout(entity);
            }
        } else {
            // Force death if still alive somehow
            entity.setHealth(0.0f);
            if (shouldSpawnPopSprout(entity)) {
                spawnPopSprout(entity);
            }
        }
    }

    private static void applyBreakCoreBehavior(MobEntity mob) {
        BlockPos currentTargetBlock = getStoredTargetBlock(mob);
        int priority = getPriority(mob);

        // Every 5 seconds (100 ticks), we reset the priority and force a full target recheck.
        // This allows switching to a higher priority target if it becomes available.
        boolean forceRecheck = mob.age % 100 == 0;
        if (forceRecheck) {
            priority = 0;
            setPriority(mob, 0);
            currentTargetBlock = null;
            clearStoredTargetBlock(mob);
            mob.setTarget(null);
        }

        // If we have a stored block, check if it's still valid
        if (currentTargetBlock != null) {
            boolean isAir = mob.getWorld().getBlockState(currentTargetBlock).isAir();
            boolean isSpecialMob = mob instanceof ZombieEntity || mob instanceof SkeletonEntity;
            if (isAir && !isSpecialMob) {
                // Target block is gone, clear it and try to find a new one at SAME priority
                currentTargetBlock = null;
                clearStoredTargetBlock(mob);
            }
        }

        // Try to find a target if we don't have one
        if (currentTargetBlock == null) {
            // Priority cascading: try current priority, then move down if none found or unreachable
            while (priority < 5) {
                BlockPos newTarget = findTarget(mob, priority);
                if (newTarget != null) {
                    // Check if the new target is reachable
                    var path = mob.getNavigation().findPathTo(newTarget, 1);
                    if (path != null && path.reachesTarget()) {
                        currentTargetBlock = newTarget;
                        setStoredTargetBlock(mob, newTarget);
                        break; // Found a valid target
                    }
                }
                // No target at this priority or unreachable, move to next
                priority++;
                setPriority(mob, priority);
            }
        }

        if (currentTargetBlock != null) {
            if (mob.getBlockPos().getSquaredDistance(currentTargetBlock) < 2.0) {
                // We've reached the target block (lava, fire, etc.), apply damage and stop.
                applyDamage(mob);
                mob.getNavigation().stop();
            } else {
                mob.getNavigation().startMovingTo(currentTargetBlock.getX(), currentTargetBlock.getY(), currentTargetBlock.getZ(), 1.2);
            }
            mob.setTarget(null);
        } else {
            // All block priorities failed or no targets found, try attacking or just take damage
            if (priority >= 5) {
                LivingEntity nearestHostile = findNearestHostile(mob);
                if (nearestHostile != null) {
                    mob.setTarget(nearestHostile);
                } else {
                    // No blocks, no hostiles -> Stand still and take damage
                    mob.getNavigation().stop();
                    applyDamage(mob);
                }
            }
        }
    }

    private static BlockPos getStoredTargetBlock(MobEntity mob) {
        for (String tag : mob.getCommandTags()) {
            if (tag.startsWith("gestaltresonance$break_core_target_block:")) {
                String[] parts = tag.substring("gestaltresonance$break_core_target_block:".length()).split(",");
                if (parts.length == 3) {
                    try {
                        return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private static void setStoredTargetBlock(MobEntity mob, BlockPos pos) {
        clearStoredTargetBlock(mob);
        mob.addCommandTag("gestaltresonance$break_core_target_block:" + pos.getX() + "," + pos.getY() + "," + pos.getZ());
    }

    private static void clearStoredTargetBlock(MobEntity mob) {
        mob.getCommandTags().removeIf(tag -> tag.startsWith("gestaltresonance$break_core_target_block:"));
    }

    private static int getPriority(MobEntity mob) {
        for (String tag : mob.getCommandTags()) {
            if (tag.startsWith("gestaltresonance$break_core_priority:")) {
                try {
                    return Integer.parseInt(tag.substring("gestaltresonance$break_core_priority:".length()));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private static void setPriority(MobEntity mob, int priority) {
        mob.getCommandTags().removeIf(tag -> tag.startsWith("gestaltresonance$break_core_priority:"));
        mob.addCommandTag("gestaltresonance$break_core_priority:" + priority);
    }

    private static BlockPos findTarget(MobEntity mob, int priority) {
        BlockPos pos = mob.getBlockPos();
        ServerWorld world = (ServerWorld) mob.getWorld();

        if (priority <= 0) {
            // 1. Lava (within 25 blocks)
            BlockPos lava = findBlock(world, pos, 25, Blocks.LAVA);
            if (lava != null) return lava;
        }

        if (priority <= 1) {
            // 2. Fire
            BlockPos fire = findBlock(world, pos, 25, Blocks.FIRE);
            if (fire == null) fire = findBlock(world, pos, 25, Blocks.SOUL_FIRE);
            if (fire != null) return fire;
        }

        if (priority <= 2) {
            // 3. Ledge Drop
            BlockPos ledge = findLethalDrop(world, pos, 25, (int) mob.getHealth() + 3);
            if (ledge != null) return ledge;
        }

        if (priority <= 3) {
            // 4. Cactus, Sweet Berry Bush, Campfire
            BlockPos hazard = findBlock(world, pos, 25, Blocks.CACTUS);
            if (hazard == null) hazard = findBlock(world, pos, 25, Blocks.SWEET_BERRY_BUSH);
            if (hazard == null) hazard = findBlock(world, pos, 25, Blocks.CAMPFIRE);
            if (hazard == null) hazard = findBlock(world, pos, 25, Blocks.SOUL_CAMPFIRE);
            if (hazard != null) return hazard;
        }

        if (priority <= 4) {
            // 5. Daylight (Zombie/Skeleton)
            if (mob instanceof ZombieEntity || mob instanceof SkeletonEntity) {
                if (world.isDay() && !world.isRaining()) {
                    // Seek a position with sky access
                    for (int i = 0; i < 20; i++) {
                        BlockPos randomPos = pos.add(world.random.nextInt(30) - 15, world.random.nextInt(10) - 5, world.random.nextInt(30) - 15);
                        if (world.isSkyVisible(randomPos)) return randomPos;
                    }
                }
            }
        }

        return null;
    }

    private static BlockPos findLethalDrop(ServerWorld world, BlockPos center, int radius, int lethalDistance) {
        // Look for a block nearby that has a significant air drop below it.
        // We scan for a solid block that has air adjacent to it and a deep fall.
        for (int r = 1; r <= radius; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (Math.abs(x) >= r - 1 || Math.abs(z) >= r - 1) {
                        BlockPos checkPos = center.add(x, 0, z);
                        // Find surface
                        checkPos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, checkPos).down();
                        
                        if (world.getBlockState(checkPos).isSolidBlock(world, checkPos)) {
                            // Check neighbors for air
                            for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
                                BlockPos neighbor = checkPos.offset(dir);
                                if (world.isAir(neighbor)) {
                                    // Found an edge, check fall distance
                                    int fall = 0;
                                    BlockPos fallCheck = neighbor.down();
                                    while (fall < lethalDistance && world.isAir(fallCheck) && fallCheck.getY() > world.getBottomY()) {
                                        fall++;
                                        fallCheck = fallCheck.down();
                                    }
                                    
                                    if (fall >= lethalDistance) {
                                        return neighbor.toImmutable(); // Target the air block to walk into it
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static BlockPos findBlock(ServerWorld world, BlockPos center, int radius, net.minecraft.block.Block block) {
        // Reduced frequency: only check a limited area or use a more efficient search
        // For a 25 block radius, we'll check in expanding layers but only every other block in XZ to save time
        for (int r = 1; r <= radius; r++) {
            for (int y = -r; y <= r; y++) {
                for (int x = -r; x <= r; x += 2) {
                    for (int z = -r; z <= r; z += 2) {
                        if (Math.abs(x) >= r-1 || Math.abs(y) >= r-1 || Math.abs(z) >= r-1) {
                            BlockPos pos = center.add(x, y, z);
                            if (world.getBlockState(pos).isOf(block)) {
                                return pos.toImmutable();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private static LivingEntity findNearestHostile(MobEntity mob) {
        if (mob.age % 10 != 0) return mob.getTarget(); // Only scan every 10 ticks

        return mob.getWorld().getClosestEntity(
                LivingEntity.class,
                net.minecraft.entity.ai.TargetPredicate.createAttackable().setBaseMaxDistance(25.0).setPredicate(target -> {
                    // Don't attack the player who used Break Core (owner of any nearby stand)
                    // We can check for a tag on the player or just any player in Break Core mode.
                    if (target instanceof net.minecraft.entity.player.PlayerEntity player) {
                        return ((net.ragdot.gestaltresonance.util.IGestaltPlayer)player).gestaltresonance$getBreakCoreTicks() <= 0;
                    }
                    return true;
                }),
                mob,
                mob.getX(), mob.getY(), mob.getZ(),
                mob.getBoundingBox().expand(25.0)
        );
    }

    private static void applyDamage(LivingEntity entity) {
        if (entity.age % 20 == 0) {
            entity.damage(entity.getDamageSources().magic(), 2.0f); // 1 heart = 2 hp
            if (entity.isDead()) {
                if (shouldSpawnPopSprout(entity)) {
                    spawnPopSprout(entity);
                }
            }
        }
    }

    private static boolean shouldSpawnPopSprout(LivingEntity entity) {
        // 1. Not submerged in lava
        if (entity.isInLava()) return false;

        // 2. Not died from fire damage (checked via the entity's recent damage source)
        // Note: isDead() is true here. We check the last damage source.
        var lastSource = entity.getRecentDamageSource();
        if (lastSource != null && (lastSource.isIn(net.minecraft.registry.tag.DamageTypeTags.IS_FIRE))) {
            return false;
        }

        // 3. Must be on solid ground (not in air, and block below is solid)
        // We check if the entity is on ground or the block below is solid.
        if (!entity.isOnGround()) {
            BlockPos below = entity.getBlockPos().down();
            if (entity.getWorld().getBlockState(below).isAir()) {
                return false;
            }
        }

        return true;
    }

    private static void spawnPopSprout(LivingEntity entity) {
        ServerWorld world = (ServerWorld) entity.getWorld();
        BlockPos pos = entity.getBlockPos();
        if (world.getBlockState(pos).isReplaceable()) {
            world.setBlockState(pos, net.ragdot.gestaltresonance.block.ModBlocks.POPSPROUT.getDefaultState());
        } else if (world.getBlockState(pos.up()).isReplaceable()) {
            world.setBlockState(pos.up(), net.ragdot.gestaltresonance.block.ModBlocks.POPSPROUT.getDefaultState());
        }
    }
}
