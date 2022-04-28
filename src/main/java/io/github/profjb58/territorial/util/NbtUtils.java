package io.github.profjb58.territorial.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NbtUtils {

    public static int[] serializeBlockPos(@NotNull BlockPos blockPos) {
        int[] posSerializable = new int[3];
        posSerializable[0] = blockPos.getX();
        posSerializable[1] = blockPos.getY();
        posSerializable[2] = blockPos.getZ();

        return posSerializable;
    }

    public static BlockPos deserializeBlockPos(int[] posSerializable) {
        return new BlockPos(posSerializable[0], posSerializable[1], posSerializable[2]);
    }

    public static NbtCompound removeBlockFeatures(NbtCompound nbtCompound) {
        if(nbtCompound != null) {
            nbtCompound.remove("id");
            nbtCompound.remove("x");
            nbtCompound.remove("y");
            nbtCompound.remove("z");
        }
        return nbtCompound;
    }
}
