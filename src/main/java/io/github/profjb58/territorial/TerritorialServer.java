package io.github.profjb58.territorial;

import io.github.profjb58.territorial.util.ActionLogger;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TerritorialServer implements DedicatedServerModInitializer  {

    public static ActionLogger actionLogger;
    public static int minOpLevel = 3;

    @Override
    public void onInitializeServer() {
        actionLogger = new ActionLogger();
        actionLogger.write(ActionLogger.LogType.INFO, "Server started... ");

        ServerLifecycleEvents.SERVER_STARTED.register(
                server -> TerritorialServer.minOpLevel = server.getOpPermissionLevel());
    }
}
