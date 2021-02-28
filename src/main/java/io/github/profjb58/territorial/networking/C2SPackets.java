package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.TerritorialRegistry;
import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class C2SPackets {

    // Packet to notify the server that a player is breaking a block
    public static final Identifier BREAKING_BLOCK = new Identifier(Territorial.MOD_ID, "breaking_block");

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(BREAKING_BLOCK, (server, player, handler, buf, responseSender) -> {
            // Target block
            BlockPos target = buf.readBlockPos();

            server.execute(() -> {
                if(!LockUtils.addEffect(player, target)) {
                    player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                }
            });
        });
    }
}
