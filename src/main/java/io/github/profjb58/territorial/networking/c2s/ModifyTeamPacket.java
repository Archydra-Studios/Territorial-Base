package io.github.profjb58.territorial.networking.c2s;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.server.team.ServerTeam;
import io.github.profjb58.territorial.server.team.ServerTeamManager;
import io.github.profjb58.territorial.team.Team;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ModifyTeamPacket extends C2SPacket {

    private UUID oldId;
    private String newName;
    private ItemStack newBannerStack;
    private int newBannerBaseColourId;

    private static ServerTeamManager teamManager;
    public ModifyTeamPacket(ServerTeamManager teamManager) {
        ModifyTeamPacket.teamManager = teamManager;
    }

    public ModifyTeamPacket(UUID oldId, String newName, ItemStack newBannerStack, int newBannerBaseColourId) {
        this.oldId = oldId;
        this.newName = newName;
        this.newBannerStack = newBannerStack;
        this.newBannerBaseColourId = newBannerBaseColourId;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var serverTeam = (ServerTeam) teamManager.getTeamById(oldId);
        if(serverTeam != null) serverTeam.setIdentifyingData(newName, new Team.Banner(newBannerStack, DyeColor.byId(newBannerBaseColourId)));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(oldId);
        buf.writeString(newName);
        buf.writeItemStack(newBannerStack);
        buf.writeInt(newBannerBaseColourId);
    }

    @Override
    public void read(PacketByteBuf buf) {
        oldId = buf.readUuid();
        newName = buf.readString();
        newBannerStack = buf.readItemStack();
        newBannerBaseColourId = buf.readInt();
    }

    @Override
    public Identifier getId() {
        return TerritorialRegistry.MODIFY_TEAM_PACKET_ID;
    }
}
