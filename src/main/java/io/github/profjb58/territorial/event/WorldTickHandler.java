package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.access.StatusEffectInstanceAccess;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class WorldTickHandler implements ServerTickEvents.StartWorldTick {

    public static void init() {
        ServerTickEvents.START_WORLD_TICK.register(new WorldTickHandler());
    }

    private static final int LOCK_FATIGUE_CHECK_TICK_INTERVAL = 40;
    private static final int LOCK_FATIGUE_CHECK_RADIUS = 8;

    private int ticksCounter = 0;

    @Override
    public void onStartTick(ServerWorld serverWorld) {
        List<ServerPlayerEntity> players = serverWorld.getPlayers();

        // Remove lock fatigue effect if the player moves far enough away from the block
        if(ticksCounter == LOCK_FATIGUE_CHECK_TICK_INTERVAL) { // Limit update rate
            for(ServerPlayerEntity player : players) {
                StatusEffectInstance sei = player.getStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                if(sei != null) {
                    BlockPos lastPosApplied = ((StatusEffectInstanceAccess) sei).getLastPosApplied();
                    if(lastPosApplied != null) {
                        if(!lastPosApplied.isWithinDistance(player.getBlockPos(), LOCK_FATIGUE_CHECK_RADIUS)) {
                            player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                        }
                    }
                }
            }
            ticksCounter = 0;
        }

        ticksCounter++;
    }
}
