package net.ragdot.gestaltresonance.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public record FuturamaRecordingPayload(boolean recording) implements CustomPayload {

    public static final Id<FuturamaRecordingPayload> ID =
            new Id<>(Identifier.of(Gestaltresonance.MOD_ID, "futurama_recording"));

    public static final PacketCodec<RegistryByteBuf, FuturamaRecordingPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOL, FuturamaRecordingPayload::recording, FuturamaRecordingPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
