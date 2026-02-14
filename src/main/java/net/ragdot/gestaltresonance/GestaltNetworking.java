package net.ragdot.gestaltresonance;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.ragdot.gestaltresonance.entities.AmenBreak;
import net.ragdot.gestaltresonance.entities.AmenBreakII;
import net.ragdot.gestaltresonance.entities.gestaltframework.GestaltBase;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;
import net.ragdot.gestaltresonance.network.FuturamaRecordingPayload;
import net.ragdot.gestaltresonance.network.FuturamaSyncPayload;
import net.ragdot.gestaltresonance.network.GestaltThrowPayload;
import net.ragdot.gestaltresonance.network.ToggleGestaltSummonPayload;
import net.ragdot.gestaltresonance.network.ToggleGuardModePayload;
import net.ragdot.gestaltresonance.network.ToggleLedgeGrabPayload;
import net.ragdot.gestaltresonance.network.UsePowerPayload;
import net.ragdot.gestaltresonance.network.DashGuardPunchPayload;
import net.ragdot.gestaltresonance.util.IGestaltPlayer;

import java.util.List;

public class GestaltNetworking {

    public static void registerServerReceivers() {
        // 1) Register payload types (C2S)
        PayloadTypeRegistry.playC2S().register(
                ToggleGestaltSummonPayload.ID,
                ToggleGestaltSummonPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                ToggleGuardModePayload.ID,
                ToggleGuardModePayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                ToggleLedgeGrabPayload.ID,
                ToggleLedgeGrabPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                GestaltThrowPayload.ID,
                GestaltThrowPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                UsePowerPayload.ID,
                UsePowerPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
                DashGuardPunchPayload.ID,
                DashGuardPunchPayload.CODEC
        );

        PayloadTypeRegistry.playS2C().register(
                FuturamaSyncPayload.ID,
                FuturamaSyncPayload.CODEC
        );
        PayloadTypeRegistry.playS2C().register(
                FuturamaRecordingPayload.ID,
                FuturamaRecordingPayload.CODEC
        );

        // 2) Register server handlers
        ServerPlayNetworking.registerGlobalReceiver(ToggleGestaltSummonPayload.ID, (payload, context) ->
                context.server().execute(() -> handleToggleSummon(context.player()))
        );

        ServerPlayNetworking.registerGlobalReceiver(ToggleGuardModePayload.ID, (payload, context) ->
                context.server().execute(() -> handleToggleGuardMode(context.player(), payload.guarding()))
        );

        ServerPlayNetworking.registerGlobalReceiver(ToggleLedgeGrabPayload.ID, (payload, context) ->
                context.server().execute(() -> handleToggleLedgeGrab(context.player(), payload.grabbing(), payload.pos(), payload.side()))
        );

        ServerPlayNetworking.registerGlobalReceiver(GestaltThrowPayload.ID, (payload, context) ->
                context.server().execute(() -> handleGestaltThrow(context.player(), payload.active()))
        );

        ServerPlayNetworking.registerGlobalReceiver(UsePowerPayload.ID, (payload, context) ->
                context.server().execute(() -> handleUsePower(context.player(), payload.powerIndex()))
        );

        ServerPlayNetworking.registerGlobalReceiver(DashGuardPunchPayload.ID, (payload, context) ->
                context.server().execute(() -> handleDashGuardPunch(context.player()))
        );
    }

