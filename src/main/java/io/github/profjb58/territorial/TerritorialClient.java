package io.github.profjb58.territorial;

import io.github.profjb58.territorial.client.ClientCachedStorage;
import io.github.profjb58.territorial.client.gui.LockableHud;
import io.github.profjb58.territorial.client.gui.LockableScreen;
import io.github.profjb58.territorial.event.ClientChunkHandlers;
import io.github.profjb58.territorial.event.ClientTickHandlers;
import io.github.profjb58.territorial.event.registry.TerritorialClientRegistry;
import io.github.profjb58.territorial.client.ClientTeamManager;
import net.fabricmc.api.ClientModInitializer;

import javax.annotation.Nullable;

public class TerritorialClient implements ClientModInitializer {

    public static final ClientTeamManager CLIENT_TEAMS_HANDLER = new ClientTeamManager();
    private static final ClientCachedStorage CLIENT_CACHED_STORAGE = new ClientCachedStorage();

    @Nullable
    public static LockableScreen lockableScreen;
    public static LockableHud lockableHud;


    @Override
    public void onInitializeClient() {
        TerritorialClientRegistry.registerAll();
        ClientTickHandlers.init();
        ClientChunkHandlers.init(CLIENT_CACHED_STORAGE);
    }
}
