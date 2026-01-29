package net.ragdot.gestaltresonance.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.ragdot.gestaltresonance.Gestaltresonance;

import java.util.Optional;

public record ToggleLedgeGrabPayload(boolean grabbing, Optional<BlockPos> pos, Optional<Direction> side) implements CustomPayload {

    public static final Id<ToggleLedgeGrabPayload> ID =
            new Id<>(Identifier.of(Gestaltresonance.MOD_ID, "toggle_ledge_grab"));

    public static final PacketCodec<RegistryByteBuf, ToggleLedgeGrabPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            ToggleLedgeGrabPayload::grabbing,
            BlockPos.PACKET_CODEC.collect(PacketCodecs::optional),
            ToggleLedgeGrabPayload::pos,
            Direction.PACKET_CODEC.collect(PacketCodecs::optional),
            ToggleLedgeGrabPayload::side,
            ToggleLedgeGrabPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
