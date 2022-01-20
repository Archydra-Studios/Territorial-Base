package io.github.profjb58.territorial;

import io.github.profjb58.territorial.util.ActionLogger;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TerritorialServer implements DedicatedServerModInitializer  {

    public static ActionLogger actionLogger;

    @Override
    public void onInitializeServer() {
        actionLogger = new ActionLogger();
        actionLogger.write(ActionLogger.LogType.INFO, "Server started... ");
    }
}
