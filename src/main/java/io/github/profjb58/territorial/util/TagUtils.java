package io.github.profjb58.territorial.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class TagUtils {

    public static int[] serializeBlockPos(BlockPos blockPos) {
        int[] posSerializable = new int[3];
        posSerializable[0] = blockPos.getX();
        posSerializable[1] = blockPos.getY();
        posSerializable[2] = blockPos.getZ();

        return posSerializable;
    }

    public static BlockPos deserializeBlockPos(int[] posSerializable) {
        return new BlockPos(posSerializable[0], posSerializable[1], posSerializable[2]);
    }
}
