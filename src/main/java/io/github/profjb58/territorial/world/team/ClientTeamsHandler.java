package io.github.profjb58.territorial.world.team;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;


@Environment(EnvType.CLIENT)
public class ClientTeamsHandler {

    List<Team> TEAMS = new ArrayList<>();

    public ClientTeamsHandler() {}


}
