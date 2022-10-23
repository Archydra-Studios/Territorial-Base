package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.block.entity.BoundaryBeaconBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BoundaryBeaconBlock extends BeaconBlock {

    public static final EnumProperty<DyeColor> DYE_COLOUR = EnumProperty.of("dye_colour", DyeColor.class);

    public BoundaryBeaconBlock() {
        super(FabricBlockSettings.copyOf(Blocks.BEACON));
        setDefaultState(getStateManager().getDefaultState().with(DYE_COLOUR, DyeColor.WHITE));

    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BoundaryBeaconBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DYE_COLOUR);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, TerritorialRegistry.BOUNDARY_BEACON_BLOCK_ENTITY_TYPE, BoundaryBeaconBlockEntity::tick);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            var blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof BoundaryBeaconBlockEntity boundaryBeaconBE) {

                // TODO - Replace this with a better "Teams" implementation
                player.setCurrentHand(hand);
                var itemStack = player.getStackInHand(hand);
                if(itemStack.getItem() instanceof BannerItem bannerItem) {
                    world.setBlockState(pos, state.with(DYE_COLOUR, bannerItem.getColor()));

                }

                player.openHandledScreen((BoundaryBeaconBlockEntity)blockEntity);
                player.incrementStat(Stats.INTERACT_WITH_BEACON);
            }
            return ActionResult.CONSUME;
        }
    }
}
