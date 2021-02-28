package io.github.profjb58.territorial;

import io.github.profjb58.territorial.networking.S2CPackets;
import net.fabricmc.api.ClientModInitializer;

public class TerritorialClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        S2CPackets.init();
    }
}
