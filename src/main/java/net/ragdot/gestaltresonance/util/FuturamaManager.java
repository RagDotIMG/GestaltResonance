package net.ragdot.gestaltresonance.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.ragdot.gestaltresonance.network.FuturamaRecordingPayload;
import net.ragdot.gestaltresonance.network.FuturamaSyncPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Server-side driver for Amen Break II+ power: Futurama.
 *
 * MVP implementation:
 * - Records entity transforms (pos/vel/yaw/pitch) for 6s (120 ticks)
 * - Records block state changes for 6s
 * - Resets blocks to pre-record state, then replays recorded transforms/changes for 6s
 * - Activating player is NOT forced during replay (but is reset to activation position once before replay)
 */
public final class FuturamaManager {

    private FuturamaManager() {}

    public static final int RECORD_TICKS = 120;
    public static final int DEFAULT_RADIUS = 80;

    private static final Map<UUID, FuturamaSession> ACTIVE = new HashMap<>();

    private static final ThreadLocal<Boolean> APPLYING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public static boolean isApplying() {
        return APPLYING.get();
    }

    public static boolean hasActiveSession(UUID playerUuid) {
        return ACTIVE.containsKey(playerUuid);
    }

    /**
     * Prevents true player death during Futurama recording.
     *
     * @return true if vanilla damage/death should be cancelled.
     */
    public static boolean tryPreventPlayerDeathDuringRecording(ServerPlayerEntity player) {
        if (player == null) return false;
        if (isApplying()) return false;
        if (ACTIVE.isEmpty()) return false;

        ServerWorld world = player.getServerWorld();
        if (world == null) return false;

        Vec3d pos = player.getPos();
        for (FuturamaSession session : ACTIVE.values()) {
            if (!session.isRecordingInWorld(world)) continue;
            if (!session.isWithinRadius(pos)) continue;
            return session.onPlayerWouldDie(player);
        }
        return false;
    }

    public static boolean tryStart(ServerPlayerEntity player) {
        if (player == null) return false;
        ServerWorld world = player.getServerWorld();
        if (world == null) return false;
        UUID uuid = player.getUuid();
        if (ACTIVE.containsKey(uuid)) return false;

        FuturamaSession session = FuturamaSession.start(world, player, DEFAULT_RADIUS);
        ACTIVE.put(uuid, session);
        return true;
    }

