package io.github.profjb58.territorial;

import io.github.profjb58.territorial.client.gui.LockableHud;
import io.github.profjb58.territorial.client.gui.LockableScreen;
import io.github.profjb58.territorial.event.ClientTickHandlers;
import io.github.profjb58.territorial.event.registry.TerritorialClientRegistry;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import io.github.profjb58.territorial.util.UuidUtils;
import io.github.profjb58.territorial.world.team.ClientTeamsHandler;
import net.fabricmc.api.ClientModInitializer;
import org.apache.http.client.HttpResponseException;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;

public class TerritorialClient implements ClientModInitializer {

    public static final ClientTeamsHandler CLIENT_TEAMS_HANDLER = new ClientTeamsHandler();

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
