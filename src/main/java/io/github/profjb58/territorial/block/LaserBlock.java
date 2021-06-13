package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.block.entity.LaserBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class LaserBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final IntProperty POWER;
    public static final BooleanProperty POWERED;
    public static final DirectionProperty FACING;

    public LaserBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque());
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(POWERED, false)
                .with(POWER, 0)
                .with(Properties.FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(POWERED);
        stateManager.add(FACING);
        stateManager.add(POWER);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection().getOpposite();
        return this.getDefaultState().with(FACING, direction);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        boolean powered = world.isReceivingRedstonePower(pos);
        if (powered != state.get(POWERED)) {
            int power = world.getReceivedRedstonePower(pos);
            world.setBlockState(pos, state.with(POWERED, powered).with(POWER, power), 3);
        }
    }

    static {
        POWERED = Properties.POWERED;
        FACING = Properties.FACING;
        POWER = Properties.POWER;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, TerritorialRegistry.LASER_BLOCK_ENTITY, world.isClient ? LaserBlockEntity::clientTick : null);
    }

}
