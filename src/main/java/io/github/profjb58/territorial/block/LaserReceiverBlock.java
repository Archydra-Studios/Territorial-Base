package io.github.profjb58.territorial.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.Material;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class LaserReceiverBlock extends FacingBlock {

    public static final BooleanProperty POWERED;
    public static final IntProperty POWER;
    public static final DirectionProperty FACING;

    public LaserReceiverBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
        stateManager.add(POWERED);
        stateManager.add(POWER);
    }

    static {
        FACING = Properties.FACING;
        POWERED = Properties.POWERED;
        POWER = Properties.POWER;
    }
}
