package io.github.profjb58.territorial.command.subcommand;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.profjb58.territorial.command.SubCommand;
import io.github.profjb58.territorial.util.task.Tasks;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static io.github.profjb58.territorial.command.subcommand.TeamCommand.TEAM_REMOVAL_TASK_ID;
import static net.minecraft.server.command.CommandManager.literal;

public class CancelCommand implements SubCommand {

    private static final TranslatableText NO_TASK = new TranslatableText("message.territorial.task.not_running");

    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("cancel")
                .then(literal("disband")
                        .executes(CancelCommand::cancelRemoveTeam)
                ).build();
    }

    private static int cancelRemoveTeam(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        var task = Tasks.remove(TEAM_REMOVAL_TASK_ID, player);
        if(task != null)
            task.cancel(false);
        else
            player.sendMessage(NO_TASK, false); // No task running

        return Command.SINGLE_SUCCESS;
    }
}
