package io.github.profjb58.territorial.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialServer;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.lwjgl.system.CallbackI;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class BlacklistCommands {

    private static final SimpleCommandExceptionType ADD_BLOCK_FAILED = new SimpleCommandExceptionType(
            new TranslatableText("message.territorial.blacklist.add_block_failed")
    );

    private static final SimpleCommandExceptionType REMOVE_BLOCK_FAILED = new SimpleCommandExceptionType(
            new TranslatableText("message.territorial.blacklist.remove_block_failed")
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("territorial")
                .then(literal("blacklist")
                        .requires(source -> source.hasPermissionLevel(TerritorialServer.minOpLevel))
                        .then(literal("add")
                                .then(argument("block to add", BlockStateArgumentType.blockState())
                                        .executes(BlacklistCommands::addBlockToBlacklist)
                                )
                        )
                        .then(literal("remove")
                                .then(argument("block to remove", BlockStateArgumentType.blockState())
                                        .executes(BlacklistCommands::removeBlockFromBlacklist)
                                )
                        )
                        .then(literal("list")
                                .executes(BlacklistCommands::listBlacklist)
                        )
                )
        );
    }

    private static int addBlockToBlacklist(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var blockState = BlockStateArgumentType.getBlockState(ctx, "block to add").getBlockState();
        var player = ctx.getSource().getPlayer();

        if(Territorial.LOCKABLES_BLACKLIST.addBlock(blockState.getBlock())) {
            player.sendMessage(new TranslatableText("message.territorial.blacklist.add_block_success"), false);
            return Command.SINGLE_SUCCESS;
        }
        else
            throw ADD_BLOCK_FAILED.create();
    }

    private static int removeBlockFromBlacklist(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        var blockState = BlockStateArgumentType.getBlockState(ctx, "block to remove").getBlockState();
        var player = ctx.getSource().getPlayer();

        if(Territorial.LOCKABLES_BLACKLIST.removeBlock(blockState.getBlock())) {
            player.sendMessage(new TranslatableText("message.territorial.blacklist.remove_block_success"), false);
            return Command.SINGLE_SUCCESS;
        }
        else
            throw REMOVE_BLOCK_FAILED.create();
    }

    private static int listBlacklist(CommandContext<ServerCommandSource> ctx) {
        try {
            var player = ctx.getSource().getPlayer();
            var stringBuilder = new StringBuilder();

            stringBuilder.append("\n").append("§a ================= BLACKLISTED BLOCKS =================§r").append("\n ");
            for (String blacklistedBlock : Territorial.LOCKABLES_BLACKLIST.asList()) {
                String[] split = blacklistedBlock.split(":");

                if(!split[0].equals("minecraft"))
                    stringBuilder.append("§a").append(split[0]).append("§r:");
                stringBuilder.append(split[1]).append(" §7|§r ");
            }
            player.sendMessage(new LiteralText(stringBuilder.toString()), false);
        } catch (CommandSyntaxException cse) {
            cse.printStackTrace();
        }
        return Command.SINGLE_SUCCESS;
    }
}
