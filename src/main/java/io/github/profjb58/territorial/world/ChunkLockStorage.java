package io.github.profjb58.territorial.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.PersistentState;

// TODO - non block entity locks
public class ChunkLockStorage extends PersistentState {

    public ChunkLockStorage() { super("territorial_chunk_locks"); }

    public static ChunkLockStorage get(ServerWorld world) {
        return world.getChunkManager().getPersistentStateManager().getOrCreate(ChunkLockStorage::new, "territorial_chunk_locks");
    }

    @Override
    public void fromTag(CompoundTag tag) {
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        return null;
    }
}
