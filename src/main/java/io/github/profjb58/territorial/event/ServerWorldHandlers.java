package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.world.TerritorialWorldStorage;
import io.github.profjb58.territorial.event.template.ServerWorldEvents;

public class ServerWorldHandlers {

    public static void init() {
        ServerWorldEvents.SAVE_LEVEL.register(ServerWorldHandlers::onSaveLevel);
    }

    public static void onSaveLevel() {
        // Save chunk data
        Territorial.getWorldStorage().getPersistentStateManager(TerritorialWorldStorage.ManagerType.CHUNKS).save();
    }
}
