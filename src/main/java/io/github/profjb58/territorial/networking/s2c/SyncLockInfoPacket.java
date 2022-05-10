package io.github.profjb58.territorial.networking.s2c;

import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class SyncLockInfoPacket extends S2CPacket {

    private NbtCompound lockableNbt;

    public SyncLockInfoPacket() {}

    public SyncLockInfoPacket(ServerPlayerEntity player, LockableBlockEntity lbe, boolean obfuscate) {
        super(List.of(player));

        lockableNbt = lbe.getNbt();
        if(lockableNbt != null) {
            lockableNbt.remove("lock_owner_uuid");
            if(obfuscate) lockableNbt.putString("lock_id", "§k" + lockableNbt.getString("lock_id"));
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeNbt(lockableNbt);
    }

    @Override
    public void read(PacketByteBuf buf) {
        lockableNbt = buf.readNbt();
    }

    @Override
    public Identifier getId() {
        return TerritorialNetworkRegistry.SYNC_LOCK_INFO_PACKET_ID;
    }

    @Override
    void execute(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String lockId = lockableNbt.getString("lock_id");
        String lockOwner = lockableNbt.getString("lock_owner_name");
        var lockType = LockableBlock.lockType(lockableNbt.getInt("lock_type"));

        var lb = new LockableBlock(lockId, lockOwner, lockType);
        if(client.player != null) TerritorialClient.lockableHud.showLockInfo(lb);
    }
}
