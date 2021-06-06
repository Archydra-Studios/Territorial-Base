package io.github.profjb58.territorial.world;

/**
// TODO - non block entity locks
public class ChunkLockStorage extends PersistentState {

    HashMap<ChunkPos, HashSet<BlockPos>> positions = new HashMap<>();

    public ChunkLockStorage() { super("territorial_chunk_locks"); }

    public static ChunkLockStorage get(ServerWorld world) {
        return world.getChunkManager().getPersistentStateManager().getOrCreate(ChunkLockStorage::new, "territorial_chunk_locks");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        return null;
    }
}
**/