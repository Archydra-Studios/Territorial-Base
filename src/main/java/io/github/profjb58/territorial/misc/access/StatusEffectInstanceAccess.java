package io.github.profjb58.territorial.misc.access;

import net.minecraft.util.math.BlockPos;

public interface StatusEffectInstanceAccess {
    BlockPos territorial$getLastPosApplied();
    void territorial$setLastPosApplied(BlockPos pos);
}
