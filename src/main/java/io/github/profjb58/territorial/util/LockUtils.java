package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.TerritorialRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LockUtils {

    private static final float TICKS_PER_SECOND = 60;

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

    public static void removeLockFatigueEffect(ServerPlayerEntity player) {
        if(player.hasStatusEffect(TerritorialRegistry.LOCK_FATIGUE)) {
            player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
        }
    }

    public static class Calculations {
        public static float getLockFatigueMultiplier(float amplifier) {
        /*  Exponential decay function
            Image: https://imgur.com/a/a53Ta1O
            Demos graph: https://www.desmos.com/calculator/ngqiafekap */

            double breakMultiplier = Territorial.getConfig().breakMultiplier;
            double a = breakMultiplier / (Math.exp(1/1.5) - 1);
            double multiplier = a * (Math.exp(1/(amplifier + 0.5)) -1);
            return (float) multiplier;
        }

        public static int calcNumItemsToDrop(int itemStacksStored, int ticksSinceAttack) {
        /*  Max drop chance for a slot in a locked inventory is = 90%. Every second the player
            spends attacking the block the drop chance decreases by 1% until after 80 seconds it stays at 10%
            Randomization is then applied which will further reduce the drop chance */

            int numItemsToDrop;
            if(ticksSinceAttack > (80 * TICKS_PER_SECOND) || ticksSinceAttack == 0) { // 80 seconds elapsed
                numItemsToDrop = Math.round(0.1F * itemStacksStored);
            }
            else { // < 80 seconds, calculate based on time spent attacking
                float multiplier = 0.9F - ((ticksSinceAttack / TICKS_PER_SECOND) * 0.01F);
                if(multiplier > 0.1F) {
                    numItemsToDrop = Math.round(multiplier * itemStacksStored);
                }
                else { // Safe fail condition
                    numItemsToDrop = Math.round(0.1F * itemStacksStored);
                }
            }
            // Randomize the amount of stacks to retrieve, will always reduce the amount
            Random randomGen = new Random();

            // Generate a random multiplier between 0.5 and 1
            float randomMultiplier = (randomGen.nextFloat() / 2) + 0.5F;

            numItemsToDrop = Math.round(numItemsToDrop * randomMultiplier);
            return numItemsToDrop;
        }
    }
}
