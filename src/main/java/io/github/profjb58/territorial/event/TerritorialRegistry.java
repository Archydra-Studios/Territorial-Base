package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.item.PadlockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TerritorialRegistry {

    // Locks
    public static final Item KEY = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(16));
    public static final Item PADLOCK = new PadlockItem(1);
    public static final Item PADLOCK_DIAMOND = new PadlockItem(3);
    public static final Item PADLOCK_NETHERITE = new PadlockItem(4);
    public static final Item PADLOCK_CREATIVE = new PadlockItem(-1);
    public static final Item LOCKPICK = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(1));
    public static final Item LOCKPICK_NETHERITE = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(1));
    public static final Item LOCKPICK_CREATIVE = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(1));
    public static final Item ENDER_AMULET = new Item(new FabricItemSettings().group(Territorial.BASE_GROUP));

    public static void registerAll() { registerItems(); }

    private static void registerItems() {
        // Locks
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "key"), KEY);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock"), PADLOCK);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_diamond"), PADLOCK_DIAMOND);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_netherite"), PADLOCK_NETHERITE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "padlock_creative"), PADLOCK_CREATIVE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "lockpick"), LOCKPICK);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "lockpick_netherite"), LOCKPICK_NETHERITE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "lockpick_creative"), LOCKPICK_CREATIVE);
        Registry.register(Registry.ITEM, new Identifier(Territorial.MOD_ID, "ender_amulet"), ENDER_AMULET);
    }
}
