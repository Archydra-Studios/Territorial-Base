package io.github.profjb58.territorial.event.registry;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.LaserBlock;
import io.github.profjb58.territorial.block.LockableBlock.LockType;
import io.github.profjb58.territorial.block.entity.LaserBlockEntity;
import io.github.profjb58.territorial.client.gui.KeyringScreenHandler;
import io.github.profjb58.territorial.command.LockCommands;
import io.github.profjb58.territorial.effect.LockFatigueEffect;
import io.github.profjb58.territorial.item.*;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TerritorialRegistry {

    // Items
    public static final Item KEY = new KeyItem(false);
    public static final Item MASTER_KEY = new KeyItem(true);
    public static final Item ENDER_KEY = new EnderKeyItem();
    public static final Item PADLOCK = new PadlockItem(LockType.IRON);
    public static final Item PADLOCK_GOLD = new PadlockItem(LockType.GOLD);
    public static final Item PADLOCK_DIAMOND = new PadlockItem(LockType.DIAMOND);
    public static final Item PADLOCK_NETHERITE = new PadlockItem(LockType.NETHERITE);
    public static final Item PADLOCK_UNBREAKABLE = new PadlockItem(LockType.UNBREAKABLE);
    public static final Item LOCKPICK = new LockpickItem();
    public static final Item ENDER_AMULET = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP));
    public static final Item KEYRING = new KeyringItem();

    // Blocks
    public static final Block SAFE_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block LASER_TRANSMITTER = new LaserBlock();

    // Block Entities
    public static final BlockEntityType<LaserBlockEntity> LASER_BLOCK_ENTITY
            = registerBlockEntity("laser_be", FabricBlockEntityTypeBuilder.create(LaserBlockEntity::new, LASER_TRANSMITTER));

    public static final LockFatigueEffect LOCK_FATIGUE = new LockFatigueEffect();

    public static final Identifier KEYRING_SCREEN_ID = new Identifier(Territorial.MOD_ID, "keyring");
    public static final ScreenHandlerType<KeyringScreenHandler> KEYRING_SCREEN_HANDLER_TYPE
            = ScreenHandlerRegistry.registerExtended(KEYRING_SCREEN_ID, KeyringScreenHandler::new);

    public static void registerAll() {
        registerItems();
        registerBlocks();
        registerCommands();
        registerStatusEffects();
    }

    private static void registerItems() {
        // Keys
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "key"), KEY);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "master_key"), MASTER_KEY);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "keyring"), KEYRING);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "ender_key"), ENDER_KEY);

        // Locks
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock"), PADLOCK);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_gold"), PADLOCK_GOLD);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_diamond"), PADLOCK_DIAMOND);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_netherite"), PADLOCK_NETHERITE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_unbreakable"), PADLOCK_UNBREAKABLE);

        // lockpicks
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "lockpick"), LOCKPICK);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "ender_amulet"), ENDER_AMULET);
    }

    private static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(Territorial.MOD_ID, "safe"), SAFE_BLOCK);

        Registry.register(Registry.BLOCK, new Identifier(Territorial.MOD_ID, "laser_transmitter"), LASER_TRANSMITTER);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "laser_transmitter"), new BlockItem(LASER_TRANSMITTER, new FabricItemSettings().group(Territorial.BASE_GROUP)));
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LockCommands.register(dispatcher);
        });
    }

    private static void registerStatusEffects() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Territorial.MOD_ID, "lock_fatigue"), LOCK_FATIGUE);
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder<T> builder) {
        BlockEntityType<T> blockEntityType = builder.build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Territorial.MOD_ID, id), blockEntityType);
        return blockEntityType;
    }
}
