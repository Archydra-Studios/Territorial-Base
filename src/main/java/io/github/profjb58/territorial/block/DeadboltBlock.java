package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.api.event.common.LockableBlockEvents;
import io.github.profjb58.territorial.block.entity.DeadboltBlockEntity;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.block.enums.LockSound;
import io.github.profjb58.territorial.block.enums.LockType;
import io.github.profjb58.territorial.world.ServerChunkStorage;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.locks.Lock;

import static io.github.profjb58.territorial.util.TextUtils.spacer;

public class DeadboltBlock extends WallMountedBlock implements BlockEntityProvider {

    public static final BooleanProperty LOCKED;
    protected static final VoxelShape NORTH_WALL_SHAPE;
    protected static final VoxelShape SOUTH_WALL_SHAPE;
    protected static final VoxelShape WEST_WALL_SHAPE;
    protected static final VoxelShape EAST_WALL_SHAPE;
    protected static final VoxelShape FLOOR_Z_AXIS_SHAPE;
    protected static final VoxelShape FLOOR_X_AXIS_SHAPE;
    protected static final VoxelShape CEILING_Z_AXIS_SHAPE;
    protected static final VoxelShape CEILING_X_AXIS_SHAPE;

    private static final List<Class<? extends Block>> deadboltLockableBlocks = List.of(
            DoorBlock.class, TrapdoorBlock.class, FenceGateBlock.class
    );

    public DeadboltBlock() {
        super(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK));
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FACE, WallMountLocation.WALL)
                .with(LOCKED, true));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction.Axis axis = state.get(FACING).getAxis();
        switch (state.get(FACE)) {
            case FLOOR:
                if (axis == Direction.Axis.X) return FLOOR_X_AXIS_SHAPE;
                else return FLOOR_Z_AXIS_SHAPE;
            case WALL:
                return switch (state.get(FACING)) {
                    case EAST -> EAST_WALL_SHAPE;
                    case WEST -> WEST_WALL_SHAPE;
                    case SOUTH -> SOUTH_WALL_SHAPE;
                    default -> NORTH_WALL_SHAPE;
                };
            default:
                if (axis == Direction.Axis.X) return CEILING_X_AXIS_SHAPE;
                else return CEILING_Z_AXIS_SHAPE;
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if(ctx.getStack().hasCustomName()) return super.getPlacementState(ctx);
        else {
            if(ctx.getPlayer() != null)
                ctx.getPlayer().sendMessage(new TranslatableText("message.territorial.lock_unnamed"), true);
            return null;
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(!world.isClient && placer instanceof ServerPlayerEntity player) {
            final int range = 1;

            // Check for blocks that can be locked by the deadbolt within a given range from the
            for(int x=-range; x <= range; x++) {
                for(int y=-range; y <= range; y++) {
                    for(int z=-range; z <= range; z++) {
                        var blockPos = pos.add(x, y, z);
                        var blockStateWithinRange = world.getBlockState(blockPos);

                        // Ignore loop if the block is air or is a full cube
                        if(!blockStateWithinRange.isAir() && !blockStateWithinRange.isFullCube(world, pos)) {
                            for(var blockClass : deadboltLockableBlocks) {
                                // Check if the current blocks class is the same as or a subclass of any blocks the deadbolt can lock
                                if(blockClass.isAssignableFrom(blockStateWithinRange.getBlock().getClass())) {
                                    linkLockableBlock((ServerWorld) world, pos, player, itemStack);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void linkLockableBlock(ServerWorld world, BlockPos pos, ServerPlayerEntity player, ItemStack itemStack) {
        var lb = new LockableBlock(
                itemStack.getName().getString(),
                player.getUuid(),
                player.getName().getString(),
                LockType.DEADBOLT,
                pos);

        switch(lb.createEntity(world, player)) {
            case SUCCESS -> {
                player.sendMessage(new TranslatableText("message.territorial.lock_successful"), true);
                lb.playSound(LockSound.LOCK_ADDED, world);
            }
            case FAIL -> {
                player.sendMessage(new TranslatableText("message.territorial.lock_failed"), true);
                lb.playSound(LockSound.DENIED_ENTRY, player.getEntityWorld());
            }
            case NO_ENTITY_EXISTS, BLACKLISTED ->
                    player.sendMessage(new TranslatableText("message.territorial.lock_not_lockable"), true);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DeadboltBlockEntity(pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, LOCKED);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        if(stack.hasCustomName()) {
            tooltip.add(new TranslatableText("tooltip.territorial.shift"));
            if(Screen.hasShiftDown()) {
                tooltip.add(spacer());
                tooltip.add(new TranslatableText("tooltip.territorial.padlock_shift"));
            }
        }
        else {
            tooltip.add(new TranslatableText("tooltip.territorial.padlock_unnamed"));
        }
        super.appendTooltip(stack, world, tooltip, options);
    }

    static {
        LOCKED = Properties.LOCKED;
        NORTH_WALL_SHAPE = Block.createCuboidShape(4.0, 3.0, 13.0, 12.0, 13.0, 16.0);
        SOUTH_WALL_SHAPE = Block.createCuboidShape(4.0, 3.0, 0.0, 12.0, 13.0, 3.0);
        WEST_WALL_SHAPE = Block.createCuboidShape(13.0, 3.0, 4.0, 16.0, 13.0, 12.0);
        EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 3.0, 4.0, 3.0, 13.0, 12.0);
        FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 3.0, 12.0, 3.0, 13.0);
        FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(3.0, 0.0, 4.0, 13.0, 3.0, 12.0);
        CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 13.0, 3.0, 12.0, 16.0, 13.0);
        CEILING_X_AXIS_SHAPE = Block.createCuboidShape(3.0, 13.0, 4.0, 13.0, 16.0, 12.0);
    }
}
