package io.github.profjb58.territorial.api.event.common;

import io.github.profjb58.territorial.server.team.ServerTeam;
import io.github.profjb58.territorial.server.team.ServerTeamManager;
import io.github.profjb58.territorial.team.Team;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * Events called after a Team action is performed on the server
 */
public final class TeamEvents {

    private TeamEvents() {}

    public static final Event<Create> CREATE_EVENT = EventFactory.createArrayBacked(TeamEvents.Create.class, listeners -> (teamName, banner, owner) -> {
        for(Create listener : listeners)
            listener.onCreate(teamName, banner, owner);
    });

    public static final Event<Remove> REMOVE_EVENT = EventFactory.createArrayBacked(TeamEvents.Remove.class, listeners -> (teamId, members) -> {
        for(Remove listener : listeners)
            listener.onRemove(teamId, members);
    });

    public static final Event<MemberAction> MEMBER_ACTION_EVENT = EventFactory.createArrayBacked(TeamEvents.MemberAction.class, listeners -> (action, teamId, member, newRole) -> {
        for(MemberAction listener : listeners)
            listener.onMemberAction(action, teamId, member, newRole);
    });

    @FunctionalInterface
    public interface Create {
        /**
         * Fired after a team is created
         */
        void onCreate(String teamName, ServerTeam.Banner banner, ServerPlayerEntity owner);
    }

    @FunctionalInterface
    public interface Remove {
        /**
         * Fired after a team is removed
         */
        void onRemove(UUID teamId, Team.Members members);
    }

    @FunctionalInterface
    public interface MemberAction {
        /**
         * Fired after any action involving a member from a team is performed
         *
         * @param action The Type of action performed. e.g. ADD, REMOVE, PROMOTE, DEMOTE
         * @param teamId Unique Team ID. Used to reference the team from ServerTeamManager
         * @param member Member Associated with the action
         * @param newRole New role the member has been assigned
         */
        void onMemberAction(ServerTeamManager.MemberAction action, UUID teamId, UUID member, ServerTeam.Members.Role newRole);
    }
}
