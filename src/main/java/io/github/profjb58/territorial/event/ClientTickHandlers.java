package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.EclipseRoseBlock;
import io.github.profjb58.territorial.client.render.entity.LaserBlockEntityRenderer;
import io.github.profjb58.territorial.entity.effect.EclipseStatusEffect;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.TickCounter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class ClientTickHandlers {

    private static final int LOCKABLE_VIEW_CHECK_TICK_INTERVAL = 5;
    private static final int LOCKABLE_VIEW_DISTANCE = 4;

    // Update limit counters
    private static final TickCounter LOCKABLE_VIEW_COUNTER = new TickCounter(LOCKABLE_VIEW_CHECK_TICK_INTERVAL);

    public static void init() {
        ClientTickEvents.START_WORLD_TICK.register((clientWorld) -> LaserBlockEntityRenderer.rainbowColourTick());
        ClientTickEvents.START_WORLD_TICK.register(ClientTickHandlers::lockableViewTick);
        ClientTickEvents.END_CLIENT_TICK.register((client) -> LaserBlockEntityRenderer.rainbowColourTick());
    }

    private static void lockableViewTick(ClientWorld clientWorld) {
        // TODO - Replace raycasts with a less expensive implementation
        /*
        if(LOCKABLE_VIEW_COUNTER.test()) {
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
        }
        LOCKABLE_VIEW_COUNTER.increment();
        */
    }
}
