package io.github.profjb58.territorial.networking.c2s;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import io.github.profjb58.territorial.world.team.Team;
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

    public ModifyTeamPacket() {}

    public ModifyTeamPacket(UUID oldId, String newName, ItemStack newBannerStack, int newBannerBaseColourId) {
        this.oldId = oldId;
        this.newName = newName;
        this.newBannerStack = newBannerStack;
        this.newBannerBaseColourId = newBannerBaseColourId;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var team = Territorial.TEAM_MANAGER.getTeamById(oldId);
        if(team != null) team.setIdentifyingData(newName, new Team.Banner(newBannerStack, DyeColor.byId(newBannerBaseColourId)));
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
        return TerritorialNetworkRegistry.MODIFY_TEAM_PACKET_ID;
    }
}
