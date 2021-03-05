package io.github.profjb58.territorial.blockEntity;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.util.LockUtils;
import io.github.profjb58.territorial.world.WorldLockStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Lockable block entity. Will only full initialize all values if a lockable block entity is present at the current
 * block position location specified from the constructor
 *
 * NOTE: To check if a lockable has been found use the 'exists()' method
 */
public class LockableBlockEntity {

    private LockableBlock lb;
    private World world;

    public LockableBlockEntity(World world, BlockPos blockPos) {
        CompoundTag tag = getNbt(world, blockPos);
        if (tag != null) {
            this.lb = new LockableBlock(tag.getString("lock_id"),
                    tag.getUuid("lock_owner_uuid"),
                    LockUtils.getLockType(tag.getInt("lock_type")),
                    blockPos);
            this.world = world;
        }
    }

    public boolean exists() {
        if(lb != null) {
            return lb.exists() && world != null;
        }
        else {
            return false;
        }
    }

    public boolean remove() {
        if(exists() && !world.isClient) {
            CompoundTag tag = getNbt(world, lb.getBlockPos());
            if(tag != null) {
                tag.remove("lock_id");
                tag.remove("lock_owner_uuid");
                tag.remove("lock_type");
                updateNbtFromTag(tag);

                WorldLockStorage lps = WorldLockStorage.get((ServerWorld) world);
                lps.removeLock(lb);
                return true;
            }
        }
        return false;
    }

    public boolean update() {
        if(exists() && !world.isClient) {
            CompoundTag tag = getNbt(world, lb.getBlockPos());
            if(tag != null) {
                tag.putString("lock_id", lb.getLockId());
                tag.putUuid("lock_owner_uuid", lb.getLockOwner());
                tag.putInt("lock_type", LockUtils.getLockTypeInt(lb.getLockType()));
                updateNbtFromTag(tag);

                WorldLockStorage lps = WorldLockStorage.get((ServerWorld) world);
                lps.addLock(lb);
                return true;
            }
        }
        return false;
    }

    private boolean updateNbtFromTag(CompoundTag tag) {
        BlockEntity be = world.getBlockEntity(lb.getBlockPos());
        if(be != null) {
            try {
                be.fromTag(world.getBlockState(lb.getBlockPos()), tag);
            } catch (Exception ignored) {}
            return true;
        }
        return false;
    }

    private CompoundTag getNbt(World world, BlockPos blockPos) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if(be != null) {
            CompoundTag tag = be.toTag(new CompoundTag());
            if(tag.contains("lock_type") && tag.contains("lock_owner_uuid")) { // Is lockable
                return tag;
            }
        }
        return null;
    }

    public LockableBlock getBlock() { return lb; };
    public BlockEntity getBlockEntity() { return world.getBlockEntity(lb.getBlockPos()); }
}
