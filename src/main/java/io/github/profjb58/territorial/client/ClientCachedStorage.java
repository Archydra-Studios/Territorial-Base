package io.github.profjb58.territorial.client;

import io.github.profjb58.territorial.util.LockUtils.LockType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class ClientCachedStorage {
    private final HashMap<BlockPos, LockType> nearbyLockables = new HashMap<>();

    public ClientCachedStorage() {}

    public void addLockable(BlockPos pos, LockType lt) {
        nearbyLockables.put(pos, lt);
    }

    public void removeLockable(BlockPos pos) {
        nearbyLockables.remove(pos);
    }

    public LockType getLockable(BlockPos pos) {
        return nearbyLockables.getOrDefault(pos, null);
    }

    public void clearCache() {
        nearbyLockables.clear();
    }
}
