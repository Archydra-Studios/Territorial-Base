package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.SafeBlock;
import io.github.profjb58.territorial.blockEntity.SafeBlockEntity;
import io.github.profjb58.territorial.command.LocksCommand;
import io.github.profjb58.territorial.item.LockpickItem;
import io.github.profjb58.territorial.item.LockpickItem.LockPickType;
import io.github.profjb58.territorial.item.PadlockItem;
import io.github.profjb58.territorial.effect.LockFatigueEffect;
import io.github.profjb58.territorial.util.LockUtils.*;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TerritorialRegistry {

    // Items
    public static final Item KEY = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(16));
    public static final Item PADLOCK = new PadlockItem(LockType.IRON);
    public static final Item PADLOCK_GOLD = new PadlockItem(LockType.GOLD);
    public static final Item PADLOCK_DIAMOND = new PadlockItem(LockType.DIAMOND);
    public static final Item PADLOCK_NETHERITE = new PadlockItem(LockType.NETHERITE);
    public static final Item PADLOCK_CREATIVE = new PadlockItem(LockType.CREATIVE);
    public static final Item LOCKPICK = new LockpickItem(LockPickType.NORMAL);
    public static final Item LOCKPICK_CREATIVE = new LockpickItem(LockPickType.CREATIVE);
    public static final Item ENDER_AMULET = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP));

    // Blocks
    public static final Block SAFE_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));

    // Block Entities
    public static final BlockEntityType<SafeBlockEntity> SAFE_BLOCK_ENTITY = BlockEntityType.Builder.create(SafeBlockEntity::new, SAFE_BLOCK).build(null);

    public static final StatusEffect LOCK_FATIGUE = new LockFatigueEffect();

    public static void registerAll() {
        registerItems();
        registerBlocks();
        registerBlockEntities();
        registerCommands();
        registerStatusEffects();
    }

    private static void registerItems() {
        // Locks
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "key"), KEY);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock"), PADLOCK);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_gold"), PADLOCK_GOLD);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_diamond"), PADLOCK_DIAMOND);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_netherite"), PADLOCK_NETHERITE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_creative"), PADLOCK_CREATIVE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "lockpick"), LOCKPICK);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "lockpick_creative"), LOCKPICK_CREATIVE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "ender_amulet"), ENDER_AMULET);
    }

    private static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(Territorial.MOD_ID, "safe"), SAFE_BLOCK);
    }

    private static void registerBlockEntities() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Territorial.MOD_ID), SAFE_BLOCK_ENTITY);
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LocksCommand.register(dispatcher);
        });
    }

    private static void registerStatusEffects() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Territorial.MOD_ID, "lock_fatigue"), LOCK_FATIGUE);
    }
}
