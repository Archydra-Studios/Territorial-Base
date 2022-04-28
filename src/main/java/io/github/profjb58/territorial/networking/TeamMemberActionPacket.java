package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.util.UuidUtils;
import io.github.profjb58.territorial.world.team.ClientTeamsHandler;
import io.github.profjb58.territorial.world.team.Team;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class TeamMemberActionPacket implements C2SPacket {

    private Action action;
    private String teamName;
    private UUID memberUuid;
    private Team.Members.Role role;

    public enum Action { ADD_MEMBER, REMOVE_MEMBER, PROMOTE_MEMBER }

    public TeamMemberActionPacket() {}

    public TeamMemberActionPacket(String teamName, Team.Members.Role role, UUID memberUuid, Action action) {
        this.action = action;
        this.teamName = teamName;
        this.memberUuid = memberUuid;
        this.role = role;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(memberUuid);
        buf.writeString(teamName);
        buf.writeEnumConstant(action);
        buf.writeEnumConstant(role);
    }

    @Override
    public void read(PacketByteBuf buf) {
        memberUuid = buf.readUuid();
        teamName = buf.readString();
        action = buf.readEnumConstant(Action.class);
        role = buf.readEnumConstant(Team.Members.Role.class);
    }

    @Override
    public Identifier getId() {
        return null;
    }
}
