package io.github.profjb58.territorial.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface C2SPacket extends Packet, ServerPlayNetworking.PlayChannelHandler {

    @Environment(EnvType.CLIENT)
    default void send() {
        Identifier id = this.getId();
        PacketByteBuf buffer = PacketByteBufs.create();
        this.write(buffer);
        ClientPlayNetworking.send(id, buffer);
    }

    @Override
    default void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        this.read(buf);
        server.execute(() -> execute(server, player, handler, buf, responseSender));
    }

    void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);

    static <T extends C2SPacket> void register(Identifier id, T obj) { ServerPlayNetworking.registerGlobalReceiver(id, obj); }
}
