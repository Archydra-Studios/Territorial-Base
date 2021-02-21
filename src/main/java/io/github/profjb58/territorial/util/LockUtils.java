package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.TerritorialRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

public class LockUtils {

    public enum LockType {
        CREATIVE,
        IRON,
        GOLD,
        DIAMOND,
        NETHERITE
    }

    public static int getLockTypeInt(LockType lockType) {
        switch(lockType) {
            case CREATIVE:
                return -1;
            case IRON:
                return 1;
            case GOLD:
                return 2;
            case DIAMOND:
                return 3;
            case NETHERITE:
                return 4;
            default:
                return 0;
        }
    }

    public static LockType getLockType(int lockType) {
        switch(lockType) {
            case -1:
                return LockType.CREATIVE;
            case 1:
                return LockType.IRON;
            case 2:
                return LockType.GOLD;
            case 3:
                return LockType.DIAMOND;
            case 4:
                return LockType.NETHERITE;
            default:
                return null;
        }
    }

    public static float getBlastResistance(LockType lockType) {
        switch(lockType) {
            case CREATIVE:
                return Float.POSITIVE_INFINITY; // Impossible to break
            case NETHERITE:
                return 8; // Wither
            case DIAMOND:
                return 6; // Charged creeper / end crystal
            case GOLD:
                return 3; // Creeper
            case IRON:
                return 4; // Tnt
            default:
                return 0;
        }
    }

    public static int getLockFatigueAmplifier(LockType lockType) {
        switch(lockType) {
            case CREATIVE:
                return 4;
            case NETHERITE:
                return 3;
            case DIAMOND:
                return 2;
            case IRON:
                return 1;
            default:
                return 0;
        }
    }

    public static float getLockFatigueMultiplier(float amplifier) {

        // Exponential decay function (Desmos link: https://www.desmos.com/calculator/9qozvfjx2j)
        double breakMultiplier = Territorial.getConfig().breakMultiplier;
        double a = breakMultiplier / (Math.exp(1/1.5) - 1);
        double multiplier = a * (Math.exp(1/(amplifier + 0.5)) -1);
        return (float) multiplier;
    }

    public static void removeLockFatigueEffect(ServerPlayerEntity player) {
        if(player.hasStatusEffect(TerritorialRegistry.LOCK_FATIGUE)) {
            player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
        }
    }
}
