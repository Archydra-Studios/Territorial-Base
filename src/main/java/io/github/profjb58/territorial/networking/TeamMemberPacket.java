package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import io.github.profjb58.territorial.world.team.ServerTeam;
import io.github.profjb58.territorial.world.team.ServerTeamsHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class TeamMemberPacket extends C2SPacket {

    private ServerTeamsHandler.MemberAction action;
    private UUID memberUuid, teamId;
    private ServerTeam.Members.Role role;

    public TeamMemberPacket() {}

    public TeamMemberPacket(ServerTeamsHandler.MemberAction action, UUID teamId, ServerTeam.Members.Role role, UUID memberUuid) {
        this.action = action;
        this.teamId = teamId;
        this.memberUuid = memberUuid;
        this.role = role;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Territorial.TEAMS_HANDLER.doMemberAction(action, teamId, memberUuid, role);
    }

    @Override
    public void write(PacketByteBuf buf) {
        var nbtCompound = new NbtCompound();
        nbtCompound.putUuid("member_uuid", memberUuid);
        nbtCompound.putUuid("team_id", teamId);
        buf.writeNbt(nbtCompound);
        buf.writeEnumConstant(action);
        buf.writeEnumConstant(role);
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
        action = buf.readEnumConstant(ServerTeamsHandler.MemberAction.class);
        role = buf.readEnumConstant(ServerTeam.Members.Role.class);
    }

    @Override
    public Identifier getId() {
        return TerritorialNetworkRegistry.TEAM_MEMBER_PACKET_ID;
    }
}
