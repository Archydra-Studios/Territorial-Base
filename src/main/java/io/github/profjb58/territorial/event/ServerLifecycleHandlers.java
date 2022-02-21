package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.database.Database;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerLifecycleHandlers {

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            //Database.getInstance();

        });

        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            //Database.getInstance().close();
        });
    }
}
