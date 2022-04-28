package io.github.profjb58.territorial.misc.access;

import net.minecraft.state.property.Property;

import java.util.List;

public interface BlockAccess {
    List<Property<?>> territorial$getAdditionalProperties();
}
