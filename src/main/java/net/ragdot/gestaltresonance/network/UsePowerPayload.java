package net.ragdot.gestaltresonance.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.ragdot.gestaltresonance.Gestaltresonance;

public record UsePowerPayload(int powerIndex) implements CustomPayload {

    public static final Id<UsePowerPayload> ID =
            new Id<>(Identifier.of(Gestaltresonance.MOD_ID, "use_power"));

    public static final PacketCodec<RegistryByteBuf, UsePowerPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.VAR_INT, UsePowerPayload::powerIndex, UsePowerPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
