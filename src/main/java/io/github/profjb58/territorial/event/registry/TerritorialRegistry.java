package io.github.profjb58.territorial.event.registry;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.*;
import io.github.profjb58.territorial.block.LockableBlock.LockType;
import io.github.profjb58.territorial.block.entity.LaserTransmitterBlockEntity;
import io.github.profjb58.territorial.client.gui.KeyringScreenHandler;
import io.github.profjb58.territorial.command.LockCommands;
import io.github.profjb58.territorial.enchantment.BloodshedCurseEnchantment;
import io.github.profjb58.territorial.entity.effect.EclipseStatusEffect;
import io.github.profjb58.territorial.entity.effect.LockFatigueStatusEffect;
import io.github.profjb58.territorial.item.*;
import io.github.profjb58.territorial.recipe.LensRecipe;
import io.github.profjb58.territorial.recipe.ConditionalRecipes;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public static final Block ANTI_MAGMA = new MagmaBlock(FabricBlockSettings.copyOf(Blocks.MAGMA_BLOCK));
    public static final EclipseRoseBlock ECLIPSE_ROSE = new EclipseRoseBlock();

    // Block Entities
    public static final BlockEntityType<LaserTransmitterBlockEntity> LASER_BLOCK_ENTITY
            = registerBlockEntity("laser_be", FabricBlockEntityTypeBuilder.create(LaserTransmitterBlockEntity::new, LASER_TRANSMITTER));

    // Status Effects
    public static final LockFatigueStatusEffect LOCK_FATIGUE_EFFECT = new LockFatigueStatusEffect();
    public static final EclipseStatusEffect ECLIPSE_EFFECT = new EclipseStatusEffect();

    // Screen handlers
    public static final ScreenHandlerType<KeyringScreenHandler> KEYRING_SCREEN_HANDLER_TYPE
            = ScreenHandlerRegistry.registerExtended(new Identifier(Territorial.MOD_ID, "keyring"), KeyringScreenHandler::new);

    // Recipe serializers
    public static final SpecialRecipeSerializer<LensRecipe> LENS_RECIPE_SERIALIZER = new SpecialRecipeSerializer<LensRecipe>(LensRecipe::new);
    public static final SpecialRecipeSerializer<ConditionalRecipes.OmniscientObsidian> OMNISCIENT_OBSIDIAN_RECIPE_SERIALIZER
            = new SpecialRecipeSerializer<>(ConditionalRecipes.OmniscientObsidian::new);

    public static void registerAll() {
        var blocks = new LinkedHashMap<String, Block>();
        var items = new LinkedHashMap<String, Item>();
        var recipes = new LinkedHashMap<String, RecipeSerializer<?>>();

        // Blocks
        blocks.put("safe", SAFE_BLOCK);
        blocks.put("laser_transmitter", LASER_TRANSMITTER);
        blocks.put("laser_receiver", LASER_RECEIVER);
        blocks.put("plinth_of_peeking", PLINTH_OF_PEEKING);
        blocks.put("omniscient_obsidian", OMNISCIENT_OBSIDIAN);
        blocks.put("anti_magma", ANTI_MAGMA);
        blocks.put("eclipse_rose", ECLIPSE_ROSE);
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
        items.put("anti_magma", createBlockItem(ANTI_MAGMA));
        items.put("eclipse_rose", createBlockItem(ECLIPSE_ROSE));
        register(Registry.ITEM, items);

        // Status Effects
        register(Registry.STATUS_EFFECT, Map.of(
                "lock_fatigue", LOCK_FATIGUE_EFFECT,
                "eclipse", ECLIPSE_EFFECT
        ));

        // Recipes
        recipes.put("crafting_special_lens", LENS_RECIPE_SERIALIZER);
        if(Territorial.getConfig().omniscientObsidianRecipe())
            recipes.put("crafting_omniscient_obsidian", OMNISCIENT_OBSIDIAN_RECIPE_SERIALIZER);
        register(Registry.RECIPE_SERIALIZER, recipes);

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
        var blockEntityType = builder.build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Territorial.MOD_ID, id), blockEntityType);
        return blockEntityType;
    }
}
