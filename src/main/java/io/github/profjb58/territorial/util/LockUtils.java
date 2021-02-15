package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.event.TerritorialRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

public class LockUtils {

    public static void removeLockFatigueEffect(ServerPlayerEntity player) {
        if(player.hasStatusEffect(TerritorialRegistry.LOCK_FATIGUE)) {
            player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
        }
    }
}
