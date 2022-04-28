package io.github.profjb58.territorial;

import io.github.profjb58.territorial.event.ClientTickHandlers;
import io.github.profjb58.territorial.event.registry.TerritorialClientRegistry;
import io.github.profjb58.territorial.client.gui.LockableHud;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import io.github.profjb58.territorial.networking.C2SPackets;
import io.github.profjb58.territorial.world.team.ClientTeamsHandler;
import net.fabricmc.api.ClientModInitializer;

public class TerritorialClient implements ClientModInitializer {

    public static final ClientTeamsHandler CLIENT_TEAMS_HANDLER = new ClientTeamsHandler();
    public static LockableHud lockableHud;

    @Override
    public void onInitializeClient() {
        C2SPackets.init();
        ClientTickHandlers.init();
        TerritorialClientRegistry.registerAll();
        TerritorialNetworkRegistry.registerClientPackets();
        lockableHud = new LockableHud();
    }
}
