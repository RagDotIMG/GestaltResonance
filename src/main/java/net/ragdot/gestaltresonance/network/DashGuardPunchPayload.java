package net.ragdot.gestaltresonance.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public record DashGuardPunchPayload() implements CustomPayload {

    public static final Id<DashGuardPunchPayload> ID =
            new Id<>(Identifier.of(Gestaltresonance.MOD_ID, "dash_guard_punch"));

    public static final PacketCodec<RegistryByteBuf, DashGuardPunchPayload> CODEC =
            PacketCodec.unit(new DashGuardPunchPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
