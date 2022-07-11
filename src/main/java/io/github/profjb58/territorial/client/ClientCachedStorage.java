package io.github.profjb58.territorial.client;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.WeakHashMap;

@Environment(EnvType.CLIENT)
public class ClientCachedStorage {

    private final WeakHashMap<ChunkPos, LongOpenHashSet> lockableBlocks = new WeakHashMap<>();

    public void onSyncLockableBlocks(ChunkPos chunkPos, LongOpenHashSet lockableBlocks) {
        this.lockableBlocks.put(chunkPos, lockableBlocks);
    }

    public boolean isLockableBlock(ClientPlayerEntity player, BlockPos pos) {
        if(lockableBlocks.containsKey(player.getChunkPos()))
            return lockableBlocks.get(player.getChunkPos()).contains(pos.asLong());
        return false;
    }

    public void clearChunkCache(ChunkPos chunkPos) {
        lockableBlocks.remove(chunkPos);
    }
}
