package io.github.profjb58.territorial.event.registry;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.networking.AddEclipseEffectPacket;
import io.github.profjb58.territorial.networking.CreateTeamPacket;
import io.github.profjb58.territorial.networking.C2SPacket;
import net.minecraft.util.Identifier;

public class TerritorialNetworkRegistry  {

    public static final Identifier CREATE_TEAM_PACKET_ID = new Identifier(Territorial.MOD_ID, "create_team");
    public static final Identifier ADD_ECLIPSE_EFFECT_ID = new Identifier(Territorial.MOD_ID, "add_eclipse_effect");

    public static void registerClientPackets() {
        C2SPacket.register(CREATE_TEAM_PACKET_ID, new CreateTeamPacket());
        C2SPacket.register(ADD_ECLIPSE_EFFECT_ID, new AddEclipseEffectPacket());
    }

    public static void registerServerPackets() {

    }
}
