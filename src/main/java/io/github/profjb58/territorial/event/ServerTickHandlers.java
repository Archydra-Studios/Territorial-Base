package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.screen.EnderKeyScreenHandler;
import io.github.profjb58.territorial.misc.access.StatusEffectInstanceAccess;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.TickCounter;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ServerTickHandlers {

    private static final int LOCK_FATIGUE_CHECK_TICK_INTERVAL = 40;
    private static final int LOCK_FATIGUE_CHECK_RADIUS = 8;

    private static final TickCounter LOCK_FATIGUE_TICKER = new TickCounter(LOCK_FATIGUE_CHECK_TICK_INTERVAL);

    public static void init() {
        ServerTickEvents.START_WORLD_TICK.register(ServerTickHandlers::lockStatusEffectTick);
        ServerTickEvents.START_WORLD_TICK.register(ServerTickHandlers::enderKeyScreenTick);
    }

    private static void lockStatusEffectTick(ServerWorld serverWorld) {
        var players = serverWorld.getPlayers();
        for(var player : players) {
            if(LOCK_FATIGUE_TICKER.test()) {
                StatusEffectInstance sei = player.getStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT);
                if (sei != null) {
                    BlockPos lastPosApplied = ((StatusEffectInstanceAccess) sei).territorial$getLastPosApplied();
                    if (lastPosApplied != null) {
                        if (!lastPosApplied.isWithinDistance(player.getBlockPos(), LOCK_FATIGUE_CHECK_RADIUS)) {
                            player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT);
                        }
                    }
                }
            }
        }
        LOCK_FATIGUE_TICKER.increment();
    }

    private static void enderKeyScreenTick(ServerWorld serverWorld) {
        var players = serverWorld.getPlayers();
        for(var player : players) {
            if (Territorial.getConfig().enderKeyEnabled()) {
                if (player.currentScreenHandler instanceof EnderKeyScreenHandler screenHandler) screenHandler.tick();
            }
        }
        AttackHandlers.TICKS_SINCE_BLOCK_ATTACK.increment();
    }
}
