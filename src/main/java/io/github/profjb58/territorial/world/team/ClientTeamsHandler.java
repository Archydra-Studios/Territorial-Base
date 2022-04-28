package io.github.profjb58.territorial.world.team;

import io.github.profjb58.territorial.networking.C2SPackets;
import io.github.profjb58.territorial.util.UuidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@Environment(EnvType.CLIENT)
public class ClientTeamsHandler {
    private final List<Team> TEAMS = new LinkedList<>();
    private Team activeTeam = null;

    public enum MemberPacketAction { ADD_MEMBER, REMOVE_MEMBER, PROMOTE_MEMBER }

    public ClientTeamsHandler() {}

    public void removeTeam(UUID teamId) {
        var buf = PacketByteBufs.create();
        buf.writeUuid(teamId);
        ClientPlayNetworking.send(C2SPackets.REMOVE_TEAM, buf);
    }

    public void renameTeam(UUID oldTeamId, String newName) {
        var buf = PacketByteBufs.create();
        buf.writeUuid(oldTeamId);
        buf.writeString(newName);
        ClientPlayNetworking.send(C2SPackets.RENAME_TEAM, buf);
    }

}
