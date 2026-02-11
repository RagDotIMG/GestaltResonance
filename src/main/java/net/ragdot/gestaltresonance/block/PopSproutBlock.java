package net.ragdot.gestaltresonance.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.util.math.random.Random;
import net.ragdot.gestaltresonance.entities.AmenBreak;

import java.util.List;

public class PopSproutBlock extends Block {
    private static final int DESPAWN_TICKS = 600; // 30 seconds @ 20 TPS
    public PopSproutBlock() {
        super(AbstractBlock.Settings.create()
                .noCollision()
                .nonOpaque()
                .breakInstantly()
                .sounds(BlockSoundGroup.GRASS)
                .dropsNothing());
    }

    @Override
    public boolean canPlaceAt(BlockState state, net.minecraft.world.WorldView world, BlockPos pos) {
        // Allow placement only if the block below is solid (flower-like rule broadened to any solid block)
        BlockPos below = pos.down();
        BlockState floor = world.getBlockState(below);
        return floor.isSolidBlock((BlockView) world, below);
    }

    // Schedule an auto-despawn when placed/added
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        scheduleDespawn(world, pos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!oldState.isOf(this)) {
            scheduleDespawn(world, pos);
        }
    }

    private void scheduleDespawn(World world, BlockPos pos) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, DESPAWN_TICKS);
        }
    }

    // Tick fired after 600 ticks to remove the sprout if it still exists
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // If not already popped/removed, despawn silently
        if (world.getBlockState(pos).isOf(this)) {
            world.removeBlock(pos, false);
        }
    }

    // Trigger when a living entity steps on it (players/mobs)
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity)) return;
        triggerPop(world, pos, entity);
    }

    // Trigger when any entity overlaps the block space (reliable for no-collision cross models)
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity) && !(entity instanceof ProjectileEntity)) return;
        triggerPop(world, pos, entity);
    }

    // Trigger when a projectile hits it
    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (world.isClient) return;
        triggerPop(world, hit.getBlockPos(), projectile);
    }

    private void triggerPop(World world, BlockPos pos, Entity source) {
        // Remove the sprout immediately to avoid re-triggering in the same tick
        if (!world.removeBlock(pos, false)) {
            // If already removed, do nothing
            return;
        }

        // Keep a small visual-only explosion for particles/sound. Real knockback will be applied manually below.
        float explosionPower = 0.5f; // minimal; ensures no unintended extra damage

        Vec3d center = Vec3d.ofCenter(pos);
        // Create an explosion that hurts entities but does not destroy blocks or create fire.
        // For 1.21.1, using the simplified signature keeps block destruction off by using NONE.
        world.createExplosion(
                null, // no specific causing entity
                center.x, center.y, center.z,
                explosionPower,
                World.ExplosionSourceType.NONE
        );

        // Knockback should NOT scale with level — fix it to baseline (approx. former level-1 feel)
        float knockbackPower = 1.5f; // constant baseline strength

        // Apply fixed damage AND manual knockback with previous strength
        // Radius similar to vanilla explosion influence: scale by power
        double maxRadius = Math.max(3.0, knockbackPower * 2.5); // ensure reasonable minimum
        var aabb = net.minecraft.util.math.Box.of(center, maxRadius * 2, maxRadius * 2, maxRadius * 2);
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, aabb, e -> true);
        for (LivingEntity target : targets) {
            // 1) Damage scales with Amen Break level, ignores armor: base 2 hearts + 1 heart per level
            int amenLevel = getAmenLevel(world, source);
            float hearts = 2.0f + amenLevel * 1.0f; // hearts
            float damageAmount = hearts * 2.0f;     // health points
            target.damage(world.getDamageSources().magic(), damageAmount);

            // 2) Explosion-like knockback without extra damage
            Vec3d toTarget = target.getPos().subtract(center);
            double distance = toTarget.length();
            if (distance < 0.0001) {
                // Nudge a tiny random dir if perfectly centered to avoid NaN
                toTarget = new Vec3d(0.01, 0.0, 0.01);
                distance = toTarget.length();
            }

            // Direction and horizontal emphasis (like explosions)
            Vec3d dir = toTarget.normalize();

            // Falloff similar to vanilla: influence decreases with distance
            double influenceRadius = knockbackPower * 2.0; // vanilla uses ~power*2 for diameter considerations
            double falloff = Math.max(0.0, 1.0 - (distance / Math.max(0.0001, influenceRadius)));

            // Scale factor tuned to approximate previous in-game feel
            double horizontalStrength = 0.9 * knockbackPower * falloff;
            double verticalBoost = 0.15 * knockbackPower * falloff;

            target.addVelocity(dir.x * horizontalStrength, verticalBoost, dir.z * horizontalStrength);
            target.velocityModified = true;
        }
    }

    /**
     * Detonate with the exact same behavior as a Pop Sprout pop, but without requiring a block at the location.
     * Used by Pop Bud when it collides with an entity. The explosion originates at the provided center.
     */
    public static void detonateAt(World world, Vec3d center, Entity source) {
        if (world.isClient) return;

        // Visual-only explosion for particles/sound; ensure it NEVER applies extra entity damage.
        // When the source is a floating Pop Bud on water, fully suppress explosion power to avoid
        // any vanilla damage and rely solely on the manual damage logic below.
        boolean floatingBudOnWater = false;
        boolean floatingBud = false;
        try {
            if (source instanceof net.ragdot.gestaltresonance.projectile.PopBud bud) {
                floatingBud = bud.isFloating();
                floatingBudOnWater = floatingBud && bud.isOnWater();
            }
        } catch (Throwable ignored) { }

        float explosionPower = floatingBudOnWater ? 0.0f : 0.5f;
        if (explosionPower > 0.0f) {
            world.createExplosion(
                    null,
                    center.x, center.y, center.z,
                    explosionPower,
                    World.ExplosionSourceType.NONE
            );
        }

        // Knockback should NOT scale with level — fixed baseline
        float knockbackPower = 1.5f;

        // Fixed damage that scales with Amen Break level: 2 hearts base + 1 heart per level, armor-ignoring (magic)
        double maxRadius = Math.max(3.0, knockbackPower * 2.5);
        var aabb = net.minecraft.util.math.Box.of(center, maxRadius * 2, maxRadius * 2, maxRadius * 2);
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, aabb, e -> true);
        int amenLevel = resolveAmenLevel(world, source);
        float heartsBase = 2.0f + amenLevel * 1.0f;
        float damageAmount = heartsBase * 2.0f; // convert hearts to health

        // Special handling: if the source is a floating Pop Bud on WATER, cap total damage to 0.5 hearts (1.0 health)
        if (floatingBudOnWater) {
            damageAmount = Math.min(damageAmount, 1.0f);
        }

        for (LivingEntity target : targets) {
            // 1) Apply damage (ignores armor via magic damage source)
            target.damage(world.getDamageSources().magic(), damageAmount);

            // 2) Apply explosion-like knockback with distance falloff
            Vec3d toTarget = target.getPos().subtract(center);
            double distance = toTarget.length();
            if (distance < 0.0001) {
                toTarget = new Vec3d(0.01, 0.0, 0.01);
                distance = toTarget.length();
            }

            Vec3d dir = toTarget.normalize();
            double influenceRadius = knockbackPower * 2.0;
            double falloff = Math.max(0.0, 1.0 - (distance / Math.max(0.0001, influenceRadius)));

            double horizontalStrength = 0.9 * knockbackPower * falloff;
            double verticalBoost = 0.15 * knockbackPower * falloff;
            // If triggered by a floating Pop Bud, enforce a strong upward push similar to ledge/throw boosts
            if (floatingBud) {
                // Ensure minimum upward velocity of ~0.55 (similar to ledge grab/throw), regardless of falloff.
                verticalBoost = Math.max(verticalBoost, 0.55);
                // Optionally tone down horizontal a bit to make the lift feel more vertical and reliable
                horizontalStrength *= 0.85;
            }

            target.addVelocity(dir.x * horizontalStrength, verticalBoost, dir.z * horizontalStrength);
            target.velocityModified = true;
        }
    }

    /**
     * Resolve Amen Break level from a player or their projectile. Defaults to 1 if unknown.
     */
    private static int resolveAmenLevel(World world, Entity source) {
        int amenLevel = 1;

        ServerPlayerEntity owner = null;
        if (source instanceof ServerPlayerEntity p) {
            owner = p;
        } else if (source instanceof ProjectileEntity proj && proj.getOwner() instanceof ServerPlayerEntity p) {
            owner = p;
        }

        if (owner != null) {
            final java.util.UUID ownerUuid = owner.getUuid();
            List<AmenBreak> stands = world.getEntitiesByClass(
                    AmenBreak.class,
                    owner.getBoundingBox().expand(256.0),
                    stand -> ownerUuid.equals(stand.getOwnerUuid())
            );
            for (AmenBreak stand : stands) {
                amenLevel = Math.max(amenLevel, stand.getLvl());
            }
        }

        return amenLevel;
    }

    /**
     * Resolve the owner's Amen Break level from the source entity if possible.
     * Defaults to 1 when unknown.
     */
    private int getAmenLevel(World world, Entity source) {
        int amenLevel = 1;

        ServerPlayerEntity owner = null;
        if (source instanceof ServerPlayerEntity p) {
            owner = p;
        } else if (source instanceof ProjectileEntity proj && proj.getOwner() instanceof ServerPlayerEntity p) {
            owner = p;
        }

        if (owner != null) {
            final java.util.UUID ownerUuid = owner.getUuid();
            List<AmenBreak> stands = world.getEntitiesByClass(
                    AmenBreak.class,
                    owner.getBoundingBox().expand(256.0),
                    stand -> ownerUuid.equals(stand.getOwnerUuid())
            );
            for (AmenBreak stand : stands) {
                amenLevel = Math.max(amenLevel, stand.getLvl());
            }
        }

        return amenLevel;
    }

    private float computeExplosionPower(World world, Entity source) {
        int amenLevel = 1; // default level if we cannot determine owner’s Amen Break

        // If the source is a player or a projectile owned by a player, try to find their Amen Break entity
        ServerPlayerEntity owner = null;
        if (source instanceof ServerPlayerEntity p) {
            owner = p;
        } else if (source instanceof ProjectileEntity proj && proj.getOwner() instanceof ServerPlayerEntity p) {
            owner = p;
        }

        if (owner != null) {
            // Find Amen Break entities owned by this player and use their (max) level
            final java.util.UUID ownerUuid = owner.getUuid();
            List<AmenBreak> stands = world.getEntitiesByClass(
                    AmenBreak.class,
                    owner.getBoundingBox().expand(256.0),
                    stand -> ownerUuid.equals(stand.getOwnerUuid())
            );
            for (AmenBreak stand : stands) {
                amenLevel = Math.max(amenLevel, stand.getLvl());
            }
        }

        // Use previous scaling for effective explosion/knockback strength
        return 1.0f + 0.5f * amenLevel;
    }
}
