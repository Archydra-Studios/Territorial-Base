package io.github.profjb58.territorial.client;

import io.github.profjb58.territorial.team.Team;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.*;


@Environment(EnvType.CLIENT)
public class ClientTeamManager {

    private static final Map<UUID, Team> CLIENT_TEAMS = new HashMap<>();

    public ClientTeamManager() {}

    void onSync() {

    }
}
