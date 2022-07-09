package io.github.profjb58.territorial.networking.c2s;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.world.team.ServerTeam;
import io.github.profjb58.territorial.world.team.ServerTeamManager;
import io.github.profjb58.territorial.world.team.Team;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class TeamMemberPacket extends C2SPacket {

    private ServerTeamManager.MemberAction action;
    private UUID memberUuid, teamId;
    private Team.Members.Role role;

    private static ServerTeamManager teamManager;

    public TeamMemberPacket(ServerTeamManager teamManager) {
        TeamMemberPacket.teamManager = teamManager;
    }

    public TeamMemberPacket(ServerTeamManager.MemberAction action, UUID teamId, ServerTeam.Members.Role role, UUID memberUuid) {
        this.action = action;
        this.teamId = teamId;
        this.memberUuid = memberUuid;
        this.role = role;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        teamManager.doMemberAction(action, teamId, memberUuid, role);
    }

    @Override
    public void write(PacketByteBuf buf) {
        var nbtCompound = new NbtCompound();
        nbtCompound.putUuid("member_uuid", memberUuid);
        nbtCompound.putUuid("team_id", teamId);
        buf.writeNbt(nbtCompound);
        buf.writeEnumConstant(action);
        buf.writeString(role.name());
        buf.writeInt(role.rank());
    }

    @Override
    public void read(PacketByteBuf buf) {
        var nbtCompound = buf.readNbt();
        if(nbtCompound != null) {
            if(nbtCompound.contains("member_uuid"))
                memberUuid = nbtCompound.getUuid("member_uuid");
            if(nbtCompound.contains("team_id"))
                teamId = nbtCompound.getUuid("team_id");
        }
        action = buf.readEnumConstant(ServerTeamManager.MemberAction.class);
        role = new Team.Members.Role(buf.readString(), buf.readInt());
    }

    @Override
    public Identifier getId() {
        return TerritorialRegistry.TEAM_MEMBER_PACKET_ID;
    }
}
