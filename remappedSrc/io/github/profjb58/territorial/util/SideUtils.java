package io.github.profjb58.territorial.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class SideUtils {

    public static boolean isDedicatedServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }
}
