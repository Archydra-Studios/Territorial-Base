package io.github.profjb58.territorial.world.team;

import io.github.profjb58.territorial.Territorial;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.world.PersistentState;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class ServerTeamsHandler extends PersistentState {

    private static final Text TEAM_CREATION_SUCCESSFUL, TEAM_CREATION_FAILED;

    private final Set<Team> teams = new TreeSet<>((Comparator<Team> & Serializable) (team1, team2) ->
            team1.members().size() - team2.members().size());
    private final Map<UUID, List<Team>> teamsByUuid = new HashMap<>();

    public ServerTeamsHandler() {}

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return null;
    }

    public void reload() {
        Iterator<Team> it = teams.iterator();

        // Check if any teams are in-active
        boolean purgeTeams = Territorial.getConfig().purgeTeams();
        int numDaysBeforeInactive = Territorial.getConfig().getNumDaysBeforeInactive();
        int numDaysBeforePurge = Territorial.getConfig().getNumDaysBeforePurge();

        for (Team team : teams) {
            if (!team.isInactive) {
                long epochTimeNow = Date.from(Instant.EPOCH).getTime();
                long epochTimeLastLogin = team.lastLoginDate.getTime();

                int timeDiffDays = (int) (epochTimeNow - epochTimeLastLogin) / 86400000;
                if (timeDiffDays > numDaysBeforeInactive)
                    team.isInactive = true;

                if(purgeTeams && timeDiffDays > numDaysBeforePurge)
                    continue; // TODO - Do Purging things
            }
        }
    }

    public void updateLastLogin(ServerPlayerEntity player) {
        if(teamsByUuid.containsKey(player.getUuid())) {
            for(Team team : teamsByUuid.get(player.getUuid())) {
                team.lastLoginDate = Date.from(Instant.EPOCH);
                team.isInactive = false;
            }
        }
    }

    public void createTeam(String name, Team.Banner banner, ServerPlayerEntity owner) {
        boolean isUnique = teams.stream().noneMatch((team -> team.getName().equals(name) || team.getBanner().equals(banner)));
        if(isUnique) {
            var team = new Team(name, banner, owner);
            if(teamsByUuid.containsKey(owner.getUuid())) {
                List<Team> ownersTeam = teamsByUuid.get(owner.getUuid());
                ownersTeam.add(team);
            }
            teams.add(team);
            owner.sendMessage(TEAM_CREATION_SUCCESSFUL, false); // TODO - Replace with proper implementation
        }
        else owner.sendMessage(TEAM_CREATION_FAILED, false);
    }

    public List<Team> getPlayersTeams(ServerPlayerEntity player) { return teamsByUuid.get(player.getUuid()); }

    static {
        TEAM_CREATION_SUCCESSFUL = new TranslatableText("team.territorial.creation.successful");
        TEAM_CREATION_FAILED = new TranslatableText("team.territorial.creation.failed");
    }
}
