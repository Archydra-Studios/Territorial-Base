package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import io.github.profjb58.territorial.world.team.Team;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class CreateTeamPacket implements C2SPacket  {
    private String teamName;
    private ItemStack bannerStack;
    private int bannerBaseColourId;

    public CreateTeamPacket() {}

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
        return TerritorialNetworkRegistry.CREATE_TEAM_PACKET_ID;
    }

    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var patternListNbt = BannerBlockEntity.getPatternListNbt(bannerStack);
        var baseDyeColour = DyeColor.byId(bannerBaseColourId);
        var patterns = BannerBlockEntity.getPatternsFromNbt(baseDyeColour, patternListNbt);
        var banner = new Team.Banner(patterns, baseDyeColour);
        Territorial.TEAMS_HANDLER.createTeam(teamName, banner, player);
    }
}
