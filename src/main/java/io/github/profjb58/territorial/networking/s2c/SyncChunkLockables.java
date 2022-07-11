package io.github.profjb58.territorial.networking.s2c;

import io.github.profjb58.territorial.util.NbtUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;

public class SyncChunkLockables extends S2CPacket {

    private RegistryKey<World> worldRegistryKey;
    private ChunkPos chunkPos;
    private LongOpenHashSet lockableBlocks;

    public SyncChunkLockables() {}

    public SyncChunkLockables(ServerPlayerEntity recipient, ServerWorld world, ChunkPos chunkPos, HashMap<Long, Long> lockableBlocks) {
        super(List.of(recipient));
        worldRegistryKey = world.getRegistryKey();
        this.chunkPos = chunkPos;
        this.lockableBlocks.addAll(lockableBlocks.keySet());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(worldRegistryKey.getValue());
        buf.writeIntArray(NbtUtils.serializeChunkPos(chunkPos));
        buf.writeLongArray(lockableBlocks.toLongArray());
    }

    @Override
    public void read(PacketByteBuf buf) {
        worldRegistryKey = RegistryKey.of(Registry.WORLD_KEY, buf.readIdentifier());
        chunkPos = NbtUtils.deserializeChunkPos(buf.readIntArray());
        lockableBlocks = new LongOpenHashSet(buf.readLongArray());
    }

    @Override
    void execute(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

    }

    @Override
    public Identifier getId() {
        return null;
    }
}
