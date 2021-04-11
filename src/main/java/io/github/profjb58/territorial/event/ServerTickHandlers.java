package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.effect.StatusEffectInstanceAccess;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ServerTickHandlers {

    private static final int LOCK_FATIGUE_CHECK_TICK_INTERVAL = 40;
    private static final int LOCK_FATIGUE_CHECK_RADIUS = 8;

    // Update limit counters
    private static int lockTicksCounter = 0;

    public static void init() {
        // Start server world ticks
        ServerTickEvents.START_WORLD_TICK.register(serverWorld -> {
            List<ServerPlayerEntity> players = serverWorld.getPlayers();

            // Remove lock fatigue effect if the player moves far enough away from the block
            if(lockTicksCounter == LOCK_FATIGUE_CHECK_TICK_INTERVAL) { // Limit update rate
                for (ServerPlayerEntity player : players) {
                    StatusEffectInstance sei = player.getStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                    if (sei != null) {
                        BlockPos lastPosApplied = ((StatusEffectInstanceAccess) sei).getLastPosApplied();
                        if (lastPosApplied != null) {
                            if (!lastPosApplied.isWithinDistance(player.getBlockPos(), LOCK_FATIGUE_CHECK_RADIUS)) {
                                player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                            }
                        }
                    }
                }
                lockTicksCounter = 0;
            }
            // Increment tick counters
            lockTicksCounter++;
            AttackHandlers.ticksSinceBlockAttack++;
        });
    }
}
