package net.ragdot.gestaltresonance.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public record ToggleGestaltSummonPayload() implements CustomPayload {

    public static final Id<ToggleGestaltSummonPayload> ID =
            new Id<>(Identifier.of(Gestaltresonance.MOD_ID, "toggle_gestalt_summon"));

    // No data to send, so we can use unit()
    public static final PacketCodec<RegistryByteBuf, ToggleGestaltSummonPayload> CODEC =
            PacketCodec.unit(new ToggleGestaltSummonPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
