package net.ragdot.gestaltresonance.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

import net.minecraft.network.codec.PacketCodecs;

public record ToggleGuardModePayload(boolean guarding) implements CustomPayload {

    public static final Id<ToggleGuardModePayload> ID =
            new Id<>(Identifier.of(Gestaltresonance.MOD_ID, "toggle_guard_mode"));

    public static final PacketCodec<RegistryByteBuf, ToggleGuardModePayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOL, ToggleGuardModePayload::guarding, ToggleGuardModePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
