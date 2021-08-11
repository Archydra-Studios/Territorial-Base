package io.github.profjb58.territorial.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class PlinthOfPeekingBlock extends Block {

    public PlinthOfPeekingBlock() {
        super(FabricBlockSettings.copyOf(Blocks.ENCHANTING_TABLE));
    }
}
