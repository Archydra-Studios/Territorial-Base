package io.github.profjb58.territorial.blockEntity;

import io.github.profjb58.territorial.world.data.LocksPersistentState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Lockable block entity. Will only full initialize all values if a lockable block entity is present at the current
 * block position location specified from the constructor
 *
 * NOTE: To check if a lockable has been found use the 'exists()' method
 */
public class LockableBlockEntity {

    public enum LockedType {
        CREATIVE,
        IRON,
        GOLD,
        DIAMOND,
        NETHERITE
    }

    private String lockId;
    private UUID lockOwner;
    private LockedType lockType;
    private BlockPos blockPos;
    private ServerWorld world;

    public LockableBlockEntity(ServerWorld world, BlockPos blockPos) {
        CompoundTag tag = getNbt(world, blockPos);
        if(tag != null) {
            this.lockId = tag.getString("lock_id");
            this.lockOwner = tag.getUuid("lock_owner_uuid");
            this.lockType = getLockType(tag.getInt("lock_type"));
            this.blockPos = blockPos;
            this.world = world;
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
                tag.putInt("lock_type", getLockTypeInt(lockType));
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

    private CompoundTag getNbt(ServerWorld world, BlockPos blockPos) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if(be != null) {
            CompoundTag tag = be.toTag(new CompoundTag());
            if(tag.contains("lock_id") && tag.contains("lock_type") && tag.contains("lock_owner_uuid")) { // Is lockable
                return tag;
            }
        }
        return null;
    }

    private LockedType getLockType(int lockType) {
        switch(lockType) {
            case -1:
                return LockedType.CREATIVE;
            case 1:
                return LockedType.IRON;
            case 2:
                return LockedType.GOLD;
            case 3:
                return LockedType.DIAMOND;
            case 4:
                return LockedType.NETHERITE;
            default:
                return null;
        }
    }

    private int getLockTypeInt(LockedType lockType) {
        switch(lockType) {
            case CREATIVE:
                return -1;
            case IRON:
                return 1;
            case GOLD:
                return 2;
            case DIAMOND:
                return 3;
            case NETHERITE:
                return 4;
            default:
                return 0;
        }
    }

    public String getLockId() {
        return lockId;
    }

    public UUID getLockOwner() {
        return lockOwner;
    }

    public LockedType getLockType() {
        return lockType;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }
}
