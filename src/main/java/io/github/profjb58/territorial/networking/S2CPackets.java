package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class S2CPackets {

    // Packet to notify the client that a player has locked a block and so the lock should render ontop
    public static final Identifier CLIENT_ATTACH_LOCK = new Identifier(Territorial.MOD_ID, "client_attach_lock");

    public static void init()
    {
        ClientPlayNetworking.registerGlobalReceiver(CLIENT_ATTACH_LOCK, (client, handler, buf, responseSender) -> {
            // Target block
            BlockPos target = buf.readBlockPos();

            client.execute(() -> {

            });
        });


    };
}
