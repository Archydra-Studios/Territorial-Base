package io.github.profjb58.territorial.block.entity;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class SafeBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public SafeBlockEntity() {
        super(TerritorialRegistry.SAFE_BLOCK_ENTITY);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {

    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return null;
    }
}
