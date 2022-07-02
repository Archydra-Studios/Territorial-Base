package io.github.profjb58.territorial.event.registry;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.networking.c2s.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;

public class TerritorialNetworkRegistry  {

    // C2S Packets
    public static final Identifier CREATE_TEAM_PACKET_ID = new Identifier(Territorial.MOD_ID, "create_team_packet");
    public static final Identifier REMOVE_TEAM_PACKET_ID = new Identifier(Territorial.MOD_ID, "remove_team_packet");
    public static final Identifier TEAM_MEMBER_PACKET_ID = new Identifier(Territorial.MOD_ID, "team_member_packet");
    public static final Identifier MODIFY_TEAM_PACKET_ID = new Identifier(Territorial.MOD_ID, "modify_team_packet");
    public static final Identifier ADD_ECLIPSE_EFFECT_PACKET_ID = new Identifier(Territorial.MOD_ID, "add_eclipse_effect_packet");
    public static final Identifier START_BREAKING_BLOCK_PACKET_ID = new Identifier(Territorial.MOD_ID, "start_breaking_block_packet");

    // S2C Packets
    public static final Identifier SYNC_TEAM_DATA_PACKET_ID = new Identifier(Territorial.MOD_ID, "sync_team_data_packet");
    public static final Identifier SYNC_LOCK_INFO_PACKET_ID = new Identifier(Territorial.MOD_ID, "sync_lock_info_packet");

    /*public static void registerClientPackets() {
        S2CPacket.register(SYNC_TEAM_DATA_PACKET_ID, new SyncTeamDataPacket());
        S2CPacket.register(SYNC_LOCK_INFO_PACKET_ID, new SyncLockInfoPacket());
    }*/

    public static Territorial modInstance;

    public static void init(Territorial modInstance) {
        TerritorialNetworkRegistry.modInstance = modInstance;
        ServerLifecycleEvents.SERVER_STARTED.register(server -> registerServerPackets());
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> registerClientPackets());
    }

    private static void registerServerPackets() {
        C2SPacket.register(CREATE_TEAM_PACKET_ID, new RemoveTeamPacket(modInstance.getTeamManager()));
        C2SPacket.register(REMOVE_TEAM_PACKET_ID, new CreateTeamPacket(modInstance.getTeamManager()));
        C2SPacket.register(TEAM_MEMBER_PACKET_ID, new TeamMemberPacket(modInstance.getTeamManager()));
        C2SPacket.register(MODIFY_TEAM_PACKET_ID, new ModifyTeamPacket(modInstance.getTeamManager()));
        C2SPacket.register(ADD_ECLIPSE_EFFECT_PACKET_ID, new AddEclipseEffectPacket());
        C2SPacket.register(START_BREAKING_BLOCK_PACKET_ID, new StartBreakingBlockPacket());
    }

    private static void registerClientPackets() {

    }
}
