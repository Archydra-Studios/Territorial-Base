package io.github.profjb58.territorial.api.lock;

import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public record LockType(Identifier id, Item item, int lockFatigueAmplifier, float blastResistance, DyeColor formatColour) {}
