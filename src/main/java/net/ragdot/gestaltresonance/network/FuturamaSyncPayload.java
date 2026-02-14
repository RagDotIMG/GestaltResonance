package net.ragdot.gestaltresonance.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.ragdot.gestaltresonance.Gestaltresonance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record FuturamaSyncPayload(
        int totalTicks,
        Map<UUID, List<GhostFrame>> entityRecordings
) implements CustomPayload {

    public static final Id<FuturamaSyncPayload> ID =
            new Id<>(Identifier.of(Gestaltresonance.MOD_ID, "futurama_sync"));

    public record GhostFrame(
            Vec3d pos,
            float yaw,
            float pitch
    ) {
        public static final PacketCodec<RegistryByteBuf, GhostFrame> CODEC = PacketCodec.tuple(
                PacketCodecs.VECTOR3F.xmap(v3f -> new Vec3d(v3f.x(), v3f.y(), v3f.z()), Vec3d::toVector3f), GhostFrame::pos,
                PacketCodecs.FLOAT, GhostFrame::yaw,
                PacketCodecs.FLOAT, GhostFrame::pitch,
                GhostFrame::new
        );
    }

    private static final PacketCodec<RegistryByteBuf, List<GhostFrame>> LIST_CODEC =
            GhostFrame.CODEC.collect(PacketCodecs.toCollection(ArrayList::new));

    private static final PacketCodec<RegistryByteBuf, Map<UUID, List<GhostFrame>>> MAP_CODEC =
            PacketCodecs.map(HashMap::new, Uuids.PACKET_CODEC, LIST_CODEC);

    public static final PacketCodec<RegistryByteBuf, FuturamaSyncPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, FuturamaSyncPayload::totalTicks,
            MAP_CODEC, FuturamaSyncPayload::entityRecordings,
            FuturamaSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
