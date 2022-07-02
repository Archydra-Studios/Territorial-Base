package io.github.profjb58.territorial.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.profjb58.territorial.Territorial;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class TerritorialCommand {

    private static LiteralCommandNode<ServerCommandSource> rootNode;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> territorialNode =
                literal("territorial")
                        .build();

        dispatcher.getRoot().addChild(territorialNode);
        rootNode = territorialNode;
    }

    public static void registerSubCommand(LiteralCommandNode<ServerCommandSource> subCommand) {
        rootNode.addChild(subCommand);
    }
}
