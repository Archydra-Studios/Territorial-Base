package io.github.profjb58.territorial.world;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.networking.s2c.SyncChunkLockables;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;

public class ServerChunkLockStorage extends PersistentState {

    private HashMap<Long, Long> lockedBlocks = new HashMap<>();

    public ServerChunkLockStorage() {}

    public ServerChunkLockStorage(HashMap<Long, Long> lockedBlocks) {
        this.lockedBlocks = lockedBlocks;
    }

    public void addLockedBlock(LockableBlock lockedBlockEntity) {
        addLockedBlock(lockedBlockEntity.blockPos(), lockedBlockEntity);
    }

    public void addLockedBlock(BlockPos lockedBlockPos, LockableBlock sourceBlockEntity) {
        if(!lockedBlocks.containsKey(lockedBlockPos.asLong()))
            lockedBlocks.put(lockedBlockPos.asLong(), sourceBlockEntity.blockPos().asLong());
    }

    public boolean removeLockedBlock(BlockPos lockedBlockPos) {
        if(lockedBlocks.containsKey(lockedBlockPos.asLong())) {
            lockedBlocks.remove(lockedBlockPos.asLong());
            return true;
        }
        return false;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList chunkLocksNbtList = new NbtList();
        NbtCompound lockPairNbt;

        for(var lockEntrySet : lockedBlocks.entrySet()) {
            lockPairNbt = new NbtCompound();
            lockPairNbt.putLong("lock_pos", lockEntrySet.getKey());
            lockPairNbt.putLong("source_pos", lockEntrySet.getValue());
            chunkLocksNbtList.add(lockPairNbt);
        }
        nbt.put("chunk_locks", chunkLocksNbtList);
        return null;
    }

    public static ServerChunkLockStorage readNbt(NbtCompound nbt) {
        NbtList chunkLocksNbtList = nbt.getList("chunk_locks", NbtType.COMPOUND);
        NbtCompound lockNbt;
        HashMap<Long, Long> lockedBlocks = new HashMap<>();

        for(NbtElement lockNbtElement : chunkLocksNbtList) {
            lockNbt = (NbtCompound) lockNbtElement;
            lockedBlocks.put(lockNbt.getLong("lock_pos"), lockNbt.getLong("source_pos"));
        }
        return new ServerChunkLockStorage(lockedBlocks);
    }

    public static void sync(ServerPlayerEntity player, ServerWorld world, ChunkPos chunkPos) {
        new SyncChunkLockables(player, world, chunkPos, get(world, chunkPos).lockedBlocks);
    }

    public static ServerChunkLockStorage get(ServerWorld world, ChunkPos chunkPos) {
        String storageId = "lock_storage_" + world.getRegistryKey().getValue().toString() + "_" + chunkPos.toLong();
        return world.getPersistentStateManager().getOrCreate(ServerChunkLockStorage::readNbt, ServerChunkLockStorage::new, storageId);
    }
}
