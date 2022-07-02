package io.github.profjb58.territorial;

import io.github.profjb58.territorial.client.gui.LockableHud;
import io.github.profjb58.territorial.client.gui.LockableScreen;
import io.github.profjb58.territorial.event.ClientTickHandlers;
import io.github.profjb58.territorial.event.registry.TerritorialClientRegistry;
import io.github.profjb58.territorial.world.team.ClientTeamManager;
import net.fabricmc.api.ClientModInitializer;

import javax.annotation.Nullable;

public class TerritorialClient implements ClientModInitializer {

    public static final ClientTeamManager CLIENT_TEAMS_HANDLER = new ClientTeamManager();

    @Nullable
    public static LockableScreen lockableScreen;
    public static LockableHud lockableHud;

    @Override
    public void onInitializeClient() {
        TerritorialClientRegistry.registerAll();
        //TerritorialNetworkRegistry.registerClientPackets();
        ClientTickHandlers.init();
    }
}
