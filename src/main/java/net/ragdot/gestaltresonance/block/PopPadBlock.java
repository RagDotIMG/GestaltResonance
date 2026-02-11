package net.ragdot.gestaltresonance.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * A thin, lily-pad-like booster pad that rests on water surfaces.
 * - Placeable only on water (we allow any water fluidstate for testing)
 * - Thin collision plane so entities can stand briefly
 * - On step: apply upward boost and remove self; no damage
 * - Auto-despawns after 600 ticks (30s)
 */
public class PopPadBlock extends Block {
    private static final int DESPAWN_TICKS = 600; // 30 seconds
    private static final VoxelShape OUTLINE = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.0/16.0, 1.0);
    // Target: ~3 blocks vertical rise and ~2 blocks forward.
    // Y boost of ~1.1–1.2 typically yields ~3 blocks with no other forces; start with 1.2 for crisp lift.
    private static final double BOOST_Y = 1.3;
    // Forward boost scalar; we’ll apply along the entity’s facing to travel ~2 blocks.
    private static final double BOOST_FORWARD = 0.8; // tuned to reach ~2 blocks horizontally

    public PopPadBlock() {
        super(AbstractBlock.Settings.create()
                .nonOpaque()
                .sounds(BlockSoundGroup.LILY_PAD)
                .noBlockBreakParticles()
                .strength(0.0f)
                .dropsNothing()
        );
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return OUTLINE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        // Allow entities to stand on it like a lily pad
        return OUTLINE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, net.minecraft.world.WorldView world, BlockPos pos) {
        // Require WATER directly below (so the pad sits on top of water, like a lily pad)
        BlockPos below = pos.down();
        FluidState fluid = world.getFluidState(below);
        return fluid.isIn(FluidTags.WATER) && !fluid.isEmpty();
    }

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

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockState(pos).isOf(this)) {
            world.removeBlock(pos, false);
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity)) return;

        // Upward-only boost similar to ledge/throw behavior
        // Add forward push in the entity's facing direction
        var forward = entity.getRotationVec(1.0f);
        entity.addVelocity(forward.x * BOOST_FORWARD, BOOST_Y, forward.z * BOOST_FORWARD);
        entity.velocityModified = true;

        world.removeBlock(pos, false);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity)) return;
        // Acts as a pressure pad: any living entity overlapping triggers the boost
        var forward = entity.getRotationVec(1.0f);
        entity.addVelocity(forward.x * BOOST_FORWARD, BOOST_Y, forward.z * BOOST_FORWARD);
        entity.velocityModified = true;
        world.removeBlock(pos, false);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, net.minecraft.util.hit.BlockHitResult hit, net.minecraft.entity.projectile.ProjectileEntity projectile) {
        if (world.isClient) return;
        BlockPos pos = hit.getBlockPos();
        // Robust reflection: handle arrows by respawning a fresh arrow with reflected velocity,
        // and apply direct velocity reflection for other projectiles.
        var v = projectile.getVelocity();
        var side = hit.getSide();
        net.minecraft.util.math.Vec3d n = new net.minecraft.util.math.Vec3d(side.getOffsetX(), side.getOffsetY(), side.getOffsetZ()).normalize();

        // Compute perfect reflection vector r = v - 2*(v·n)*n
        double dot = v.dotProduct(n);
        net.minecraft.util.math.Vec3d reflected = v.subtract(n.multiply(2.0 * dot));

        // Ensure an upward component so it visibly bounces on a horizontal pad
        if (side.getAxis().isVertical() && reflected.y <= 0.0) {
            reflected = new net.minecraft.util.math.Vec3d(reflected.x, Math.max(0.4, -reflected.y), reflected.z);
        }

        if (projectile instanceof net.minecraft.entity.projectile.PersistentProjectileEntity ppe) {
            // Remove the original and spawn a new arrow entity using the registry factory
            net.minecraft.entity.Entity maybeArrow = net.minecraft.entity.EntityType.ARROW.create(world);
            if (maybeArrow instanceof net.minecraft.entity.projectile.ArrowEntity newArrow) {
                newArrow.refreshPositionAndAngles(projectile.getX(), projectile.getY() + 0.05, projectile.getZ(), projectile.getYaw(), projectile.getPitch());
                if (ppe.getOwner() instanceof net.minecraft.entity.LivingEntity owner) {
                    newArrow.setOwner(owner);
                }
                newArrow.setVelocity(reflected);
                newArrow.velocityModified = true;
                projectile.discard();
                world.spawnEntity(newArrow);
            } else {
                // Fallback: just set the velocity on the original if creation failed
                projectile.setVelocity(reflected);
                projectile.velocityModified = true;
            }
        } else if (projectile instanceof net.ragdot.gestaltresonance.projectile.PopBud bud) {
            // Reflect Pop Bud: set its velocity and prevent its own onCollision from discarding
            bud.setVelocity(reflected);
            // Slightly raise and move off the pad to avoid immediate re-collision in the same tick
            bud.refreshPositionAfterTeleport(
                    bud.getX() + n.x * 0.06,
                    bud.getY() + n.y * 0.06,
                    bud.getZ() + n.z * 0.06
            );
            bud.velocityDirty = true;
            // Mark as not floating (defensive) and ensure gravity enabled so it continues as a projectile
            // (PopBud uses gravity by default; only floating on lava disables it)
            if (bud.isFloating()) {
                // no direct setter; rely on state machine not to engage as we're not in lava anymore
            }
        } else {
            projectile.setVelocity(reflected);
            // Nudge away from surface to avoid immediate re-collision and mark modified
            projectile.setPosition(
                    projectile.getX() + n.x * 0.06,
                    projectile.getY() + n.y * 0.06,
                    projectile.getZ() + n.z * 0.06
            );
            projectile.velocityModified = true;
        }

        // Remove the pad after reflecting the projectile
        world.removeBlock(pos, false);
    }
}
