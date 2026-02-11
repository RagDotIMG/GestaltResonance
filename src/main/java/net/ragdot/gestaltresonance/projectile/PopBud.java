package net.ragdot.gestaltresonance.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ragdot.gestaltresonance.block.ModBlocks;
import net.ragdot.gestaltresonance.block.PopSproutBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Box;

public class PopBud extends ThrownItemEntity {

    // Floating state fields
    private static final TrackedData<Boolean> FLOATING = DataTracker.registerData(PopBud.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ON_WATER = DataTracker.registerData(PopBud.class, TrackedDataHandlerRegistry.BOOLEAN);
    private boolean floating = false; // local cache for server logic; client reads tracker via accessors
    private boolean onWater = false;  // local cache for server logic; client reads tracker via accessors
    private Vec3d floatCenter = null;
    private int floatAge = 0; // ticks since started floating

    public PopBud(EntityType<? extends PopBud> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FLOATING, false);
        builder.add(ON_WATER, false);
    }

    public PopBud(World world, LivingEntity owner, EntityType<? extends PopBud> type) {
        super(type, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        // Use snowball item only to satisfy base class; we render with a custom model/texture.
        return Items.SNOWBALL;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (this.getWorld().isClient) {
            super.onCollision(hitResult);
            return;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hitResult;
            BlockPos impact = bhr.getBlockPos();

            // If we hit a Pod Pad, let the pad's onProjectileHit handle reflection; do not process further here.
            if (this.getWorld().getBlockState(impact).isOf(ModBlocks.POP_PAD)) {
                // Do NOT call super.onCollision to avoid default discard; the pad will handle reflection.
                return;
            }

            // Check if we hit a liquid: on WATER -> place Pod Pad; on LAVA -> float armed
            FluidState fluid = this.getWorld().getFluidState(impact);
            if (!fluid.isEmpty()) {
                if (fluid.isIn(FluidTags.WATER)) {
                    if (tryPlacePodPad(impact)) {
                        this.discard();
                        return;
                    }
                } else if (fluid.isIn(FluidTags.LAVA)) {
                    beginFloatingAtSurface(impact, fluid);
                    return; // do not discard; we are now an armed floating bud
                }
            }

            // Try to place Pop Sprout at the adjacent spot in the hit face direction
            BlockPos placePos = impact.offset(bhr.getSide());

            boolean placed = tryPlaceSprout(placePos);
            if (!placed && this.getWorld().getBlockState(impact).isAir()) {
                placed = tryPlaceSprout(impact);
            }
            this.discard();
        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
            // On entity hit: explode with the same behavior as Pop Sprout, centered on the projectile
            EntityHitResult ehr = (EntityHitResult) hitResult;
            Vec3d center = this.getPos();
            PopSproutBlock.detonateAt(this.getWorld(), center, this);
            this.discard();
        }

        // For all other cases, allow base behavior to run
        super.onCollision(hitResult);
    }

    private boolean tryPlaceSprout(BlockPos pos) {
        // Ensure target is empty/replaceable, and the block below is solid per PopSprout placement rules
        if (!this.getWorld().getBlockState(pos).isAir()) return false;
        BlockPos below = pos.down();
        if (!this.getWorld().getBlockState(below).isSolidBlock(this.getWorld(), below)) return false;
        return this.getWorld().setBlockState(pos, ModBlocks.POPSPROUT.getDefaultState());
    }

    private boolean tryPlacePodPad(BlockPos pos) {
        // Place Pod Pad on top of the water block: target position is pos.up()
        FluidState fluid = this.getWorld().getFluidState(pos);
        if (fluid.isEmpty() || !fluid.isIn(FluidTags.WATER)) return false;
        BlockPos target = pos.up();
        if (!this.getWorld().getBlockState(target).isAir()) return false;
        return this.getWorld().setBlockState(target, ModBlocks.POP_PAD.getDefaultState());
    }

    private void beginFloatingAtSurface(BlockPos fluidPos, FluidState fluid) {
        // Anchor to the actual fluid surface height (supports flowing/partial fluids),
        // then add a tiny offset to prevent z-fighting with the surface.
        double surface = fluid.getHeight(this.getWorld(), fluidPos);
        double y = fluidPos.getY() + surface + 0.03; // just above the surface
        Vec3d center = new Vec3d(fluidPos.getX() + 0.5, y, fluidPos.getZ() + 0.5);
        this.floating = true;
        this.onWater = fluid.isIn(FluidTags.WATER);
        // Sync to clients
        this.getDataTracker().set(FLOATING, true);
        this.getDataTracker().set(ON_WATER, this.onWater);
        this.floatCenter = center;
        this.floatAge = 0;
        this.setNoGravity(true);
        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;
        this.refreshPositionAndAngles(center.x, center.y, center.z, this.getYaw(), this.getPitch());
    }

    @Override
    public void tick() {
        super.tick();

        // If we are not yet floating, detect entry into liquids and transition immediately.
        if (!this.floating) {
            if (!this.getWorld().isClient) {
                BlockPos pos = this.getBlockPos();
                FluidState fluid = this.getWorld().getFluidState(pos);
                if (!fluid.isEmpty()) {
                    if (fluid.isIn(FluidTags.WATER)) {
                        if (tryPlacePodPad(pos)) {
                            this.discard();
                            return;
                        }
                    } else if (fluid.isIn(FluidTags.LAVA)) {
                        beginFloatingAtSurface(pos, fluid);
                        return; // start floating this tick
                    }
                }
            }
            return;
        }

        // Keep locked at surface, apply subtle server-side stabilization
        if (!this.getWorld().isClient) {
            this.setVelocity(Vec3d.ZERO);
            this.setPosition(this.floatCenter.x, this.floatCenter.y, this.floatCenter.z);
        }

        // Lifetime management
        this.floatAge++;
        if (!this.getWorld().isClient) {
            if (this.floatAge >= 600) {
                this.discard();
                return;
            }

            // Trigger check: living entities OR projectiles intersecting a small AABB above the surface
            double r = 0.45; // trigger radius
            Box box = new Box(
                    floatCenter.x - r, floatCenter.y - 0.1, floatCenter.z - r,
                    floatCenter.x + r, floatCenter.y + 0.5, floatCenter.z + r
            );

            boolean triggered = false;
            // Check living entities
            if (!this.getWorld().getEntitiesByClass(LivingEntity.class, box, Entity::isAlive).isEmpty()) {
                triggered = true;
            }
            // Or projectiles
            if (!triggered && !this.getWorld().getEntitiesByClass(ProjectileEntity.class, box, e -> e.isAlive() && e != this).isEmpty()) {
                triggered = true;
            }

            if (triggered) {
                PopSproutBlock.detonateAt(this.getWorld(), this.floatCenter, this);
                this.discard();
            }
        }
    }

    // Accessors for detonation logic
    public boolean isFloating() {
        // Use synced tracker on both sides; server also keeps local boolean true when floating
        return this.getDataTracker().get(FLOATING);
    }
    public boolean isOnWater() {
        return this.getDataTracker().get(ON_WATER);
    }
}
