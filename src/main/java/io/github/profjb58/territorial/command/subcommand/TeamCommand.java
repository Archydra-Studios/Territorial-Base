package io.github.profjb58.territorial.command.subcommand;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.command.SubCommand;
import io.github.profjb58.territorial.util.dispatcher.Dispatcher;
import io.github.profjb58.territorial.util.dispatcher.ScheduledTask;
import io.github.profjb58.territorial.util.dispatcher.TaskType;
import io.github.profjb58.territorial.world.team.ServerTeamManager;
import io.github.profjb58.territorial.world.team.Team;
import net.minecraft.block.BannerBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class TeamCommand implements SubCommand {
    private static final TranslatableText NO_TEAM = new TranslatableText("message.territorial.team.no_team");
    private static final TranslatableText TEAM_REMOVE_CANCEL = new TranslatableText("message.territorial.team.remove.cancel");

    private static final SimpleCommandExceptionType MULTIPLE_TEAMS = new SimpleCommandExceptionType(
            new TranslatableText("error.territorial.team.multiple")
    );

    private static ServerTeamManager teamManager;
    private static Dispatcher dispatcher;

    public TeamCommand(ServerTeamManager teamManager, Dispatcher dispatcher) {
        TeamCommand.teamManager = teamManager;
        TeamCommand.dispatcher = dispatcher;
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("team")
                .then(literal("create")
                        .then(argument("name", StringArgumentType.greedyString())
                                .executes(TeamCommand::createTeam)
                        )
                )
                .then(literal("disband")
                        .executes(TeamCommand::removeTeam)
                )
                .then(literal("cancel")
                        .then(literal("disband")
                                .executes(TeamCommand::cancelRemoveTeam)
                        )
                ).build();
    }

    private static int createTeam(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource scs = ctx.getSource();
        String teamName = StringArgumentType.getString(ctx, "name");

        var leastUsedDyeColour = ServerTeamManager.getLeastUsedDyeColour();
        var banner = new Team.Banner(BannerBlock.getForColor(leastUsedDyeColour).asItem().getDefaultStack(), leastUsedDyeColour);
        var teamResult = teamManager.createTeam(teamName, banner, scs.getPlayer());

        switch(teamResult){
            case MULTIPLE_TEAMS -> throw MULTIPLE_TEAMS.create();
            case DUPLICATE_TEAM -> throw new SimpleCommandExceptionType(new TranslatableText("error.territorial.team.duplicate", teamName)).create();
            case SUCCESS -> scs.getPlayer().sendMessage(new TranslatableText("message.territorial.team.create.success", teamName), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int removeTeam(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        MinecraftServer server = ctx.getSource().getServer();

        Team team = teamManager.getPlayersTeam(player);
        if(team != null) {
            player.sendMessage(new TranslatableText("message.territorial.team.remove.warning", team.getName()), false);
            dispatcher.scheduleTask(player, new ScheduledTask(TaskType.TEAM_REMOVAL, 20, TimeUnit.SECONDS,
                    () -> server.execute(() -> teamManager.removeTeam(team.getId(), team.members())),
                    () -> server.execute(() -> player.sendMessage(TEAM_REMOVE_CANCEL, false))
            ));
        }
        else player.sendMessage(NO_TEAM, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int cancelRemoveTeam(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        dispatcher.failTask(ctx.getSource().getPlayer(), TaskType.TEAM_REMOVAL);
        return Command.SINGLE_SUCCESS;
    }
}
