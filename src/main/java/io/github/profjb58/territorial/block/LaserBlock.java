package io.github.profjb58.territorial.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class LaserBlock extends FacingBlock {
    public static final BooleanProperty POWERED;

    public LaserBlock() {
        super(FabricBlockSettings.of(Material.METAL).nonOpaque().strength(4.0f));
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false).with(Properties.FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(POWERED);
        stateManager.add(Properties.FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection().getOpposite();
        return this.getDefaultState().with(Properties.FACING, direction);
    }

    static {
        POWERED = Properties.POWERED;
    }

}
