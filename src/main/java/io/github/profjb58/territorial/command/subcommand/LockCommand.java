package io.github.profjb58.territorial.command.subcommand;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.profjb58.territorial.command.SubCommand;
import io.github.profjb58.territorial.util.UuidUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class LockCommand implements SubCommand {

    private static final SimpleCommandExceptionType LOCKS_MATCH_FAILED = new SimpleCommandExceptionType(
            new TranslatableText("message.territorial.locks_match_failed")
    );

    private static final SimpleCommandExceptionType LOCKS_MATCH_FAILED_PLAYER = new SimpleCommandExceptionType(
            new TranslatableText("message.territorial.locks_match_failed_player")
    );

    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("lock")
                .then(literal("list")
                        .then(argument("player name", StringArgumentType.greedyString())
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(this::listLocksByPlayerName)
                        )
                        .executes(this::listLocks)
                ).build();
    }

    private int listLocksByPlayerName(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource scs = ctx.getSource();
        String playerName = StringArgumentType.getString(ctx, "player name");

        try {
            UuidUtils.findUuid(playerName, uuid -> {
                try {
                    scs.getPlayer().sendMessage(new LiteralText("found UUID: " + uuid.toString())
                            .formatted(Formatting.AQUA), false);
                }
                catch(CommandSyntaxException e) {}
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

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

    private int listLocks(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
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
