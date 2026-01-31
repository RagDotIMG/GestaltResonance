package net.ragdot.gestaltresonance.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public record GestaltThrowPayload(boolean active) implements CustomPayload {

    public static final Id<GestaltThrowPayload> ID =
            new Id<>(Identifier.of(Gestaltresonance.MOD_ID, "gestalt_throw"));

    public static final PacketCodec<RegistryByteBuf, GestaltThrowPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOL, GestaltThrowPayload::active, GestaltThrowPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
