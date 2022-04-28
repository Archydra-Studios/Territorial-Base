package io.github.profjb58.territorial.event.registry;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.networking.AddEclipseEffectPacket;
import io.github.profjb58.territorial.networking.CreateTeamPacket;
import io.github.profjb58.territorial.networking.C2SPacket;
import io.github.profjb58.territorial.networking.StartBreakingBlockPacket;
import net.minecraft.util.Identifier;

public class TerritorialNetworkRegistry  {

    public static final Identifier CREATE_TEAM_PACKET_ID = new Identifier(Territorial.MOD_ID, "create_team");
    public static final Identifier ADD_ECLIPSE_EFFECT_PACKET_ID = new Identifier(Territorial.MOD_ID, "add_eclipse_effect");
    public static final Identifier START_BREAKING_BLOCK_PACKET_ID = new Identifier(Territorial.MOD_ID, "start_breaking_block");

    public static void registerClientPackets() {
        C2SPacket.register(CREATE_TEAM_PACKET_ID, new CreateTeamPacket());
        C2SPacket.register(ADD_ECLIPSE_EFFECT_PACKET_ID, new AddEclipseEffectPacket());
        C2SPacket.register(START_BREAKING_BLOCK_PACKET_ID, new StartBreakingBlockPacket());
    }

    public static void registerServerPackets() {

    }
}
