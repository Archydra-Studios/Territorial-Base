package io.github.profjb58.territorial.block.entity;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class DeadboltBlockEntity extends BlockEntity {

    public DeadboltBlockEntity(BlockPos pos, BlockState state) {
        super(TerritorialRegistry.BLANK_BLOCK_ENTITY_TYPE, pos, state);
    }
}
