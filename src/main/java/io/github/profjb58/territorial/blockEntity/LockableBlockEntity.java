package io.github.profjb58.territorial.blockEntity;

import io.github.profjb58.territorial.util.LockUtils;
import io.github.profjb58.territorial.util.LockUtils.*;
import io.github.profjb58.territorial.world.data.LocksPersistentState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Lockable block entity. Will only full initialize all values if a lockable block entity is present at the current
 * block position location specified from the constructor
 *
 * NOTE: To check if a lockable has been found use the 'exists()' method
 */
public class LockableBlockEntity {

    private String lockId;
    private UUID lockOwner;
    private LockType lockType;
    private BlockPos blockPos;
    private ServerWorld world;
    private float blastResistance, fatigueMultiplier;

    public LockableBlockEntity(ServerWorld world, BlockPos blockPos) {
        CompoundTag tag = getNbt(world, blockPos);
        if (tag != null) {
            this.lockId = tag.getString("lock_id");
            this.lockOwner = tag.getUuid("lock_owner_uuid");
            this.lockType = LockUtils.getLockType(tag.getInt("lock_type"));
            this.blockPos = blockPos;
            this.world = world;

            if(lockType != null) {
                this.blastResistance = LockUtils.getBlastResistance(lockType);
                this.fatigueMultiplier = LockUtils.Calculations.getLockFatigueMultiplier(LockUtils.getLockFatigueAmplifier(lockType));
            }
        }
    }

    public boolean exists() {
        return lockId != null && lockOwner != null && lockType != null && blockPos != null && world != null;
    }

    public boolean remove() {
        if(exists()) {
            CompoundTag tag = getNbt(world, blockPos);
            if(tag != null) {
                tag.remove("lock_id");
                tag.remove("lock_owner_uuid");
                tag.remove("lock_type");
                updateNbtFromTag(tag);

                LocksPersistentState lps = LocksPersistentState.get(world);
                lps.removeLock(lockOwner, blockPos);
                return true;
            }
        }
        return false;
    }

    public boolean update() {
        if(exists()) {
            CompoundTag tag = getNbt(world, blockPos);
            if(tag != null) {
                tag.putString("lock_id", lockId);
                tag.putUuid("lock_owner_uuid", lockOwner);
                tag.putInt("lock_type", LockUtils.getLockTypeInt(lockType));
                updateNbtFromTag(tag);

                LocksPersistentState lps = LocksPersistentState.get(world);
                lps.addLock(lockOwner, blockPos);
                return true;
            }
        }
        return false;
    }

    private boolean updateNbtFromTag(CompoundTag tag) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if(be != null) {
            try {
                be.fromTag(world.getBlockState(blockPos), tag);
            } catch (Exception ignored) {}
            return true;
        }
        return false;
    }

    private CompoundTag getNbt(World world, BlockPos blockPos) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if(be != null) {
            CompoundTag tag = be.toTag(new CompoundTag());
            if(tag.contains("lock_id") && tag.contains("lock_type") && tag.contains("lock_owner_uuid")) { // Is lockable
                return tag;
            }
        }
        return null;
    }

    public String getLockId() {
        return lockId;
    }

    public UUID getLockOwner() {
        return lockOwner;
    }

    public LockType getLockType() { return lockType; }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public float getBlastResistance() { return blastResistance; }

    public float getFatigueMultiplier() { return fatigueMultiplier; }

    public BlockEntity getBlockEntity() { return world.getBlockEntity(blockPos); }
}
