package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.effect.LockFatigueInstance;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class C2SPackets {

    // Packet to notify the server that a player is breaking a block
    public static final Identifier BREAKING_BLOCK = new Identifier(Territorial.MOD_ID, "breaking_block");

    // Packet to notify placement of a laser transmitter
    public static final Identifier PLACED_LASER_TRANSMITTER = new Identifier(Territorial.MOD_ID, "placed_laser_transmitter");

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(BREAKING_BLOCK, (server, player, handler, buf, responseSender) -> {
            // Target block
            final BlockPos target = buf.readBlockPos();

            server.execute(() -> {
                if(!LockFatigueInstance.addEffect(player, target)) {
                    player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PLACED_LASER_TRANSMITTER, (server, player, handler, buf, responseSender) -> {

        });
    }
}
