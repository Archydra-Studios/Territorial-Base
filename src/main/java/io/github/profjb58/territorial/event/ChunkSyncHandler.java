package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.event.template.ChunkClientSyncEvent;
import io.github.profjb58.territorial.world.ServerChunkLockStorage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkSyncHandler implements ChunkClientSyncEvent {

    public static void init() {
        ChunkClientSyncEvent.EVENT.register(new ChunkSyncHandler());
    }

    @Override
    public void onSync(ServerPlayerEntity serverPlayer, WorldChunk worldChunk) {
        if(!worldChunk.getBlockEntities().isEmpty())
            ServerChunkLockStorage.sync(serverPlayer, (ServerWorld) worldChunk.getWorld(), worldChunk.getPos());
    }
}
