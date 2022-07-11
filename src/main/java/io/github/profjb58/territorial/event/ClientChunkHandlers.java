package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.client.ClientCachedStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class ClientChunkHandlers {

    private static ClientCachedStorage clientCachedStorage;

    public static void init(ClientCachedStorage clientCachedStorage) {
        ClientChunkHandlers.clientCachedStorage = clientCachedStorage;
        ClientChunkEvents.CHUNK_UNLOAD.register(ClientChunkHandlers::onChunkUnload);
    }

    private static void onChunkUnload(ClientWorld world, WorldChunk chunk) {
        clientCachedStorage.clearChunkCache(chunk.getPos());
    }
}
