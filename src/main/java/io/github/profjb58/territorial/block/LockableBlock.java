package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.util.LockUtils;
import io.github.profjb58.territorial.util.LockUtils.LockType;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class LockableBlock {

    private final String lockId;
    private final UUID lockOwner;
    private final LockType lockType;
    private final BlockPos blockPos;
    float blastResistance, fatigueMultiplier;

    public LockableBlock(String lockId, UUID lockOwner, LockType lockType, BlockPos blockPos) {
        this.lockId = lockId;
        this.lockOwner = lockOwner;
        this.lockType = lockType;
        this.blockPos = blockPos;

        if(lockType != null) {
            this.blastResistance = LockUtils.getBlastResistance(lockType);
            this.fatigueMultiplier = LockUtils.Calculations.getLockFatigueMultiplier(LockUtils.getLockFatigueAmplifier(lockType));
        }
    }

    public boolean exists() {
        return lockOwner != null && lockType != null && blockPos != null;
    }

    public UUID getLockOwner() { return lockOwner; }
    public String getLockId() { return lockId; }
    public LockType getLockType() { return lockType; }
    public BlockPos getBlockPos() { return blockPos; }
    public float getBlastResistance() { return blastResistance; }
    public float getFatigueMultiplier() { return fatigueMultiplier; }
}
