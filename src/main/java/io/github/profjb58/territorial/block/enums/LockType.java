package io.github.profjb58.territorial.block.enums;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.item.Item;

public enum LockType {
    UNBREAKABLE("padlock_unbreakable", TerritorialRegistry.PADLOCK_UNBREAKABLE,  -1, Integer.MAX_VALUE, Float.POSITIVE_INFINITY, "§d"),
    NETHERITE("padlock_netherite", TerritorialRegistry.PADLOCK_NETHERITE,4, 3, 8, "§0"),
    DIAMOND("padlock_diamond", TerritorialRegistry.PADLOCK_DIAMOND,3, 2, 6, "§b"),
    GOLD("padlock_gold", TerritorialRegistry.PADLOCK_GOLD,2, 1, 3, "§6"), // TODO - Maybe change lockFatigueAmplifier
    IRON("padlock", TerritorialRegistry.PADLOCK,1, 1, 4, "§7");

    private final Item item;
    private final int typeInt, lockFatigueAmplifier;
    private final float blastResistance;
    private final String name, formatColour;

    LockType(String name, Item item, int typeInt, int lockFatigueAmplifier, float blastResistance, String formatColour) {
        this.name = name;
        this.item = item;
        this.typeInt = typeInt;
        this.lockFatigueAmplifier = lockFatigueAmplifier;
        this.blastResistance = blastResistance;
        this.formatColour = formatColour;
    }

    public String getName() { return name; }
    public Item getItem() { return item; }
    public int getTypeInt() { return typeInt; }
    public int getLockFatigueAmplifier() { return lockFatigueAmplifier; }
    public float getBlastResistance() { return blastResistance; }
    public String getFormatColour() { return formatColour; }

    public static LockType getTypeFromInt(int lockType) {
        return switch (lockType) {
            case -1 -> LockType.UNBREAKABLE;
            case 1 -> LockType.IRON;
            case 2 -> LockType.GOLD;
            case 3 -> LockType.DIAMOND;
            case 4 -> LockType.NETHERITE;
            default -> null;
        };
    }
}
