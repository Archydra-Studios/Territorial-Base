package io.github.profjb58.territorial.event.registry;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.LaserReceiverBlock;
import io.github.profjb58.territorial.block.LaserTransmitterBlock;
import io.github.profjb58.territorial.block.LockableBlock.LockType;
import io.github.profjb58.territorial.block.OmniscientObsidianBlock;
import io.github.profjb58.territorial.block.PlinthOfPeekingBlock;
import io.github.profjb58.territorial.block.entity.LaserTransmitterBlockEntity;
import io.github.profjb58.territorial.client.gui.KeyringScreenHandler;
import io.github.profjb58.territorial.command.LockCommands;
import io.github.profjb58.territorial.enchantment.BloodshedCurseEnchantment;
import io.github.profjb58.territorial.entity.effect.LockFatigueStatusEffect;
import io.github.profjb58.territorial.item.*;
import io.github.profjb58.territorial.recipe.LensRecipe;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.CryingObsidianBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
    public static final Item ENDER_AMULET = createBlankItem();
    public static final Item KEYRING = new KeyringItem();
    public static final Item LENS = new LensItem();
    public static final Item BLANK_SLOT = new Item(new FabricItemSettings().maxCount(1));
    public static final Item DESTRUCTED_SLOT = new Item(new FabricItemSettings().maxCount(1));

    // Blocks
    public static final Block SAFE_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static final Block LASER_TRANSMITTER = new LaserTransmitterBlock();
    public static final Block LASER_RECEIVER = new LaserReceiverBlock();
    public static final Block PLINTH_OF_PEEKING = new PlinthOfPeekingBlock();
    public static final Block OMNISCIENT_OBSIDIAN = new OmniscientObsidianBlock();

    // Block Entities
    public static final BlockEntityType<LaserTransmitterBlockEntity> LASER_BLOCK_ENTITY
            = registerBlockEntity("laser_be", FabricBlockEntityTypeBuilder.create(LaserTransmitterBlockEntity::new, LASER_TRANSMITTER));

    // Status Effects
    public static final LockFatigueStatusEffect LOCK_FATIGUE = new LockFatigueStatusEffect();

    // Screen handlers
    public static final ScreenHandlerType<KeyringScreenHandler> KEYRING_SCREEN_HANDLER_TYPE
            = ScreenHandlerRegistry.registerExtended(new Identifier(Territorial.MOD_ID, "keyring"), KeyringScreenHandler::new);

    // Recipe serializers
    public static final SpecialRecipeSerializer<LensRecipe> LENS_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(LensRecipe::new);

    public static void registerAll() {
        LinkedHashMap<String, Block> blocks = new LinkedHashMap<>();
        LinkedHashMap<String, Item> items = new LinkedHashMap<>();

        // Blocks
        blocks.put("safe", SAFE_BLOCK);
        blocks.put("laser_transmitter", LASER_TRANSMITTER);
        blocks.put("laser_receiver", LASER_RECEIVER);
        blocks.put("plinth_of_peeking", PLINTH_OF_PEEKING);
        blocks.put("omniscient_obsidian", OMNISCIENT_OBSIDIAN);
        register(Registry.BLOCK, blocks);

        // Items
        items.put("key", KEY);
        items.put("master_key", MASTER_KEY);
        items.put("keyring", KEYRING);
        items.put("ender_key", ENDER_KEY);
        items.put("padlock", PADLOCK);
        items.put("padlock_gold", PADLOCK_GOLD);
        items.put("padlock_diamond", PADLOCK_DIAMOND);
        items.put("padlock_netherite", PADLOCK_NETHERITE);
        items.put("padlock_unbreakable", PADLOCK_UNBREAKABLE);
        items.put("lockpick", LOCKPICK);
        items.put("ender_amulet", ENDER_AMULET);
        items.put("lens", LENS);
        items.put("blank_slot", BLANK_SLOT);
        items.put("destructed_slot", DESTRUCTED_SLOT);

        // Block items
        items.put("laser_transmitter", createBlockItem(LASER_TRANSMITTER));
        items.put("laser_receiver", createBlockItem(LASER_RECEIVER));
        items.put("plinth_of_peeking", createBlockItem(PLINTH_OF_PEEKING));
        items.put("omniscient_obsidian", createBlockItem(OMNISCIENT_OBSIDIAN));
        register(Registry.ITEM, items);

        // Status Effects
        register(Registry.STATUS_EFFECT, Map.of(
                "lock_fatigue", LOCK_FATIGUE
        ));

        // Recipes
        register(Registry.RECIPE_SERIALIZER, Map.of(
                "crafting_special_lens", LENS_RECIPE_SERIALIZER
        ));

        // Enchantments
        register(Registry.ENCHANTMENT, Map.of(
                "bloodshed_curse", new BloodshedCurseEnchantment()
        ));

        // Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LockCommands.register(dispatcher);
        });
    }

    private static Item createBlankItem() {
        return new Item(new FabricItemSettings().group(Territorial.BASE_GROUP));
    }

    private static BlockItem createBlockItem(final Block block) { return new BlockItem(block, new FabricItemSettings().group(Territorial.BASE_GROUP)); }

    private static <V,T extends V> void register(Registry<V> registryType, Map<String, T> entries) {
        for(String key : entries.keySet()) {
            Registry.register(registryType, new Identifier(Territorial.MOD_ID, key), entries.get(key));
        }
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String id, FabricBlockEntityTypeBuilder<T> builder) {
        BlockEntityType<T> blockEntityType = builder.build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Territorial.MOD_ID, id), blockEntityType);
        return blockEntityType;
    }
}
