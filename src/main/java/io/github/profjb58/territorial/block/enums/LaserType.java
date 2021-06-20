package io.github.profjb58.territorial.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum LaserType implements StringIdentifiable {
    TRANSMITTER("transmitter"),
    RECEIVER("receiver");

    private final String name;

    LaserType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}
