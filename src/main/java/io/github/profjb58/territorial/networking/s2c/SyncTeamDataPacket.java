package io.github.profjb58.territorial.networking.s2c;

import io.github.profjb58.territorial.event.registry.TerritorialClientRegistry;
import io.github.profjb58.territorial.util.NbtUtils;
import io.github.profjb58.territorial.team.Team;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class SyncTeamDataPacket extends S2CPacket {

    private UUID id;
    private String name;
    private ItemStack bannerStack;
    private int bannerBaseColourId;
    private Team.Members members;

    public SyncTeamDataPacket() {}

    public SyncTeamDataPacket(ServerPlayerEntity recipient, Team team) {
        super(List.of(recipient));
        id = team.getId();
        name = team.getName();
        bannerStack = team.getBanner().stack();
        bannerBaseColourId = team.getBanner().baseColour().getId();
        members = team.members();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(id);
        buf.writeString(name);
        buf.writeItemStack(bannerStack);
        buf.writeInt(bannerBaseColourId);
        buf.writeNbt(NbtUtils.getMembersNbt(members));
    }

    @Override
    public void read(PacketByteBuf buf) {
        id = buf.readUuid();
        name = buf.readString();
        bannerStack = buf.readItemStack();
        bannerBaseColourId = buf.readInt();
        members = NbtUtils.getMembersFromNbt(buf.readNbt());
    }

    @Override
    public void execute(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // TODO - Handle client shit
    }

    @Override
    public Identifier getId() {
        return TerritorialClientRegistry.SYNC_TEAM_DATA_PACKET_ID;
    }
}