    private static void handleUsePower(ServerPlayerEntity player, int powerIndex) {
        if (powerIndex == 0) {
            ServerWorld world = player.getServerWorld();

            // Scorched Utopia: toggle aura when present
            List<ScorchedUtopia> suStands = world.getEntitiesByClass(
                    ScorchedUtopia.class,
                    player.getBoundingBox().expand(256.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            );
            for (ScorchedUtopia stand : suStands) {
                stand.setAuraActive(!stand.isAuraActive());
            }

            // Amen Break: Ability 1 — Jungle Bomber (placeholder)
            List<AmenBreak> amenStands = world.getEntitiesByClass(
                    AmenBreak.class,
                    player.getBoundingBox().expand(256.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            );
            for (AmenBreak stand : amenStands) {
                stand.jungleBomber(player);
            }

            // Spillways: Ability 1 — create a water block at look target
            List<net.ragdot.gestaltresonance.entities.Spillways> spillwaysStands = world.getEntitiesByClass(
                    net.ragdot.gestaltresonance.entities.Spillways.class,
                    player.getBoundingBox().expand(256.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            );
            for (net.ragdot.gestaltresonance.entities.Spillways stand : spillwaysStands) {
                stand.lachryma(player);
            }
        }

        if (powerIndex == 1) {
            ServerWorld world = player.getServerWorld();

            // Spillways (Tier II+): Ability 2 — Tears for Fears
            List<net.ragdot.gestaltresonance.entities.Spillways> spillwaysStands = world.getEntitiesByClass(
                    net.ragdot.gestaltresonance.entities.Spillways.class,
                    player.getBoundingBox().expand(256.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            );
            for (net.ragdot.gestaltresonance.entities.Spillways stand : spillwaysStands) {
                stand.tearsForFears(player);
            }

            // Amen Break (Tier II+): Ability 2 — Futurama
            List<AmenBreakII> amenStands = world.getEntitiesByClass(
                    AmenBreakII.class,
                    player.getBoundingBox().expand(256.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            );
            for (AmenBreakII stand : amenStands) {
                stand.futurama(player);
            }
        }

        if (powerIndex == 2) {
            ServerWorld world = player.getServerWorld();

            // Amen Break (Tier III): Ability 3 — Break Core
            List<net.ragdot.gestaltresonance.entities.AmenBreakIII> amenStands = world.getEntitiesByClass(
                    net.ragdot.gestaltresonance.entities.AmenBreakIII.class,
                    player.getBoundingBox().expand(256.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            );
            for (net.ragdot.gestaltresonance.entities.AmenBreakIII stand : amenStands) {
                stand.breakCore(player);
            }
        }
    }

    private static void handleGestaltThrow(ServerPlayerEntity player, boolean active) {
        ((IGestaltPlayer) player).gestaltresonance$setGestaltThrowActive(active);
    }

    private static void handleDashGuardPunch(ServerPlayerEntity player) {
        IGestaltPlayer gp = (IGestaltPlayer) player;
        // Only consider dash if the player is currently guarding
        if (!gp.gestaltresonance$isGuarding()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        // Find all GestaltBase owned by the player in a reasonable radius
        List<GestaltBase> gestalts = world.getEntitiesByClass(
                GestaltBase.class,
                player.getBoundingBox().expand(256.0),
                g -> player.getUuid().equals(g.getOwnerUuid())
        );

        boolean canDash = false;
        for (GestaltBase g : gestalts) {
            if (g.getStamina() >= 12.0f) {
                canDash = true;
                break;
            }
        }

        if (!canDash) {
            // Insufficient stamina → do nothing; keep guarding
            return;
        }

        // We have enough stamina; cancel guarding and start dash on eligible Gestalts
        gp.gestaltresonance$setGuarding(false);
        for (GestaltBase g : gestalts) {
            if (g.getStamina() >= 12.0f) {
                g.startGuardDashPunch();
            }
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static void handleToggleLedgeGrab(ServerPlayerEntity player, boolean grabbing, java.util.Optional<net.minecraft.util.math.BlockPos> pos, java.util.Optional<net.minecraft.util.math.Direction> side) {
        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) player;
        boolean wasGrabbing = gestaltPlayer.gestaltresonance$isLedgeGrabbing();
        gestaltPlayer.gestaltresonance$setLedgeGrabbing(grabbing);
        pos.ifPresent(p -> {
            gestaltPlayer.gestaltresonance$setLedgeGrabPos(p);
            side.ifPresent(gestaltPlayer::gestaltresonance$setLedgeGrabSide);
            if (!wasGrabbing && grabbing) {
                // When starting a grab, pull the player up and closer to the ledge
                
                // We want the player to be 1.3 blocks away from the targeted side of the block horizontally and 0.1 blocks lower than its top
                Vec3d targetBlockCenter = new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
                
                // If side is provided, use it to determine the exact horizontal position
                Vec3d pullTarget;
                if (side.isPresent()) {
                    net.minecraft.util.math.Direction s = side.get();
                    // s is the face we hit, so we want to be 1.3 blocks away from the center in that direction
                    // Direction vector points FROM the block center TO the face.
                    Vec3d sideVec = Vec3d.of(s.getVector());
                    pullTarget = new Vec3d(
                        targetBlockCenter.x + sideVec.x * 1.3,
                        p.getY() - 0.6,
                        targetBlockCenter.z + sideVec.z * 1.3
                    );
                } else {
                    // Fallback to old logic if side is somehow missing
                    Vec3d playerPos = player.getPos();
                    Vec3d playerPosFlat = new Vec3d(playerPos.x, 0, playerPos.z);
                    Vec3d targetBlockCenterFlat = new Vec3d(targetBlockCenter.x, 0, targetBlockCenter.z);
                    Vec3d direction = playerPosFlat.subtract(targetBlockCenterFlat).normalize();
                    
                    pullTarget = new Vec3d(
                        targetBlockCenter.x + direction.x * 1.3,
                        p.getY() + 0.5, 
                        targetBlockCenter.z + direction.z * 1.3
                    );
                }
                
                player.teleport(pullTarget.x, pullTarget.y, pullTarget.z, false);
                
                // Immediately update client with the new position to avoid local freezing before sync
                player.networkHandler.requestTeleport(pullTarget.x, pullTarget.y, pullTarget.z, player.getYaw(), player.getPitch());

                // Force freezing immediately after teleport on server to avoid one-tick drift
                player.setVelocity(Vec3d.ZERO);
                player.velocityModified = true;
                player.setNoGravity(true);
                player.fallDistance = 0;
            }
        });

        if (wasGrabbing && !grabbing) {
            // Player released space, apply boost
            player.setNoGravity(false);
            
            // Momentum should be 2 blocks high and 1 block towards the target block
            // Vertical velocity for 2 blocks height: sqrt(2 * gravity * height)
            // Minecraft gravity is roughly 0.08 per tick, but jump logic is different.
            // Using a standard value that approximates 2 blocks.
            double verticalBoost = 0.55;
            
            // Horizontal boost towards the target block
            net.minecraft.util.math.BlockPos p = gestaltPlayer.gestaltresonance$getLedgeGrabPos();
            if (p != null) {
                Vec3d targetBlockCenter = new Vec3d(p.getX() + 0.5, 0, p.getZ() + 0.5);
                Vec3d playerPos = new Vec3d(player.getX(), 0, player.getZ());
                Vec3d toTarget = targetBlockCenter.subtract(playerPos).normalize();
                
                double horizontalBoost = 0.40; // 1 block distance roughly
                
                player.setVelocity(toTarget.x * horizontalBoost, verticalBoost, toTarget.z * horizontalBoost);
                player.velocityModified = true;
            }
        }
    }

    private static void handleToggleGuardMode(ServerPlayerEntity player, boolean guarding) {
        ((IGestaltPlayer) player).gestaltresonance$setGuarding(guarding);
        if (guarding) {
            player.stopRiding();
            if (player.isSprinting()) {
                player.setSprinting(false);
            }

            // Snap owned Gestalt(s) immediately to the guarding position (server authoritative)
            ServerWorld world = player.getServerWorld();
            List<GestaltBase> stands = world.getEntitiesByClass(
                    GestaltBase.class,
                    player.getBoundingBox().expand(256.0),
                    stand -> player.getUuid().equals(stand.getOwnerUuid())
            );

            for (GestaltBase stand : stands) {
                stand.snapToGuardPosition();
            }
        }
    }

    private static void handleToggleSummon(ServerPlayerEntity player) {
        var assigned = GestaltAssignments.getAssignedGestalt(player);
        if (assigned == null) return;

        var scorchedId = Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia");
        var scorchedId2 = Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia_ii");
        var scorchedId3 = Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia_iii");
        if (assigned.equals(scorchedId)) {
            toggleScorchedUtopia(player);
            return;
        }
        if (assigned.equals(scorchedId2)) {
            toggleScorchedUtopiaII(player);
            return;
        }
        if (assigned.equals(scorchedId3)) {
            toggleScorchedUtopiaIII(player);
            return;
        }

        var amenBreakId = Identifier.of(Gestaltresonance.MOD_ID, "amen_break");
        var amenBreakId2 = Identifier.of(Gestaltresonance.MOD_ID, "amen_break_ii");
        var amenBreakId3 = Identifier.of(Gestaltresonance.MOD_ID, "amen_break_iii");
        if (assigned.equals(amenBreakId)) {
            toggleAmenBreak(player);
            return;
        }
        if (assigned.equals(amenBreakId2)) {
            toggleAmenBreakII(player);
            return;
        }
        if (assigned.equals(amenBreakId3)) {
            toggleAmenBreakIII(player);
            return;
        }

        var spillwaysId = Identifier.of(Gestaltresonance.MOD_ID, "spillways");
        var spillwaysId2 = Identifier.of(Gestaltresonance.MOD_ID, "spillways_ii");
        var spillwaysId3 = Identifier.of(Gestaltresonance.MOD_ID, "spillways_iii");
        if (assigned.equals(spillwaysId)) {
            toggleSpillways(player);
            return;
        }
        if (assigned.equals(spillwaysId2)) {
            toggleSpillwaysII(player);
            return;
        }
        if (assigned.equals(spillwaysId3)) {
            toggleSpillwaysIII(player);
        }
    }

    private static void toggleAmenBreak(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<AmenBreak> stands = world.getEntitiesByClass(
                AmenBreak.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            // Explicitly clear passives before removal to avoid any lingering owner-bound modifiers
            for (AmenBreak stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        AmenBreak stand = new AmenBreak(Gestaltresonance.AMEN_BREAK, world);
        stand.setOwner(player);

        // Load persisted values from player
        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }

    private static void toggleAmenBreakII(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<net.ragdot.gestaltresonance.entities.AmenBreakII> stands = world.getEntitiesByClass(
                net.ragdot.gestaltresonance.entities.AmenBreakII.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            for (net.ragdot.gestaltresonance.entities.AmenBreakII stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        net.ragdot.gestaltresonance.entities.AmenBreakII stand = new net.ragdot.gestaltresonance.entities.AmenBreakII(Gestaltresonance.AMEN_BREAK_II, world);
        stand.setOwner(player);

        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }

    private static void toggleAmenBreakIII(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<net.ragdot.gestaltresonance.entities.AmenBreakIII> stands = world.getEntitiesByClass(
                net.ragdot.gestaltresonance.entities.AmenBreakIII.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            for (net.ragdot.gestaltresonance.entities.AmenBreakIII stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        net.ragdot.gestaltresonance.entities.AmenBreakIII stand = new net.ragdot.gestaltresonance.entities.AmenBreakIII(Gestaltresonance.AMEN_BREAK_III, world);
        stand.setOwner(player);

        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }

    private static void toggleScorchedUtopia(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<ScorchedUtopia> stands = world.getEntitiesByClass(
                ScorchedUtopia.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            for (ScorchedUtopia stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        ScorchedUtopia stand = new ScorchedUtopia(Gestaltresonance.SCORCHED_UTOPIA, world);
        stand.setOwner(player);

        // Load persisted values from player
        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }

    private static void toggleScorchedUtopiaII(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<net.ragdot.gestaltresonance.entities.ScorchedUtopiaII> stands = world.getEntitiesByClass(
                net.ragdot.gestaltresonance.entities.ScorchedUtopiaII.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            for (net.ragdot.gestaltresonance.entities.ScorchedUtopiaII stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        net.ragdot.gestaltresonance.entities.ScorchedUtopiaII stand = new net.ragdot.gestaltresonance.entities.ScorchedUtopiaII(Gestaltresonance.SCORCHED_UTOPIA_II, world);
        stand.setOwner(player);

        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }

    private static void toggleScorchedUtopiaIII(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<net.ragdot.gestaltresonance.entities.ScorchedUtopiaIII> stands = world.getEntitiesByClass(
                net.ragdot.gestaltresonance.entities.ScorchedUtopiaIII.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            for (net.ragdot.gestaltresonance.entities.ScorchedUtopiaIII stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        net.ragdot.gestaltresonance.entities.ScorchedUtopiaIII stand = new net.ragdot.gestaltresonance.entities.ScorchedUtopiaIII(Gestaltresonance.SCORCHED_UTOPIA_III, world);
        stand.setOwner(player);

        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }

    private static void toggleSpillways(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<net.ragdot.gestaltresonance.entities.Spillways> stands = world.getEntitiesByClass(
                net.ragdot.gestaltresonance.entities.Spillways.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            for (net.ragdot.gestaltresonance.entities.Spillways stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        net.ragdot.gestaltresonance.entities.Spillways stand = new net.ragdot.gestaltresonance.entities.Spillways(Gestaltresonance.SPILLWAYS, world);
        stand.setOwner(player);

        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }

    private static void toggleSpillwaysII(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<net.ragdot.gestaltresonance.entities.SpillwaysII> stands = world.getEntitiesByClass(
                net.ragdot.gestaltresonance.entities.SpillwaysII.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            for (net.ragdot.gestaltresonance.entities.SpillwaysII stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        net.ragdot.gestaltresonance.entities.SpillwaysII stand = new net.ragdot.gestaltresonance.entities.SpillwaysII(Gestaltresonance.SPILLWAYS_II, world);
        stand.setOwner(player);

        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }

    private static void toggleSpillwaysIII(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        List<net.ragdot.gestaltresonance.entities.SpillwaysIII> stands = world.getEntitiesByClass(
                net.ragdot.gestaltresonance.entities.SpillwaysIII.class,
                player.getBoundingBox().expand(256.0),
                stand -> {
                    var uuid = stand.getOwnerUuid();
                    return uuid != null && uuid.equals(player.getUuid());
                }
        );

        if (!stands.isEmpty()) {
            for (net.ragdot.gestaltresonance.entities.SpillwaysIII stand : stands) {
                stand.despawnWithCleanup();
            }
            return;
        }

        float yaw = player.getYaw();
        double rad = Math.toRadians(yaw);

        double backOffset = 1.9;
        double sideOffset = 0.5;
        double heightOffset = 0.4;

        double backX = -Math.sin(rad);
        double backZ =  Math.cos(rad);
        double rightX =  Math.cos(rad);
        double rightZ =  Math.sin(rad);

        double spawnX = player.getX() + backOffset * backX + sideOffset * rightX;
        double spawnZ = player.getZ() + backOffset * backZ + sideOffset * rightZ;
        double spawnY = player.getY() + heightOffset;

        net.ragdot.gestaltresonance.entities.SpillwaysIII stand = new net.ragdot.gestaltresonance.entities.SpillwaysIII(Gestaltresonance.SPILLWAYS_III, world);
        stand.setOwner(player);

        IGestaltPlayer gp = (IGestaltPlayer) player;
        net.minecraft.util.Identifier id = stand.getGestaltId();
        stand.setStamina(gp.gestaltresonance$getGestaltStamina(id));
        stand.setExp(gp.gestaltresonance$getGestaltExp(id));
        stand.setLvl(gp.gestaltresonance$getGestaltLvl(id));

        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }
}

