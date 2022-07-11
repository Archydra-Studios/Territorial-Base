package io.github.profjb58.territorial.networking.c2s;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
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

public class CreateTeamPacket extends C2SPacket {

    private String teamName;
    private ItemStack bannerStack;
    private int bannerBaseColourId;

    private static ServerTeamManager teamManager;

    public CreateTeamPacket(ServerTeamManager teamManager) {
        CreateTeamPacket.teamManager = teamManager;
    }

    public CreateTeamPacket(String teamName, ItemStack bannerStack, int bannerBaseColourId) {
        this.teamName = teamName;
        this.bannerStack = bannerStack;
        this.bannerBaseColourId = bannerBaseColourId;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(teamName);
        buf.writeItemStack(bannerStack);
        buf.writeInt(bannerBaseColourId);

    }

    @Override
    public void read(PacketByteBuf buf) {
        teamName = buf.readString();
        bannerStack = buf.readItemStack();
        bannerBaseColourId = buf.readInt();
    }

    @Override
    public Identifier getId() {
        return TerritorialRegistry.CREATE_TEAM_PACKET_ID;
    }

    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        teamManager.createTeam(teamName, new Team.Banner(bannerStack, DyeColor.byId(bannerBaseColourId)), player);
    }
}
