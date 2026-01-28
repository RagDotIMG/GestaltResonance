package net.ragdot.gestaltresonance;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.entities.GestaltBase;
import net.ragdot.gestaltresonance.entities.ScorchedUtopia;
import net.ragdot.gestaltresonance.network.ToggleGestaltSummonPayload;

import java.util.List;

public class GestaltNetworking {

    public static void registerServerReceivers() {
        // 1) Register payload type (C2S)
        PayloadTypeRegistry.playC2S().register(
                ToggleGestaltSummonPayload.ID,
                ToggleGestaltSummonPayload.CODEC
        );

        // 2) Register server handler
        ServerPlayNetworking.registerGlobalReceiver(
                ToggleGestaltSummonPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    player.getServer().execute(() -> handleToggleSummon(player));
                }
        );
    }

    private static void handleToggleSummon(ServerPlayerEntity player) {
        var assigned = GestaltAssignments.getAssignedGestalt(player);
        if (assigned == null) return;

        var scorchedId = Identifier.of(Gestaltresonance.MOD_ID, "scorched_utopia");
        if (assigned.equals(scorchedId)) {
            toggleScorchedUtopia(player);
        }
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

