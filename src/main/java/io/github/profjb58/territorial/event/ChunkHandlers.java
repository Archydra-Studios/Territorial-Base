package io.github.profjb58.territorial.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public class ChunkHandlers {

    public static void init() {
        ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            chunk.getPos();
        });

        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {

        });
    }
}
