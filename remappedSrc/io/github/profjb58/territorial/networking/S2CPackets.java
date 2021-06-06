package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class S2CPackets {

    // Packet to notify the client that a player has locked a block and so the lock should render ontop
    public static final Identifier CLIENT_ATTACH_LOCK = new Identifier(Territorial.MOD_ID, "client_attach_lock");

    public static void init()
    {

    };
}
