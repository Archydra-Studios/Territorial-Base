package io.github.profjb58.territorial.block.entity;

import io.github.profjb58.territorial.block.LockableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.concurrent.locks.Lock;

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
        var nbt = getNbt(world, blockPos);
        if (nbt != null) {
            this.lb = new LockableBlock(nbt.getString("lock_id"),
                    nbt.getUuid("lock_owner_uuid"),
                    nbt.getString("lock_owner_name"),
                    LockableBlock.lockType(nbt.getInt("lock_type")),
                    blockPos);
            this.world = world;
        }
    }

    public boolean exists() { return lb != null && (lb.exists() && world != null); }

    public boolean remove() {
        if(exists() && !world.isClient) {
            var nbt = getNbt();
            if(nbt != null) {
                nbt.remove("lock_id");
                nbt.remove("lock_owner_uuid");
                nbt.remove("lock_owner_name");
                nbt.remove("lock_type");
                updateNbtFromTag(nbt);

                //WorldLockStorage lps = WorldLockStorage.get((ServerWorld) world);
                //lps.removeLock(lb);
                return true;
            }
        }
        return false;
    }

    public boolean update() {
        if(exists() && !world.isClient) {
            var nbt = getNbt();
            if(nbt != null) {
                nbt.putString("lock_id", lb.lockId());
                nbt.putUuid("lock_owner_uuid", lb.lockOwnerUuid());
                nbt.putString("lock_owner_name", lb.lockOwnerName());
                nbt.putInt("lock_type", lb.lockTypeInt());
                updateNbtFromTag(nbt);

                //WorldLockStorage lps = WorldLockStorage.get((ServerWorld) world);
                //lps.addLock(lb);
                return true;
            }
        }
        return false;
    }

    private boolean updateNbtFromTag(NbtCompound tag) {
        var be = world.getBlockEntity(lb.blockPos());
        if(be != null) {
            try {
                be.readNbt(tag);
            } catch (Exception ignored) {}
            return true;
        }
        return false;
    }

    private NbtCompound getNbt(World world, BlockPos blockPos) {
        var be = world.getBlockEntity(blockPos);
        if(be != null) {
            var nbt = be.createNbt();
            if(nbt.contains("lock_id") && nbt.contains("lock_type") && nbt.contains("lock_owner_uuid")
                    && nbt.contains("lock_owner_name")) { // Is lockable
                return nbt;
            }
        }
        return null;
    }

    @Nullable
    public NbtCompound getNbt() {
        return getNbt(world, lb.blockPos());
    }

    public LockableBlock getBlock() { return lb; };
    public BlockEntity getBlockEntity() { return world.getBlockEntity(lb.blockPos());}
}
