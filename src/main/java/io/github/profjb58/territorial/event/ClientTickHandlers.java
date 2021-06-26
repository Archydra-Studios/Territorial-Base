package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.util.ClientUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

import static io.github.profjb58.territorial.TerritorialClient.lockableHud;

@Environment(EnvType.CLIENT)
public class ClientTickHandlers {

    private static final int LOCKABLE_VIEW_CHECK_TICK_INTERVAL = 5;
    private static final int LOCKABLE_VIEW_DISTANCE = 4;

    // Update limit counters
    private static int lockableViewCounter = 0;

    public static void init() {
        ClientTickEvents.START_WORLD_TICK.register((clientWorld) -> {
            if(lockableViewCounter >= LOCKABLE_VIEW_CHECK_TICK_INTERVAL) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;

                if(player != null) {
                    if(!player.isSneaking()) {
                        BlockPos viewPos = ClientUtils.getRaycastPos(player, LOCKABLE_VIEW_DISTANCE);
                        LockableBlockEntity lbe = new LockableBlockEntity(clientWorld, viewPos);

                        if(lbe.exists()) {
                            lockableHud.showLockInfo(lbe.getBlock(), player);
                        }
                        else if(!lockableHud.isIgnoringCycle()){
                            lockableHud.reset();
                        }
                    }
                }
                lockableViewCounter = 0;
            }
            lockableViewCounter++;
        });
    }
}
