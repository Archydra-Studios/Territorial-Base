package io.github.profjb58.territorial.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class LockCommands {

    private static final SimpleCommandExceptionType LOCKS_MATCH_FAILED = new SimpleCommandExceptionType(
            new TranslatableText("message.territorial.locks_match_failed")
    );

    private static final SimpleCommandExceptionType LOCKS_MATCH_FAILED_PLAYER = new SimpleCommandExceptionType(
            new TranslatableText("message.territorial.locks_match_failed_player")
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("territorial")
                .then(literal("locks")
                        .then(literal("list")
                                .then(argument("player name", StringArgumentType.greedyString())
                                        .requires(source -> source.hasPermissionLevel(2))
                                        .executes(LockCommands::listLocksByPlayerName)
                                )
                                .executes(LockCommands::listLocks)
                        )
                )
        );
    }

    private static int listLocksByPlayerName(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource scs = ctx.getSource();
        String playerName = StringArgumentType.getString(ctx, "player name");

        /*
        WorldLockStorage lps = WorldLockStorage.get(scs.getWorld());

        UUID uuid = UuidUtils.findUuid(playerName);
        if(uuid != null) {
            if(lps.listLocks(uuid, scs.getPlayer())) return Command.SINGLE_SUCCESS;
            else throw LOCKS_MATCH_FAILED.create();
        }
        else {
            throw LOCKS_MATCH_FAILED.create();
        }
        */
        return Command.SINGLE_SUCCESS;
    }

    private static int listLocks(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource scs = ctx.getSource();

        /*
        WorldLockStorage lps = WorldLockStorage.get(scs.getWorld());
        if(lps.listLocks(scs.getPlayer().getUuid(), scs.getPlayer())) {
            return Command.SINGLE_SUCCESS;
        }
        else {
            throw LOCKS_MATCH_FAILED_PLAYER.create();
        }
        */
        return Command.SINGLE_SUCCESS;
    }
}
