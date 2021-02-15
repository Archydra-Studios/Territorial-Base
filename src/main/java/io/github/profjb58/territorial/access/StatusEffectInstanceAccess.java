package io.github.profjb58.territorial.access;

import net.minecraft.util.math.BlockPos;

public interface StatusEffectInstanceAccess {
    BlockPos getLastPosApplied();
    void setLastPosApplied(BlockPos pos);
}
