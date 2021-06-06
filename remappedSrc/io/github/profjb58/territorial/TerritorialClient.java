package io.github.profjb58.territorial;

import io.github.profjb58.territorial.event.ClientTickHandlers;
import io.github.profjb58.territorial.event.registry.TerritorialClientRegistry;
import io.github.profjb58.territorial.client.gui.LockableHud;
import io.github.profjb58.territorial.networking.S2CPackets;
import net.fabricmc.api.ClientModInitializer;

public class TerritorialClient implements ClientModInitializer {

    public static LockableHud lockableHud;

    @Override
    public void onInitializeClient() {
        S2CPackets.init();
        ClientTickHandlers.init();
        TerritorialClientRegistry.registerAll();
        lockableHud = new LockableHud();
    }
}
