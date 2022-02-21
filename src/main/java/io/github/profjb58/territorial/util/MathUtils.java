package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.Territorial;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class MathUtils {

    private static final float TICKS_PER_SECOND = 60;

    public static class Locks {

        public static float getLockFatigueMultiplier(float amplifier) {
        /*  Exponential decay function
            Image: https://imgur.com/a/a53Ta1O
            Demos graph: https://www.desmos.com/calculator/ngqiafekap */

            double breakMultiplier = Territorial.getConfig().getBreakMultiplier();
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
