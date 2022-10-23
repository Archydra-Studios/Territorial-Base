package io.github.profjb58.territorial.block.entity;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.enums.LockType;
import io.github.profjb58.territorial.util.NbtUtils;
import io.github.profjb58.territorial.world.ServerChunkStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Lockable block entity. Will only full initialize all values if a lockable block entity is present at the current
 * block position location specified from the constructor
 *
 * NOTE: To check if a lockable has been found use the 'exists()' method
 */
public class LockableBlockEntity {

    private LockableBlock lb;
    private World world;

    public LockableBlockEntity(World world, BlockPos blockEntityPos) {
        var nbt = getNbt(world, blockEntityPos);
        if (nbt != null) {
            // By default, the lockable block position is the same as the block entity position
            BlockPos lockableBlockPos = blockEntityPos;

            // Check if the block entity is locking a separate block nearby
            if(nbt.contains("lock_pos"))
                lockableBlockPos = NbtUtils.deserializeBlockPos(nbt.getIntArray("lock_pos"));

            this.lb = new LockableBlock(nbt.getString("lock_id"),
                    nbt.getUuid("lock_owner_uuid"),
                    nbt.getString("lock_owner_name"),
                    LockType.getTypeFromInt(nbt.getInt("lock_type")),
                    lockableBlockPos,
                    blockEntityPos);

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

                if(nbt.contains("lock_pos"))
                    nbt.remove("lock_pos");

                // Remove the locked block from persistent storage
                ServerChunkStorage.get((ServerWorld) world, world.getChunk(lb.selfPos()).getPos()).removeLockedBlock(lb.selfPos());
                return updateNbtFromTag(nbt);
            }
        }
        return false;
    }

    private boolean updateNbtFromTag(NbtCompound tag) {
        var be = world.getBlockEntity(lb.blockEntitySourcePos());
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
        return getNbt(world, lb.blockEntitySourcePos());
    }

    public LockableBlock getBlock() { return lb; };
    public BlockEntity getBlockEntity() { return world.getBlockEntity(lb.blockEntitySourcePos());}
}
