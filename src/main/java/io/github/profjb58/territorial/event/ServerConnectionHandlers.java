package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.world.team.ServerTeam;
import io.github.profjb58.territorial.world.team.ServerTeamManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class ServerConnectionHandlers {

    private static Territorial modInstance;

    public static void init(Territorial modInstance) {
        ServerConnectionHandlers.modInstance = modInstance;
        ServerPlayConnectionEvents.JOIN.register(ServerConnectionHandlers::onPlayerConnect);
    }

    private static void onPlayerConnect(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        modInstance.getTeamManager().updateLastLogin(handler.getPlayer());
        modInstance.getTeamManager().checkInactive();
    }
}
