package io.github.profjb58.territorial.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.profjb58.territorial.util.UuidUtils;
import io.github.profjb58.territorial.world.data.LocksPersistentState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class LocksCommand {

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
                                        .executes(LocksCommand::listLocksByPlayerName)
                                )
                                .executes(LocksCommand::listLocks)
                        )
                )
        );
    }

    private static int listLocksByPlayerName(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource scs = ctx.getSource();
        String playerName = StringArgumentType.getString(ctx, "player name");
        LocksPersistentState lps = LocksPersistentState.get(scs.getWorld());
        UUID uuid = null;

        for(String serverPlayerName : scs.getPlayerNames()) {
            if(serverPlayerName.equals(playerName)) {
                ServerPlayerEntity spe = scs.getMinecraftServer().getPlayerManager().getPlayer(serverPlayerName);
                if(spe != null) {
                    uuid = spe.getUuid();
                }
            }
        }

        if(uuid == null) { // Uuid has not been found yet, try searching for offline players
            CompletableFuture<UUID> uuidFuture = CompletableFuture.supplyAsync(() -> UuidUtils.getUUIDFromPlayer(playerName));
            try {
                uuid = uuidFuture.get();
            } catch (InterruptedException | ExecutionException ignored) { }
        }

        if(uuid != null) {
            if(lps.listLocks(uuid, scs.getPlayer())) return Command.SINGLE_SUCCESS;
            else throw LOCKS_MATCH_FAILED.create();
        }
        else {
            throw LOCKS_MATCH_FAILED.create();
        }
    }

    private static int listLocks(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource scs = ctx.getSource();
        LocksPersistentState lps = LocksPersistentState.get(scs.getWorld());
        if(lps.listLocks(scs.getPlayer().getUuid(), scs.getPlayer())) {
            return Command.SINGLE_SUCCESS;
        }
        else {
            throw LOCKS_MATCH_FAILED_PLAYER.create();
        }
    }
}
