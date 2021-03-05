package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.TerritorialRegistry;
import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class S2CPackets {

    // Packet to notify the client that a player has locked a block and so the lock should render ontop
    public static final Identifier CLIENT_ATTACH_LOCK = new Identifier(Territorial.MOD_ID, "client_attach_lock");

    public static void init()
    {

    };
}
