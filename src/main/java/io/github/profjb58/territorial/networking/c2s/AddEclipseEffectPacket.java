package io.github.profjb58.territorial.networking.c2s;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AddEclipseEffectPacket extends C2SPacket {

    private int effectDuration;

    public AddEclipseEffectPacket() {}

    public AddEclipseEffectPacket(int effectDuration) {
        this.effectDuration = effectDuration;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        player.addStatusEffect(new StatusEffectInstance(TerritorialRegistry.ECLIPSE_EFFECT, effectDuration));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(effectDuration);
    }

    @Override
    public void read(PacketByteBuf buf) {
        effectDuration = buf.readInt();
    }

    @Override
    public Identifier getId() {
        return TerritorialRegistry.ADD_ECLIPSE_EFFECT_PACKET_ID;
    }
}