    public static void tick(ServerWorld world) {
        if (world == null) return;
        if (ACTIVE.isEmpty()) return;

        Iterator<Map.Entry<UUID, FuturamaSession>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            FuturamaSession session = it.next().getValue();
            if (!session.isInWorld(world)) continue;

            session.tick(world);
            if (session.isDone()) {
                it.remove();
            }
        }
    }

    public static void onBlockSetHead(ServerWorld world, BlockPos pos, BlockState oldState) {
        if (world == null || pos == null || oldState == null) return;
        if (isApplying()) return;
        if (ACTIVE.isEmpty()) return;

        for (FuturamaSession session : ACTIVE.values()) {
            if (session.isRecordingInWorld(world) && session.isWithinRadius(pos)) {
                session.captureOldBlockState(pos, oldState);
            }
        }
    }

    public static void onBlockSetReturn(ServerWorld world, BlockPos pos, BlockState newState, boolean success) {
        if (!success) return;
        if (world == null || pos == null || newState == null) return;
        if (isApplying()) return;
        if (ACTIVE.isEmpty()) return;

        for (FuturamaSession session : ACTIVE.values()) {
            if (session.isRecordingInWorld(world) && session.isWithinRadius(pos)) {
                session.recordBlockChange(pos, newState);
            }
        }
    }

    public static void onExplosion(ServerWorld world,
                                   Entity entity,
                                   double x, double y, double z,
                                   float power,
                                   World.ExplosionSourceType sourceType) {
        if (world == null) return;
        if (isApplying()) return;
        if (ACTIVE.isEmpty()) return;

        Vec3d pos = new Vec3d(x, y, z);
        UUID sourceUuid = entity != null ? entity.getUuid() : null;
        for (FuturamaSession session : ACTIVE.values()) {
            if (session.isRecordingInWorld(world) && session.isWithinRadius(pos)) {
                session.recordExplosion(sourceUuid, pos, power, sourceType);
            }
        }
    }

    public static void onEntityRemove(ServerWorld world, Entity entity) {
        if (world == null || entity == null) return;
        if (isApplying()) return;
        if (ACTIVE.isEmpty()) return;

        UUID removedUuid = entity.getUuid();
        Vec3d pos = entity.getPos();
        for (FuturamaSession session : ACTIVE.values()) {
            if (session.isRecordingInWorld(world) && session.isWithinRadius(pos)) {
                session.recordEntityRemoval(removedUuid);
            }
        }
    }

    static void withApplying(ServerWorld world, Runnable action) {
        if (action == null) return;
        Boolean prev = APPLYING.get();
        APPLYING.set(Boolean.TRUE);
        try {
            action.run();
        } finally {
            APPLYING.set(prev);
        }
    }

    static final class FuturamaSession {
        private enum Phase { RECORDING, RESETTING, REPLAYING, DONE }

        private final UUID ownerUuid;
        private final BlockPos anchor;
        private final int radius;
        private final net.minecraft.registry.RegistryKey<net.minecraft.world.World> worldKey;

        private Phase phase;
        private int tick;

        private final Map<BlockPos, BlockState> oldBlockStates = new HashMap<>();
        private final List<List<BlockChange>> blockChanges = new ArrayList<>(RECORD_TICKS);
        private final List<List<ExplosionEvent>> explosionEvents = new ArrayList<>(RECORD_TICKS);
        private final List<List<SpawnEvent>> spawnEvents = new ArrayList<>(RECORD_TICKS);
        private final List<List<RemoveEvent>> removeEvents = new ArrayList<>(RECORD_TICKS);
        private final List<Map<UUID, EntityFrame>> entityFrames = new ArrayList<>(RECORD_TICKS);
        private final Set<UUID> initialEntityUuids = new HashSet<>();
        private final Map<UUID, EntitySnapshot> initialEntitySnapshots = new HashMap<>();
        private final Set<UUID> seenDuringRecording = new HashSet<>();
        private final Set<UUID> recordedRemovals = new HashSet<>();
        private final Map<UUID, UUID> uuidRemap = new HashMap<>();
        private EntityFrame ownerStartFrame;

        private final Map<UUID, PlayerSnapshot> playerSnapshots = new HashMap<>();
        private final Map<UUID, Integer> playerDeathTicks = new HashMap<>();

        private FuturamaSession(ServerWorld world, ServerPlayerEntity owner, int radius) {
            this.ownerUuid = owner.getUuid();
            this.anchor = owner.getBlockPos();
            this.radius = Math.max(1, radius);
            this.worldKey = world.getRegistryKey();
            this.phase = Phase.RECORDING;
            this.tick = 0;
            for (int i = 0; i < RECORD_TICKS; i++) {
                blockChanges.add(new ArrayList<>());
                explosionEvents.add(new ArrayList<>());
                spawnEvents.add(new ArrayList<>());
                removeEvents.add(new ArrayList<>());
                entityFrames.add(new HashMap<>());
            }
        }

        static FuturamaSession start(ServerWorld world, ServerPlayerEntity owner, int radius) {
            FuturamaSession s = new FuturamaSession(world, owner, radius);
            s.ownerStartFrame = EntityFrame.capture(owner);
            s.snapshotInitialEntities(world);
            s.snapshotInitialPlayers(world);
            ServerPlayNetworking.send(owner, new FuturamaRecordingPayload(true));
            return s;
        }

        private void snapshotInitialPlayers(ServerWorld world) {
            if (world == null) return;
            for (ServerPlayerEntity p : world.getPlayers()) {
                if (p == null) continue;
                if (isWithinRadius(p.getPos())) {
                    playerSnapshots.put(p.getUuid(), PlayerSnapshot.capture(p));
                }
            }
        }

        boolean isInWorld(ServerWorld world) {
            return world != null && world.getRegistryKey().equals(this.worldKey);
        }

        boolean isRecordingInWorld(ServerWorld world) {
            return isInWorld(world) && phase == Phase.RECORDING && tick >= 0 && tick < RECORD_TICKS;
        }

        boolean isWithinRadius(BlockPos pos) {
            if (pos == null) return false;
            return pos.isWithinDistance(anchor, radius);
        }

        boolean isWithinRadius(Vec3d pos) {
            if (pos == null) return false;
            return pos.squaredDistanceTo(Vec3d.ofCenter(anchor)) <= (radius * radius);
        }

        boolean isDone() {
            return phase == Phase.DONE;
        }

        void captureOldBlockState(BlockPos pos, BlockState oldState) {
            // Only capture the first old state for a position during the recording window.
            oldBlockStates.putIfAbsent(pos.toImmutable(), oldState);
        }

        void recordBlockChange(BlockPos pos, BlockState newState) {
            if (phase != Phase.RECORDING) return;
            if (tick < 0 || tick >= RECORD_TICKS) return;
            blockChanges.get(tick).add(new BlockChange(pos.toImmutable(), newState));
        }

        void recordExplosion(UUID sourceUuid, Vec3d pos, float power, World.ExplosionSourceType sourceType) {
            if (phase != Phase.RECORDING) return;
            if (tick < 0 || tick >= RECORD_TICKS) return;
            if (sourceType == null) return;
            explosionEvents.get(tick).add(new ExplosionEvent(sourceUuid, pos, power, sourceType));
        }

        void recordEntityRemoval(UUID removedUuid) {
            if (phase != Phase.RECORDING) return;
            if (tick < 0 || tick >= RECORD_TICKS) return;
            if (removedUuid == null) return;
            if (removedUuid.equals(ownerUuid)) return;

            // Prevent spamming duplicates for the same UUID (some codepaths call remove multiple times).
            // We only need the first removal tick to correctly reenact death/despawn.
            if (!recordedRemovals.add(removedUuid)) return;
            removeEvents.get(tick).add(new RemoveEvent(removedUuid));
        }

        void tick(ServerWorld world) {
            if (!isInWorld(world)) return;
            switch (phase) {
                case RECORDING -> tickRecording(world);
                case RESETTING -> {
                    applyReset(world);
                    syncToClients(world);
                    ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(ownerUuid);
                    if (owner != null) {
                        ServerPlayNetworking.send(owner, new FuturamaRecordingPayload(false));
                    }
                    phase = Phase.REPLAYING;
                    tick = 0;
                }
                case REPLAYING -> tickReplay(world);
                case DONE -> {
                    ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(ownerUuid);
                    if (owner != null) {
                        ((IGestaltPlayer) owner).gestaltresonance$setIncapacitated(false);
                    }
                }
            }
        }

        private void tickRecording(ServerWorld world) {
            ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(ownerUuid);
            if (owner == null) {
                phase = Phase.DONE;
                return;
            }

            // Record entity frames for this tick.
            Box box = new Box(
                    anchor.getX() - radius, anchor.getY() - radius, anchor.getZ() - radius,
                    anchor.getX() + radius + 1, anchor.getY() + radius + 1, anchor.getZ() + radius + 1
            );
            var entities = world.getOtherEntities(null, box);
            Map<UUID, EntityFrame> frameMap = entityFrames.get(tick);
            for (var e : entities) {
                UUID id = e.getUuid();

                // Capture a snapshot for any player we see during recording (first time only).
                // This allows rewind to put them back into a valid alive state before replay.
                if (e instanceof ServerPlayerEntity sp) {
                    playerSnapshots.putIfAbsent(id, PlayerSnapshot.capture(sp));
                }

                if (seenDuringRecording.add(id) && !initialEntityUuids.contains(id)) {
                    // Entity appeared after activation (includes projectiles). Record a spawn snapshot.
                    // It will be removed during reset and recreated during replay at this same tick.
                    spawnEvents.get(tick).add(new SpawnEvent(id, EntitySnapshot.capture(e)));
                }
                frameMap.put(e.getUuid(), EntityFrame.capture(e));
            }
            frameMap.put(ownerUuid, EntityFrame.capture(owner));

            tick++;
            if (tick >= RECORD_TICKS) {
                phase = Phase.RESETTING;
            }
        }

        private void applyReset(ServerWorld world) {
            ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(ownerUuid);
            if (owner != null) {
                ((IGestaltPlayer) owner).gestaltresonance$setIncapacitated(false);
            }

            uuidRemap.clear();

            FuturamaManager.withApplying(world, () -> {
                // 1) Reset changed blocks to their old states
                for (var entry : oldBlockStates.entrySet()) {
                    world.setBlockState(entry.getKey(), entry.getValue());
                }

                // 2) Remove entities that were not present at the start (within the anchored radius)
                Box box = new Box(
                        anchor.getX() - radius, anchor.getY() - radius, anchor.getZ() - radius,
                        anchor.getX() + radius + 1, anchor.getY() + radius + 1, anchor.getZ() + radius + 1
                );
                var entitiesNow = world.getOtherEntities(null, box);
                for (var e : entitiesNow) {
                    if (e instanceof ServerPlayerEntity) continue;
                    if (!initialEntityUuids.contains(e.getUuid())) {
                        e.discard();
                    }
                }

                // 3) Respawn any initial entities that are missing now (e.g., died during recording)
                for (var entry : initialEntitySnapshots.entrySet()) {
                    UUID uuid = entry.getKey();
                    if (uuid.equals(ownerUuid)) continue;
                    Entity existing = world.getEntity(uuid);
                    if (existing != null) {
                        // If the entity still exists but is dead/removed, treat it as missing so replay can work.
                        // (Dead entities can hang around briefly, causing us to incorrectly skip respawn.)
                        if (!existing.isAlive() || existing.isRemoved()) {
                            existing.discard();
                        } else {
                            continue;
                        }
                    }

                    EntitySnapshot snapshot = entry.getValue();
                    EntitySnapshot.SpawnedEntity spawned = snapshot.trySpawn(world, uuid);
                    if (spawned != null && spawned.entity != null) {
                        // Ensure the respawned entity is placed inside the simulation area immediately.
                        // Subsequent replay ticks will apply exact recorded frames.
                        spawned.entity.velocityModified = true;
                        if (!uuid.equals(spawned.actualUuid)) {
                            uuidRemap.put(uuid, spawned.actualUuid);
                        }
                    }
                }
            });

            // 4) Reset affected players back to their activation snapshot and ensure they are alive.
            // Activator is snapped once then is free during replay; other players are forced during replay.
            for (var entry : playerSnapshots.entrySet()) {
                UUID puid = entry.getKey();
                ServerPlayerEntity p = world.getServer().getPlayerManager().getPlayer(puid);
                if (p == null) continue;

                PlayerSnapshot snap = entry.getValue();
                if (snap != null) {
                    snap.frame.applyTo(p, world, true);

                    // Health
                    p.setHealth(Math.max(1.0f, Math.min(p.getMaxHealth(), snap.health)));

                    // Hunger
                    try {
                        var hm = p.getHungerManager();
                        hm.setFoodLevel(Math.max(0, Math.min(20, snap.foodLevel)));
                        hm.setSaturationLevel(Math.max(0.0f, snap.saturationLevel));
                        hm.setExhaustion(Math.max(0.0f, snap.exhaustion));
                    } catch (Throwable ignored) {
                    }

                    // Experience
                    try {
                        p.experienceLevel = Math.max(0, snap.experienceLevel);
                        p.experienceProgress = Math.max(0.0f, Math.min(1.0f, snap.experienceProgress));
                        p.totalExperience = Math.max(0, snap.totalExperience);
                    } catch (Throwable ignored) {
                    }

                    p.setFireTicks(0);
                    p.fallDistance = 0.0f;
                }
            }
        }

        private void syncToClients(ServerWorld world) {
            Map<UUID, List<FuturamaSyncPayload.GhostFrame>> recordings = new HashMap<>();

            for (int t = 0; t < RECORD_TICKS; t++) {
                Map<UUID, EntityFrame> frameMap = entityFrames.get(t);
                for (Map.Entry<UUID, EntityFrame> entry : frameMap.entrySet()) {
                    UUID id = entry.getKey();
                    EntityFrame f = entry.getValue();
                    recordings.computeIfAbsent(id, k -> new ArrayList<>())
                            .add(new FuturamaSyncPayload.GhostFrame(f.pos, f.yaw, f.pitch));
                }
            }

            FuturamaSyncPayload payload = new FuturamaSyncPayload(RECORD_TICKS, recordings);

            // Send to all players in the same world near the anchor
            for (ServerPlayerEntity p : world.getPlayers()) {
                if (isWithinRadius(p.getPos())) {
                    ServerPlayNetworking.send(p, payload);
                }
            }
        }

        private void tickReplay(ServerWorld world) {
            ServerPlayerEntity owner = world.getServer().getPlayerManager().getPlayer(ownerUuid);
            if (owner == null) {
                phase = Phase.DONE;
                return;
            }
            if (tick < 0 || tick >= RECORD_TICKS) {
                phase = Phase.DONE;
                return;
            }

            List<BlockChange> changes = blockChanges.get(tick);
            List<ExplosionEvent> explosions = explosionEvents.get(tick);
            List<SpawnEvent> spawns = spawnEvents.get(tick);
            List<RemoveEvent> removals = removeEvents.get(tick);
            Map<UUID, EntityFrame> frames = entityFrames.get(tick);

            FuturamaManager.withApplying(world, () -> {
                // Spawn any entities/projectiles that were created during the recording window on this tick.
                // (They were deleted during reset, so we recreate them before applying frames.)
                for (SpawnEvent se : spawns) {
                    UUID recordedUuid = se.recordedUuid;
                    if (recordedUuid == null || recordedUuid.equals(ownerUuid)) continue;

                    // Only spawn if there isn't already a live mapped entity.
                    UUID mapped = resolveUuid(recordedUuid);
                    Entity existing = world.getEntity(mapped);
                    if (existing != null && existing.isAlive() && !existing.isRemoved()) continue;

                    EntitySnapshot.SpawnedEntity spawned = se.snapshot.trySpawn(world, recordedUuid);
                    if (spawned != null && spawned.entity != null) {
                        spawned.entity.velocityModified = true;
                        if (!recordedUuid.equals(spawned.actualUuid)) {
                            uuidRemap.put(recordedUuid, spawned.actualUuid);
                        }
                    }
                }

                // Apply recorded block changes for this tick
                for (BlockChange change : changes) {
                    world.setBlockState(change.pos, change.state);
                }

                // Apply recorded explosions for this tick
                for (ExplosionEvent ex : explosions) {
                    Entity source = null;
                    if (ex.sourceUuid != null) {
                        source = world.getEntity(ex.sourceUuid);
                    }
                    world.createExplosion(
                            source,
                            ex.pos.x, ex.pos.y, ex.pos.z,
                            ex.power,
                            ex.sourceType
                    );
                }

                // Apply recorded entity transforms for this tick (excluding owner)
                for (var entry : frames.entrySet()) {
                    UUID recordedUuid = entry.getKey();
                    if (recordedUuid.equals(ownerUuid)) continue;

                    EntityFrame f = entry.getValue();
                    UUID mappedUuid = resolveUuid(recordedUuid);
                    var target = world.getEntity(mappedUuid);
                    if (target == null) continue;
                    if (target instanceof ServerPlayerEntity sp) {
                        f.applyTo(sp, world, true);
                    } else {
                        f.applyTo(target);
                    }
                }

                // Apply recorded removals last so entities can still be driven on their final tick.
                for (RemoveEvent re : removals) {
                    UUID recordedUuid = re.recordedUuid;
                    if (recordedUuid == null || recordedUuid.equals(ownerUuid)) continue;
                    UUID mapped = resolveUuid(recordedUuid);
                    Entity target = world.getEntity(mapped);
                    if (target != null) {
                        target.discard();
                    }
                }
            });

            // Apply recorded player deaths AFTER replaying the tick.
            // This makes the replay death the “true” death (respawn screen) for non-activators.
            for (var entry : playerDeathTicks.entrySet()) {
                UUID puid = entry.getKey();
                if (puid == null) continue;
                if (puid.equals(ownerUuid)) continue;
                Integer dt = entry.getValue();
                if (dt == null || dt != tick) continue;

                ServerPlayerEntity p = world.getServer().getPlayerManager().getPlayer(puid);
                if (p == null) continue;

                try {
                    p.kill();
                } catch (Throwable ignored) {
                    // Best-effort; if kill fails due to modded states, vanilla will handle later.
                }
            }

            tick++;
            if (tick >= RECORD_TICKS) {
                phase = Phase.DONE;
            }
        }

        boolean onPlayerWouldDie(ServerPlayerEntity player) {
            if (player == null) return false;
            if (phase != Phase.RECORDING) return false;
            if (tick < 0 || tick >= RECORD_TICKS) return false;
            if (!isWithinRadius(player.getPos())) return false;

            UUID puid = player.getUuid();

            // Activator is always protected from true death during recording.
            if (puid.equals(ownerUuid)) {
                ((IGestaltPlayer) player).gestaltresonance$setIncapacitated(true);
                return true;
            }

            // Only record the first death tick; multiple lethal hits should still just replay a single death.
            playerDeathTicks.putIfAbsent(puid, tick);
            return true;
        }

        private void snapshotInitialEntities(ServerWorld world) {
            Box box = new Box(
                    anchor.getX() - radius, anchor.getY() - radius, anchor.getZ() - radius,
                    anchor.getX() + radius + 1, anchor.getY() + radius + 1, anchor.getZ() + radius + 1
            );
            var entities = world.getOtherEntities(null, box);
            for (var e : entities) {
                initialEntityUuids.add(e.getUuid());

                // Mark as already-seen so we don't emit spawn events for initial entities.
                seenDuringRecording.add(e.getUuid());

                if (e instanceof ServerPlayerEntity) continue;
                initialEntitySnapshots.put(e.getUuid(), EntitySnapshot.capture(e));
            }
            initialEntityUuids.add(ownerUuid);
            seenDuringRecording.add(ownerUuid);
        }

        private UUID resolveUuid(UUID recordedUuid) {
            if (recordedUuid == null) return null;
            return uuidRemap.getOrDefault(recordedUuid, recordedUuid);
        }
    }

    private record BlockChange(BlockPos pos, BlockState state) {}

    private record ExplosionEvent(UUID sourceUuid, Vec3d pos, float power, World.ExplosionSourceType sourceType) {}

    private record SpawnEvent(UUID recordedUuid, EntitySnapshot snapshot) {}

    private record RemoveEvent(UUID recordedUuid) {}

    private record EntitySnapshot(EntityType<?> type, NbtCompound nbt) {
        static EntitySnapshot capture(Entity e) {
            NbtCompound nbt = new NbtCompound();
            // Includes UUID + full entity data.
            // NOTE: `EntityType#loadEntityWithPassengers` expects the entity id under key `id`.
            // `Entity#writeNbt` does NOT include it, so we must add it ourselves or respawn will fail.
            e.writeNbt(nbt);
            try {
                nbt.putString(
                        "id",
                        net.minecraft.registry.Registries.ENTITY_TYPE.getId(e.getType()).toString()
                );
            } catch (Throwable ignored) {
                // Best-effort; if we can't write id, we still keep the rest of the NBT.
            }
            return new EntitySnapshot(e.getType(), nbt);
        }

        private static NbtCompound withUuid(NbtCompound nbt, UUID uuid) {
            NbtCompound copy = nbt.copy();
            if (uuid != null) {
                try {
                    copy.putUuid("UUID", uuid);
                } catch (Throwable ignored) {
                    // If format changes, we still return the copy; caller will accept UUID remap.
                }
            }
            return copy;
        }

        SpawnedEntity trySpawn(ServerWorld world, UUID preferredUuid) {
            if (world == null) return null;

            // Prefer NBT-based loading because it restores equipment/health/AI state.
            // We attempt to preserve UUID, but if the server rejects it (UUID collision), we fall back to a new UUID.
            UUID desiredUuid = preferredUuid;
            NbtCompound nbt1 = withUuid(this.nbt, desiredUuid);

            Entity loaded = null;
            try {
                loaded = EntityType.loadEntityWithPassengers(nbt1, world, entity -> entity);
            } catch (Throwable ignored) {
                loaded = null;
            }

            if (loaded != null) {
                if (world.spawnEntity(loaded)) {
                    return new SpawnedEntity(loaded, loaded.getUuid());
                }
                loaded.discard();
            }

            // Fallback: if NBT loading failed for any reason, at least recreate an entity of the same type.
            // This matches the requested “spawn same type to repeat its death” behavior.
            try {
                Entity fallback = this.type.create(world);
                if (fallback != null) {
                    // Best-effort position restore from NBT (Pos is a list of 3 doubles).
                    // If missing, spawn at world spawn to avoid crashing.
                    if (this.nbt.contains("Pos", net.minecraft.nbt.NbtElement.LIST_TYPE)) {
                        var posList = this.nbt.getList("Pos", net.minecraft.nbt.NbtElement.DOUBLE_TYPE);
                        if (posList.size() >= 3) {
                            double px = posList.getDouble(0);
                            double py = posList.getDouble(1);
                            double pz = posList.getDouble(2);
                            fallback.refreshPositionAndAngles(px, py, pz, fallback.getYaw(), fallback.getPitch());
                        }
                    }
                    if (world.spawnEntity(fallback)) {
                        return new SpawnedEntity(fallback, fallback.getUuid());
                    }
                    fallback.discard();
                }
            } catch (Throwable ignored) {
                // keep going to UUID retry
            }

            // If we failed to spawn (likely UUID collision / timing), retry with a fresh UUID.
            UUID fresh = UUID.randomUUID();
            NbtCompound nbt2 = withUuid(this.nbt, fresh);
            try {
                Entity loaded2 = EntityType.loadEntityWithPassengers(nbt2, world, entity -> entity);
                if (loaded2 == null) return null;
                if (!world.spawnEntity(loaded2)) {
                    loaded2.discard();
                    return null;
                }
                return new SpawnedEntity(loaded2, loaded2.getUuid());
            } catch (Throwable ignored) {
                // If loading fails (NBT incompat, mod entity issues), fail silently for now.
                return null;
            }
        }

        private record SpawnedEntity(Entity entity, UUID actualUuid) {}
    }

    private record EntityFrame(
            Vec3d pos,
            Vec3d vel,
            float yaw,
            float pitch,
            float headYaw,
            float bodyYaw,
            float limbSpeed,
            boolean handSwinging,
            net.minecraft.util.Hand preferredHand,
            boolean sneaking,
            boolean sprinting,
            boolean swimming,
            boolean invisible,
            boolean onFire,
            net.minecraft.entity.EntityPose pose
    ) {
        static EntityFrame capture(net.minecraft.entity.Entity e) {
            float headYaw = 0;
            float bodyYaw = 0;
            float limbSpeed = 0;
            boolean handSwinging = false;
            net.minecraft.util.Hand preferredHand = net.minecraft.util.Hand.MAIN_HAND;

            if (e instanceof net.minecraft.entity.LivingEntity le) {
                headYaw = le.headYaw;
                bodyYaw = le.bodyYaw;
                limbSpeed = le.limbAnimator.getSpeed();
                handSwinging = le.handSwinging;
                preferredHand = le.preferredHand != null ? le.preferredHand : net.minecraft.util.Hand.MAIN_HAND;
            }

            return new EntityFrame(
                    e.getPos(),
                    e.getVelocity(),
                    e.getYaw(),
                    e.getPitch(),
                    headYaw,
                    bodyYaw,
                    limbSpeed,
                    handSwinging,
                    preferredHand,
                    e.isSneaking(),
                    e.isSprinting(),
                    e.isSwimming(),
                    e.isInvisible(),
                    e.isOnFire(),
                    e.getPose()
            );
        }

        void applyTo(net.minecraft.entity.Entity e) {
            e.refreshPositionAndAngles(pos.x, pos.y, pos.z, yaw, pitch);
            e.setVelocity(vel);
            e.velocityModified = true;

            e.setSneaking(sneaking);
            e.setSprinting(sprinting);
            e.setSwimming(swimming);
            e.setInvisible(invisible);
            if (onFire) e.setOnFireFor(20);
            e.setPose(pose);

            if (e instanceof net.minecraft.entity.LivingEntity le) {
                le.headYaw = headYaw;
                le.prevHeadYaw = headYaw;
                le.bodyYaw = bodyYaw;
                le.prevBodyYaw = bodyYaw;
                le.limbAnimator.setSpeed(limbSpeed);

                if (handSwinging) {
                    le.swingHand(preferredHand, true);
                }
            }
        }

        void applyTo(ServerPlayerEntity player, ServerWorld world, boolean includeVelocity) {
            player.teleport(world, pos.x, pos.y, pos.z, yaw, pitch);
            if (includeVelocity) {
                player.setVelocity(vel);
                player.velocityModified = true;
            }

            player.setSneaking(sneaking);
            player.setSprinting(sprinting);
            player.setSwimming(swimming);
            player.setInvisible(invisible);
            if (onFire) player.setOnFireFor(20);
            player.setPose(pose);

            player.headYaw = headYaw;
            player.prevHeadYaw = headYaw;
            player.bodyYaw = bodyYaw;
            player.prevBodyYaw = bodyYaw;
            player.limbAnimator.setSpeed(limbSpeed);

            if (handSwinging) {
                player.swingHand(preferredHand, true);
            }
        }
    }

    private record PlayerSnapshot(
            EntityFrame frame,
            float health,
            int foodLevel,
            float saturationLevel,
            float exhaustion,
            int experienceLevel,
            float experienceProgress,
            int totalExperience
    ) {
        static PlayerSnapshot capture(ServerPlayerEntity p) {
            int food = 20;
            float sat = 5.0f;
            float exh = 0.0f;
            try {
                var hm = p.getHungerManager();
                food = hm.getFoodLevel();
                sat = hm.getSaturationLevel();
                exh = hm.getExhaustion();
            } catch (Throwable ignored) {
            }
            return new PlayerSnapshot(
                    EntityFrame.capture(p),
                    p.getHealth(),
                    food,
                    sat,
                    exh,
                    p.experienceLevel,
                    p.experienceProgress,
                    p.totalExperience
            );
        }
    }
}
