package io.github.profjb58.territorial.networking.s2c;

import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.client.ClientCachedStorage;
import io.github.profjb58.territorial.event.registry.TerritorialClientRegistry;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.NbtUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class SyncChunkLockablesPacket extends S2CPacket {

    private static ClientCachedStorage clientCachedStorage;

    private ChunkPos chunkPos;
    private LongOpenHashSet lockableBlocks = new LongOpenHashSet();

    public SyncChunkLockablesPacket(ClientCachedStorage clientCachedStorage) {
        SyncChunkLockablesPacket.clientCachedStorage = clientCachedStorage;
    }

    public SyncChunkLockablesPacket(ServerPlayerEntity recipient, ChunkPos chunkPos, Map<BlockPos, BlockPos> lockedBlockPositions) {
        super(List.of(recipient));
        this.chunkPos = chunkPos;
        for(var lockedBlockPos : lockedBlockPositions.keySet())
            this.lockableBlocks.add(lockedBlockPos.asLong());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIntArray(NbtUtils.serializeChunkPos(chunkPos));
        buf.writeLongArray(lockableBlocks.toLongArray());
    }

    @Override
    public void read(PacketByteBuf buf) {
        chunkPos = NbtUtils.deserializeChunkPos(buf.readIntArray());
        lockableBlocks = new LongOpenHashSet(buf.readLongArray());
    }

    @Override
    @Environment(EnvType.CLIENT)
    void execute(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        clientCachedStorage.onSyncLockableBlocks(chunkPos, lockableBlocks);
    }

    @Override
    public Identifier getId() {
        return TerritorialClientRegistry.SYNC_CHUNK_LOCKABLES_PACKET_ID;
    }
}
