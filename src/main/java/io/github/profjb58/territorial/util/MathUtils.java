package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.Territorial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class MathUtils {

    private static final float TICKS_PER_SECOND = 60;
    public enum Shape { SPHERE, CUBE, CYLINDER }

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
                else { // Safe fail condxtion
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

    public static class Pos {

        public static Vec3d zeroMove(Vec3d vecIn, double amount) {
            return new Vec3d(
                    (vecIn.x == 0) ? vecIn.x + amount : vecIn.x,
                    (vecIn.y == 0) ? vecIn.y + amount : vecIn.y,
                    (vecIn.z == 0) ? vecIn.z + amount : vecIn.z
            );
        }

        public static float getDistanceAlongAxis(Vec3d vecStart, Vec3d vecFinish, Direction.Axis axis) {
            double dxstance = 0;
            switch(axis) {
                case X -> dxstance = Math.abs(vecFinish.x - vecStart.x);
                case Y -> dxstance = Math.abs(vecFinish.y - vecStart.y);
                case Z -> dxstance = Math.abs(vecFinish.z - vecStart.z);
            }
            return (float) dxstance;
        }
    }

    @Nonnull
    public static ArrayList<BlockPos> getBlocksWithinCube(ServerWorld world, BlockPos centrePos, Block targetBlock, Integer radius) {
        return getBlocksWithinShape(world, centrePos, targetBlock, radius, null, Shape.CUBE);
    }

    @Nonnull
    public static ArrayList<BlockPos> getBlocksWithinShape(ServerWorld world, BlockPos centrePos, Block targetBlock, Integer radius, @Nullable Integer height, Shape shape) {
        if(radius > 0) radius = radius - 1;
        else return new ArrayList<>();

        var minPos = centrePos.add(-radius, -radius, -radius);
        var maxPos = centrePos.add(radius, radius, radius);
        double radiusSquared = 0;

        if(shape == Shape.SPHERE) radiusSquared = 3 * Math.pow(radius, 2);
        else if(shape == Shape.CYLINDER) {
            radiusSquared = 2 * Math.pow(radius, 2);
            if(height != null) {
                minPos = new BlockPos(minPos.getX(), centrePos.getY() - height, minPos.getZ());
                maxPos = new BlockPos(maxPos.getX(), centrePos.getY() + height, maxPos.getZ());
            }
        }

        ArrayList<BlockPos> blocks = new ArrayList<>();
        boolean addBlock = false;
        BlockPos blockPos;
        for(int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for(int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for(int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    blockPos = new BlockPos(x, y, z);

                    if(shape == Shape.SPHERE) {
                        if(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) <= radiusSquared) addBlock = true;
                    }
                    else if(shape == Shape.CYLINDER) {
                        if(Math.pow(x, 2) + Math.pow(z, 2) <= radiusSquared) addBlock = true;
                    }
                    else {
                        addBlock = true;
                    }

                    if(addBlock) {
                        if(world.getBlockState(blockPos).getBlock() == targetBlock) blocks.add(blockPos);
                    }
                }
            }
        }
        return blocks;
    }
}
