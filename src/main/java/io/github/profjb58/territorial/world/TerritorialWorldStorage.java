package io.github.profjb58.territorial.world;

import io.github.profjb58.territorial.Territorial;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.PersistentStateManager;

import java.util.EnumMap;

public class TerritorialWorldStorage {

    public enum ManagerType { CHUNKS, TEAMS }
    private final EnumMap<ManagerType, PersistentStateManager> persistentStateManagers = new EnumMap<>(ManagerType.class);

    public TerritorialWorldStorage(MinecraftServer server) {
        var levelDataPath = server.getSavePath(WorldSavePath.ROOT).resolve(Territorial.MOD_ID);

        for(var managerType : ManagerType.values()) {
             var dataDirectory = levelDataPath.resolve(managerType.name().toLowerCase()).toFile();
             dataDirectory.mkdirs();
             persistentStateManagers.put(managerType, new PersistentStateManager(dataDirectory, server.getDataFixer()));
        }
    }

    public PersistentStateManager getPersistentStateManager(ManagerType managerType) {
        return persistentStateManagers.get(managerType);
    }
}
