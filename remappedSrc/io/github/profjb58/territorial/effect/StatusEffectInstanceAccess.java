package io.github.profjb58.territorial.entity.effect;

import net.minecraft.util.math.BlockPos;

public interface StatusEffectInstanceAccess {
    BlockPos getLastPosApplied();
    void setLastPosApplied(BlockPos pos);
}
