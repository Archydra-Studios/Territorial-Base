package io.github.profjb58.territorial.networking.s2c;

import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.block.enums.LockType;
import io.github.profjb58.territorial.client.gui.LockableHud;
import io.github.profjb58.territorial.client.gui.LockableScreen;
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

    public enum DisplayLocation { HUD, SCREEN }

    private DisplayLocation displayLocation;
    private NbtCompound lockableNbt;

    public SyncLockInfoPacket() {}

    public SyncLockInfoPacket(ServerPlayerEntity player, LockableBlockEntity lbe, boolean obfuscate, DisplayLocation displayLocation) {
        super(List.of(player));
        this.displayLocation = displayLocation;

        lockableNbt = lbe.getNbt();
        if(lockableNbt != null) {
            if(obfuscate) lockableNbt.putString("lock_id", "§k" + lockableNbt.getString("lock_id"));
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeNbt(lockableNbt);
        buf.writeEnumConstant(displayLocation);
    }

    @Override
    public void read(PacketByteBuf buf) {
        lockableNbt = buf.readNbt();
        displayLocation = buf.readEnumConstant(DisplayLocation.class);
    }

    @Override
    public Identifier getId() {
        return TerritorialNetworkRegistry.SYNC_LOCK_INFO_PACKET_ID;
    }

    @Override
    void execute(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String lockId = lockableNbt.getString("lock_id");
        String lockOwner = lockableNbt.getString("lock_owner_name");
        var lockType = LockType.getTypeFromInt(lockableNbt.getInt("lock_type"));
        var lockOwnerUuid = lockableNbt.getUuid("lock_owner_uuid");

        var lb = new LockableBlock(lockId, lockOwnerUuid, lockOwner, lockType);

        if(client.player != null) {
            if(displayLocation == DisplayLocation.HUD) {
                if(TerritorialClient.lockableHud != null) TerritorialClient.lockableHud.clear();
                TerritorialClient.lockableHud = new LockableHud(lb);
                TerritorialClient.lockableHud.show();
            }
            else TerritorialClient.lockableScreen = new LockableScreen(client.player, lb);
        }
    }
}
