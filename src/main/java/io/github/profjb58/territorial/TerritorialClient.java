package io.github.profjb58.territorial;

import io.github.profjb58.territorial.networking.S2CPackets;
import io.github.profjb58.territorial.client.ClientCachedStorage;
import net.fabricmc.api.ClientModInitializer;

public class TerritorialClient implements ClientModInitializer {

    public static final ClientCachedStorage cache = new ClientCachedStorage();

    @Override
    public void onInitializeClient() {
        S2CPackets.init();
    }
}
