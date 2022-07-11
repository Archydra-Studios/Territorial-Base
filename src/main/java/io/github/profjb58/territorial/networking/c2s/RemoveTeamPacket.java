package io.github.profjb58.territorial.networking.c2s;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.NbtUtils;
import io.github.profjb58.territorial.server.team.ServerTeam;
import io.github.profjb58.territorial.server.team.ServerTeamManager;
import io.github.profjb58.territorial.team.Team;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class RemoveTeamPacket extends C2SPacket {

    private UUID id;
    private Team.Members members;

    private static ServerTeamManager teamManager;

    public RemoveTeamPacket(ServerTeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public RemoveTeamPacket(UUID teamId, ServerTeam.Members members) {
        this.id = teamId;
        this.members = members;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        teamManager.removeTeam(id, members);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(id);
        buf.writeNbt(NbtUtils.getMembersNbt(members));
    }

    @Override
    public void read(PacketByteBuf buf) {
        id = buf.readUuid();
        members = NbtUtils.getMembersFromNbt(buf.readNbt());
    }

    @Override
    public Identifier getId() {
        return TerritorialRegistry.REMOVE_TEAM_PACKET_ID;
    }
}
