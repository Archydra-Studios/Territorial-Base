package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.client.gui.EnderKeyScreenHandler;
import io.github.profjb58.territorial.entity.effect.StatusEffectInstanceAccess;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ServerTickHandlers {

    private static final int LOCK_FATIGUE_CHECK_TICK_INTERVAL = 40;
    private static final int LOCK_FATIGUE_CHECK_RADIUS = 8;

    // Update counters
    private static int lockTicksCounter = 0;

    public static void init() {
        // Start server world ticks
        ServerTickEvents.START_WORLD_TICK.register(serverWorld -> {
            List<ServerPlayerEntity> players = serverWorld.getPlayers();

            for(ServerPlayerEntity player : players) {
                lockStatusEffectTick(player);
                enderKeyScreenTick(player);
            }
            // Increment tick counters
            lockTicksCounter++;
            AttackHandlers.ticksSinceBlockAttack++;
        });
    }

    private static void lockStatusEffectTick(ServerPlayerEntity player) {
        if(lockTicksCounter >= LOCK_FATIGUE_CHECK_TICK_INTERVAL) {
            StatusEffectInstance sei = player.getStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
            if (sei != null) {
                BlockPos lastPosApplied = ((StatusEffectInstanceAccess) sei).getLastPosApplied();
                if (lastPosApplied != null) {
                    if (!lastPosApplied.isWithinDistance(player.getBlockPos(), LOCK_FATIGUE_CHECK_RADIUS)) {
                        player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                    }
                }
            }
            lockTicksCounter = 0;
        }
    }

    private static void enderKeyScreenTick(ServerPlayerEntity player) {
        if(Territorial.getConfig().enderKeyEnabled()) {
            if (player.currentScreenHandler instanceof EnderKeyScreenHandler screenHandler) screenHandler.tick();
        }
    }
}
