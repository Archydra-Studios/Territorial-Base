package io.github.profjb58.territorial.event;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.item.LockpickItem;
import io.github.profjb58.territorial.item.PadlockItem;
import io.github.profjb58.territorial.util.UuidUtils;
import io.github.profjb58.territorial.world.data.LocksPersistentState;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static net.minecraft.server.command.CommandManager.*;

public class TerritorialRegistry {

    // Locks
    public static final Item KEY = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(16));
    public static final Item PADLOCK = new PadlockItem(1);
    public static final Item PADLOCK_DIAMOND = new PadlockItem(3);
    public static final Item PADLOCK_NETHERITE = new PadlockItem(4);
    public static final Item PADLOCK_CREATIVE = new PadlockItem(-1);
    public static final Item LOCKPICK = new LockpickItem();
    public static final Item LOCKPICK_CREATIVE = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(1));
    public static final Item ENDER_AMULET = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP));

    public static void registerAll() {
        registerItems();
        registerCommands();
    }

    private static void registerItems() {
        // Locks
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "key"), KEY);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock"), PADLOCK);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_diamond"), PADLOCK_DIAMOND);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_netherite"), PADLOCK_NETHERITE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_creative"), PADLOCK_CREATIVE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "lockpick"), LOCKPICK);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "lockpick_creative"), LOCKPICK_CREATIVE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "ender_amulet"), ENDER_AMULET);
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {

            dispatcher.register(literal("territorial")
                    .then(literal("locks")
                            .then(literal("list")
                                    .then(argument("player name", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                ServerCommandSource scs = ctx.getSource();
                                                String playerName = StringArgumentType.getString(ctx, "player name");
                                                LocksPersistentState lps = LocksPersistentState.get(scs.getWorld());

                                                CompletableFuture<UUID> uuidFuture = CompletableFuture.supplyAsync(() -> UuidUtils.getUUIDFromPlayer(playerName));
                                                try {
                                                    UUID uuid = uuidFuture.get();
                                                    if (uuid != null) {
                                                        lps.listLocks(uuid, scs.getPlayer());
                                                        Territorial.logger.info("Found UUID: " + uuid + " for player: " + scs.getPlayer().getName().getString());
                                                    } else {
                                                        return -1;
                                                    }
                                                } catch (InterruptedException | ExecutionException e) {
                                                    // Add exception signature here
                                                }

                                                return 1;
                                            })
                                    )
                                    .executes(ctx-> {
                                        ServerCommandSource scs = ctx.getSource();
                                        LocksPersistentState lps = LocksPersistentState.get(scs.getWorld());
                                        lps.listLocks(scs.getPlayer().getUuid(), scs.getPlayer());
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }
}
