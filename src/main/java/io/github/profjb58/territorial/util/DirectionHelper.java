package io.github.profjb58.territorial.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DirectionHelper {

    public static Direction getDirectionBetween(BlockPos startPos, BlockPos endPos){
        if(endPos.getX() == startPos.getX()){
            if(startPos.getZ() - endPos.getZ() > 0){
                return Direction.SOUTH;
            } else {
                return Direction.NORTH;
            }
        } else if(startPos.getZ() == endPos.getZ()) {
            if(startPos.getX() - endPos.getX() > 0){
                return Direction.WEST;
            } else {
                return Direction.EAST;
            }
        } else {
            return null;
        }
    }

}
