package io.github.profjb58.territorial.networking.c2s;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import io.github.profjb58.territorial.networking.c2s.C2SPacket;
import io.github.profjb58.territorial.util.NbtUtils;
import io.github.profjb58.territorial.world.team.ServerTeam;
import io.github.profjb58.territorial.world.team.Team;
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

    public RemoveTeamPacket() {}

    public RemoveTeamPacket(UUID teamId, ServerTeam.Members members) {
        this.id = teamId;
        this.members = members;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Territorial.TEAMS_HANDLER.removeTeam(id, members);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(id);
        buf.writeNbt(NbtUtils.getNbtFromMembers(members));
    }

    @Override
    public void read(PacketByteBuf buf) {
        id = buf.readUuid();
        members = NbtUtils.getMembersFromNbt(buf.readNbt());
    }

    @Override
    public Identifier getId() {
        return TerritorialNetworkRegistry.REMOVE_TEAM_PACKET_ID;
    }
}
