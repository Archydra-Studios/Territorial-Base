package io.github.profjb58.territorial.block.enums;

import javax.annotation.Nullable;

public enum LockType {
    UNBREAKABLE("padlock_unbreakable",  -1, Integer.MAX_VALUE, Float.POSITIVE_INFINITY, "§d"),
    NETHERITE("padlock_netherite",4, 3, 8, "§8"),
    DIAMOND("padlock_diamond", 3, 2, 6, "§b"),
    GOLD("padlock_gold", 2, 1, 3, "§6"),
    IRON("padlock", 1, 1, 4, "§7"),
    DEADBOLT("deadbolt", 0, 2, 6, "§7");

    private final int typeInt, lockFatigueAmplifier;
    private final float blastResistance;
    private final String registryName, formatColour;

    LockType(String registryName, int typeInt, int lockFatigueAmplifier, float blastResistance, @Nullable String formatColour) {
        this.registryName = registryName;
        this.typeInt = typeInt;
        this.lockFatigueAmplifier = lockFatigueAmplifier;
        this.blastResistance = blastResistance;
        this.formatColour = formatColour;
    }

    public String getRegistryName() { return registryName; }
    public int getTypeInt() { return typeInt; }
    public int getLockFatigueAmplifier() { return lockFatigueAmplifier; }
    public float getBlastResistance() { return blastResistance; }
    @Nullable
    public String getFormatColour() { return formatColour; }

    public static LockType getTypeFromInt(int lockType) {
        return switch (lockType) {
            case -1 -> LockType.UNBREAKABLE;
            case 0 -> LockType.DEADBOLT;
            case 1 -> LockType.IRON;
            case 2 -> LockType.GOLD;
            case 3 -> LockType.DIAMOND;
            case 4 -> LockType.NETHERITE;
            default -> null;
        };
    }
}
