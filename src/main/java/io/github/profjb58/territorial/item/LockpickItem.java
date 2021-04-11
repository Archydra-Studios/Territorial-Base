package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class LockpickItem extends Item {

    // TODO - Lockpick mechanics
    public LockpickItem() {
        super(new FabricItemSettings()
                .group(Territorial.BASE_GROUP)
                .maxCount(1));
    }
}
