package io.github.profjb58.territorial.command.subcommand;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialServer;
import io.github.profjb58.territorial.command.SubCommand;
import io.github.profjb58.territorial.config.LockablesBlacklistHandler;
import io.github.profjb58.territorial.util.CommandUtils;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class BlacklistCommand implements SubCommand {

    private static final SimpleCommandExceptionType ADD_BLOCK_FAILED = new SimpleCommandExceptionType(
            new TranslatableText("message.territorial.blacklist.already_blacklisted")
    );

    private static final SimpleCommandExceptionType REMOVE_BLOCK_FAILED = new SimpleCommandExceptionType(
            new TranslatableText("message.territorial.blacklist.not_blacklisted")
    );

    private static LockablesBlacklistHandler lockablesBlacklist;

    public BlacklistCommand(LockablesBlacklistHandler lockablesBlacklist) {
        BlacklistCommand.lockablesBlacklist = lockablesBlacklist;
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("blacklist")
                .requires(source -> source.hasPermissionLevel(TerritorialServer.minOpLevel))
                .then(literal("add")
                        .then(argument("block to add", BlockStateArgumentType.blockState())
                                .executes(this::addBlockToBlacklist)
                        )
                )
                .then(literal("remove")
                        .then(argument("block to remove", BlockStateArgumentType.blockState())
                                .executes(this::removeBlockFromBlacklist)
                        )
                )
                .then(literal("list")
                        .executes(this::listBlacklist)
                ).build();
    }

    private int addBlockToBlacklist(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var blockState = BlockStateArgumentType.getBlockState(ctx, "block to add").getBlockState();

        if(lockablesBlacklist.addBlock(blockState.getBlock(), ctx.getSource().getServer(), CommandUtils.getPlayerOrNull(ctx)))
            return Command.SINGLE_SUCCESS;
        else
            throw ADD_BLOCK_FAILED.create();
    }

    private int removeBlockFromBlacklist(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var blockState = BlockStateArgumentType.getBlockState(ctx, "block to remove").getBlockState();

        if(lockablesBlacklist.removeBlock(blockState.getBlock(), ctx.getSource().getServer(), CommandUtils.getPlayerOrNull(ctx)))
            return Command.SINGLE_SUCCESS;
        else
            throw REMOVE_BLOCK_FAILED.create();
    }

    private int listBlacklist(CommandContext<ServerCommandSource> ctx) {
        var entity = ctx.getSource().getEntity();

        if(entity instanceof ServerPlayerEntity player) {
            var stringBuilder = new StringBuilder();
            stringBuilder.append("\n").append("§a ================= BLACKLISTED BLOCKS =================§r").append("\n ");

            for (String blacklistedBlock : lockablesBlacklist.asList()) {
                String[] split = blacklistedBlock.split(":");

                if(!split[0].equals("minecraft"))
                    stringBuilder.append("§a").append(split[0]).append("§r:");
                stringBuilder.append(split[1]).append(" §7|§r ");
            }
            player.sendMessage(new LiteralText(stringBuilder.toString()), false);
        }
        else {
            Territorial.LOGGER.info("List of blacklisted blocks available at: ./config/territorial/lockables_blacklist.txt");
        }
        return Command.SINGLE_SUCCESS;
    }
}
