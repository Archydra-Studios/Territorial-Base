package io.github.profjb58.territorial.world;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.networking.s2c.SyncChunkLockablesPacket;
import io.github.profjb58.territorial.util.NbtUtils;
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
import java.util.Map;

public class ServerChunkStorage extends PersistentState {

    /**
     * Key - Actual position of the locked block.
     * Value - Position of the parent of the locked block that stores it's nbt info. For Block Entities
     *         The key and value are the same.
     */
    private Map<BlockPos, BlockPos> lockedBlockPositions = new HashMap<>();

    public ServerChunkStorage() {}

    public ServerChunkStorage(HashMap<BlockPos, BlockPos> lockedBlockPositions) {
        this.lockedBlockPositions = lockedBlockPositions;
    }

    public void addLockedBlock(LockableBlock lockedBlock) {
        if (!lockedBlockPositions.containsKey(lockedBlock.selfPos())) {
            lockedBlockPositions.put(lockedBlock.selfPos(), lockedBlock.blockEntitySourcePos());
            this.markDirty();
        }
    }

    public boolean removeLockedBlock(BlockPos lockedBlockPos) {
        if(lockedBlockPositions.containsKey(lockedBlockPos)) {
            lockedBlockPositions.remove(lockedBlockPos);
            return true;
        }
        return false;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList chunkLocksNbtList = new NbtList();
        NbtCompound lockPairNbt;

        for(var lockEntrySet : lockedBlockPositions.entrySet()) {
            lockPairNbt = new NbtCompound();
            lockPairNbt.putIntArray("lock_pos", NbtUtils.serializeBlockPos(lockEntrySet.getKey()));
            lockPairNbt.putIntArray("parent_pos", NbtUtils.serializeBlockPos(lockEntrySet.getValue()));
            chunkLocksNbtList.add(lockPairNbt);
        }
        nbt.put("chunk_locks", chunkLocksNbtList);
        return nbt;
    }

    public static ServerChunkStorage readNbt(NbtCompound nbt) {
        NbtList chunkLocksNbtList = nbt.getList("chunk_locks", NbtType.COMPOUND);
        NbtCompound lockNbt;
        HashMap<BlockPos, BlockPos> lockedBlocks = new HashMap<>();

        for(NbtElement lockNbtElement : chunkLocksNbtList) {
            lockNbt = (NbtCompound) lockNbtElement;
            lockedBlocks.put(
                    NbtUtils.deserializeBlockPos(lockNbt.getIntArray("lock_pos")),
                    NbtUtils.deserializeBlockPos(lockNbt.getIntArray("parent_pos"))
            );
        }
        return new ServerChunkStorage(lockedBlocks);
    }

    public static void sync(ServerPlayerEntity player, ServerWorld world, ChunkPos chunkPos) {
        new SyncChunkLockablesPacket(player, chunkPos, get(world, chunkPos).lockedBlockPositions).send();
    }

    public static ServerChunkStorage get(ServerWorld world, ChunkPos chunkPos) {
        String storageId = "locks." + world.getRegistryKey().getValue().toString()
                + "." + chunkPos.x + "." + chunkPos.z;
        return Territorial.getWorldStorage().getPersistentStateManager(TerritorialWorldStorage.ManagerType.CHUNKS)
                .getOrCreate(ServerChunkStorage::readNbt, ServerChunkStorage::new, storageId);
    }
}
