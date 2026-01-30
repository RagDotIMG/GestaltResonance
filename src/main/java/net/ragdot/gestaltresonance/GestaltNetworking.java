package net.ragdot.gestaltresonance;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.ragdot.gestaltresonance.entities.AmenBreak;
import net.ragdot.gestaltresonance.entities.GestaltBase;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;
import net.ragdot.gestaltresonance.network.GestaltThrowPayload;
import net.ragdot.gestaltresonance.network.ToggleGestaltSummonPayload;
import net.ragdot.gestaltresonance.network.ToggleGuardModePayload;
import net.ragdot.gestaltresonance.network.ToggleLedgeGrabPayload;
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

        // 2) Register server handlers
        ServerPlayNetworking.registerGlobalReceiver(ToggleGestaltSummonPayload.ID, (payload, context) -> {
            context.server().execute(() -> handleToggleSummon(context.player()));
        });

        ServerPlayNetworking.registerGlobalReceiver(ToggleGuardModePayload.ID, (payload, context) -> {
            context.server().execute(() -> handleToggleGuardMode(context.player(), payload.guarding()));
        });

        ServerPlayNetworking.registerGlobalReceiver(ToggleLedgeGrabPayload.ID, (payload, context) -> {
            context.server().execute(() -> handleToggleLedgeGrab(context.player(), payload.grabbing(), payload.pos(), payload.side()));
        });

        ServerPlayNetworking.registerGlobalReceiver(GestaltThrowPayload.ID, (payload, context) -> {
            context.server().execute(() -> handleGestaltThrow(context.player(), payload.active()));
        });
    }

    private static void handleGestaltThrow(ServerPlayerEntity player, boolean active) {
        ((IGestaltPlayer) player).gestaltresonance$setGestaltThrowActive(active);
    }

    private static void handleToggleLedgeGrab(ServerPlayerEntity player, boolean grabbing, java.util.Optional<net.minecraft.util.math.BlockPos> pos, java.util.Optional<net.minecraft.util.math.Direction> side) {
        IGestaltPlayer gestaltPlayer = (IGestaltPlayer) player;
        boolean wasGrabbing = gestaltPlayer.gestaltresonance$isLedgeGrabbing();
        gestaltPlayer.gestaltresonance$setLedgeGrabbing(grabbing);
        pos.ifPresent(p -> {
            gestaltPlayer.gestaltresonance$setLedgeGrabPos(p);
            if (!wasGrabbing && grabbing) {
                // When starting a grab, pull the player up and closer to the ledge
                
                // We want the player to be 1.5 blocks away from the targeted side of the block horizontally and 0.1 blocks lower than its top
                Vec3d targetBlockCenter = new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
                
                // If side is provided, use it to determine the exact horizontal position
                Vec3d pullTarget;
                if (side.isPresent()) {
                    net.minecraft.util.math.Direction s = side.get();
                    // s is the face we hit, so we want to be 1.5 blocks away from the center in that direction
                    // Direction vector points FROM the block center TO the face.
                    Vec3d sideVec = Vec3d.of(s.getVector());
                    pullTarget = new Vec3d(
                        targetBlockCenter.x + sideVec.x * 1.5,
                        p.getY() - 0.6,
                        targetBlockCenter.z + sideVec.z * 1.5
                    );
                } else {
                    // Fallback to old logic if side is somehow missing
                    Vec3d playerPos = player.getPos();
                    Vec3d playerPosFlat = new Vec3d(playerPos.x, 0, playerPos.z);
                    Vec3d targetBlockCenterFlat = new Vec3d(targetBlockCenter.x, 0, targetBlockCenter.z);
                    Vec3d direction = playerPosFlat.subtract(targetBlockCenterFlat).normalize();
                    
                    pullTarget = new Vec3d(
                        targetBlockCenter.x + direction.x * 1.5,
                        p.getY() + 0.5, 
                        targetBlockCenter.z + direction.z * 1.5
                    );
                }
                
                player.teleport(pullTarget.x, pullTarget.y, pullTarget.z, false);
                
                // Calculate and store the fixed Gestalt position during ledge grab
                Vec3d dirToLedge = targetBlockCenter.subtract(pullTarget).withAxis(net.minecraft.util.math.Direction.Axis.Y, 0).normalize();
                Vec3d gestaltPos = new Vec3d(
                    pullTarget.x + dirToLedge.x * 0.3,
                    pullTarget.y + player.getEyeHeight(player.getPose()) - 1.1,
                    pullTarget.z + dirToLedge.z * 0.3
                );
                gestaltPlayer.gestaltresonance$setLedgeGrabGestaltPos(gestaltPos);
                gestaltPlayer.gestaltresonance$setLedgeGrabGestaltYaw(player.getYaw());
                
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
            gestaltPlayer.gestaltresonance$setLedgeGrabGestaltPos(null);
            
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
    }

    private static void handleToggleSummon(ServerPlayerEntity player) {
        var assigned = GestaltAssignments.getAssignedGestalt(player);
        if (assigned == null) return;

        var scorchedId = Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia");
        if (assigned.equals(scorchedId)) {
            toggleScorchedUtopia(player);
            return;
        }

        var amenBreakId = Identifier.of(Gestaltresonance.MOD_ID, "amen_break");
        if (assigned.equals(amenBreakId)) {
            toggleAmenBreak(player);
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
            stands.forEach(GestaltBase::discard);
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
            stands.forEach(GestaltBase::discard);
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
        stand.refreshPositionAndAngles(spawnX, spawnY, spawnZ, yaw, 0.0f);
        world.spawnEntity(stand);
    }
}

