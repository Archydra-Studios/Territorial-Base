package io.github.profjb58.territorial.world.team;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@Environment(EnvType.CLIENT)
public class ClientTeamsHandler {
    private final List<Team> TEAMS = new LinkedList<>();
    private Team activeTeam = null;

    public enum MemberPacketAction { ADD_MEMBER, REMOVE_MEMBER, PROMOTE_MEMBER }

    public ClientTeamsHandler() {}
}
