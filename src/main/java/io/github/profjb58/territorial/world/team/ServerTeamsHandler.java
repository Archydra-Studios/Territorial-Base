package io.github.profjb58.territorial.world.team;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.networking.SyncTeamDataPacket;
import io.github.profjb58.territorial.util.TickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.PersistentState;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;

public class ServerTeamsHandler extends PersistentState {

    private static final TickCounter MESSAGE_QUEUE_TICKER = new TickCounter(10);
    private static final Map<UUID, Queue<Text>> MEMBER_MESSAGE_QUEUE = new HashMap<>();
    
    private static final Map<UUID, ServerTeam> SERVER_TEAMS = new HashMap<>();
    private static final Map<UUID, UUID> PLAYER_TEAM_REFERENCES = new HashMap<>();

    public enum MemberAction { ADD_MEMBER, REMOVE_MEMBER, PROMOTE_MEMBER, DEMOTE_MEMBER }

    public ServerTeamsHandler() {}

    public void checkInactive() {
        boolean purgeTeams = Territorial.getConfig().purgeTeams();
        int numDaysBeforeInactive = Territorial.getConfig().getNumDaysBeforeInactive();
        int numDaysBeforePurge = Territorial.getConfig().getNumDaysBeforePurge();

        for(var team : SERVER_TEAMS.values()) {
            long epochTimeNow = Date.from(Instant.EPOCH).getTime();
            long epochTimeLastLogin = team.lastLoginDate.getTime();

            int timeDiffDays = (int) (epochTimeNow - epochTimeLastLogin) / 86400000;
            if (timeDiffDays > numDaysBeforeInactive)
                team.isInactive = true;

            if(purgeTeams && timeDiffDays > numDaysBeforePurge)
                continue; // TODO - Do Purging things
        }
    }

    public void updateLastLogin(PlayerEntity player) {
        if(hasTeam(player.getUuid())) {
            var teamId = PLAYER_TEAM_REFERENCES.get(player.getUuid());
            if(SERVER_TEAMS.containsKey(teamId)) {
                var team = SERVER_TEAMS.get(teamId);
                team.lastLoginDate = Date.from(Instant.EPOCH);
                team.isInactive = false;
            }
        }
    }

    public boolean createTeam(String teamName, ServerTeam.Banner banner, ServerPlayerEntity owner) {
        if(!PLAYER_TEAM_REFERENCES.containsKey(owner.getUuid())) {
            boolean isUnique = getTeamsSortedSet().stream().noneMatch(
                    (team -> team.getValue().getName().equals(teamName) || team.getValue().getBanner().equals(banner)));

            if(isUnique) {
                var team = new ServerTeam(teamName, banner, owner);
                PLAYER_TEAM_REFERENCES.put(owner.getUuid(), team.getId());
                SERVER_TEAMS.put(team.getId(), team);
                return true;
            }
        }
        return false;
    }

    public boolean removeTeam(UUID teamId, Team.Members members) {
        if(SERVER_TEAMS.containsKey(teamId)) {
            SERVER_TEAMS.remove(teamId);
            for(var member : members.asList())
                if(isInTeam(member, teamId)) PLAYER_TEAM_REFERENCES.remove(member);
            return true;
        }
        return false;
    }

    public boolean doMemberAction(MemberAction action, UUID teamId, UUID member, ServerTeam.Members.Role newRole) {
        if(SERVER_TEAMS.containsKey(teamId)) {
            var members = SERVER_TEAMS.get(teamId).members();
            var prevRole = members.getRole(member);

            switch(action) {
                case ADD_MEMBER -> {
                    if(prevRole == null && !hasTeam(member))
                        return members.add(newRole, member);
                }
                case REMOVE_MEMBER -> {
                    if(prevRole != null && hasTeam(member))
                        return members.remove(member);
                }
                case PROMOTE_MEMBER, DEMOTE_MEMBER -> {
                    boolean promoteSuccess, demoteSuccess;

                    if(prevRole != null && isInTeam(member, teamId)) {
                        promoteSuccess = (action == MemberAction.PROMOTE_MEMBER && newRole.getRank() > prevRole.getRank());
                        demoteSuccess = (action == MemberAction.DEMOTE_MEMBER && newRole.getRank() < prevRole.getRank());

                        if(promoteSuccess || demoteSuccess) {
                            members.remove(member);
                            return members.add(newRole, member);
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void messageQueueTick(MinecraftServer server) {
        if(MESSAGE_QUEUE_TICKER.test()) {
            for(var player : server.getPlayerManager().getPlayerList()) {
                if(MEMBER_MESSAGE_QUEUE.containsKey(player.getUuid())) {
                    var messageQueue = MEMBER_MESSAGE_QUEUE.get(player.getUuid());
                    for(var message : messageQueue)
                        player.sendMessage(message, false);
                }
            }
        }
    }

    public void addToMessageQueue(UUID playerUuid, Text message) {
        if(MEMBER_MESSAGE_QUEUE.containsKey(playerUuid))
            MEMBER_MESSAGE_QUEUE.get(playerUuid).add(message);
        else {
            var messageQueue = new LinkedList<Text>();
            messageQueue.add(message);
            MEMBER_MESSAGE_QUEUE.put(playerUuid, messageQueue);
        }
    }

    public void syncToClients(List<ServerPlayerEntity> players) {
        for(var player : players) {
            var teamUuid = PLAYER_TEAM_REFERENCES.get(player.getUuid());
            var team = (Team) SERVER_TEAMS.get(teamUuid);
            new SyncTeamDataPacket(player, team).send();
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return null;
    }

    public static TreeSet<Map.Entry<UUID, ServerTeam>> getTeamsSortedSet() {
        var teamsSortedSet = new TreeSet<>(Comparator.comparingInt((Map.Entry<UUID, ServerTeam> team) -> team.getValue().members().size()));
        teamsSortedSet.addAll(ServerTeamsHandler.SERVER_TEAMS.entrySet());
        return teamsSortedSet;
    }

    @Nullable
    public ServerTeam getTeamById(UUID id) { return SERVER_TEAMS.get(id); }

    private boolean hasTeam(UUID playerUuid) { return PLAYER_TEAM_REFERENCES.containsKey(playerUuid); }

    private boolean isInTeam(UUID playerUuid, UUID teamId) {
        if(PLAYER_TEAM_REFERENCES.containsKey(playerUuid))
            return teamId.equals(PLAYER_TEAM_REFERENCES.get(playerUuid));
        return false;
    }
}
