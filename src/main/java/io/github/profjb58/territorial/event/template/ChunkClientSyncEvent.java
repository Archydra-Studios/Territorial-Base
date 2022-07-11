package io.github.profjb58.territorial.event.template;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkClientSyncEvent {
    Event<ChunkClientSyncEvent> EVENT = EventFactory.createArrayBacked(ChunkClientSyncEvent.class,
            (listeners) -> (serverPlayer, worldChunk) -> {
                for (ChunkClientSyncEvent listener : listeners)
                    listener.onSync(serverPlayer, worldChunk);
            });

    void onSync(ServerPlayerEntity serverPlayer, WorldChunk worldChunk);
}
