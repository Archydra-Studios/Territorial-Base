package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.block.entity.LaserTransmitterBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.TextUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LaserTransmitterBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final BooleanProperty POWERED;
    public static final IntProperty POWER;
    public static final DirectionProperty FACING;

    public LaserTransmitterBlock() {
        super(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).nonOpaque().requiresTool()
                .strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)); //.breakByTool(FabricToolTags.PICKAXES, 0));
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(POWERED, false)
                .with(POWER, 0)
                .with(Properties.FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
        stateManager.add(POWERED);
        stateManager.add(POWER);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        boolean powered = world.isReceivingRedstonePower(pos);
        world.setBlockState(pos, state.with(POWER, world.getReceivedRedstonePower(pos))
                .with(POWERED, powered), 3);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        world.setBlockState(pos, state.with(POWER, world.getReceivedRedstonePower(pos))
                .with(POWERED, world.isReceivingRedstonePower(pos)), 3);
        BlockEntity be = world.getBlockEntity(pos);
        if (!world.isClient && be instanceof LaserTransmitterBlockEntity lbe) {
            lbe.createFromLens(stack);
        }
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack toolStack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStack(world, pos, getDroppedStacks(state, (ServerWorld) world, pos, be).get(0));
        onStacksDropped(state, (ServerWorld) world, pos, toolStack);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        ItemStack stackToDrop = asItem().getDefaultStack();
        return List.of(((LaserTransmitterBlockEntity) builder.get(LootContextParameters.BLOCK_ENTITY)).writeNbtStack(stackToDrop));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            var be = world.getBlockEntity(pos);
            if(be instanceof LaserTransmitterBlockEntity ltbe) {
                ltbe.updateLightBlocks(false, state.get(Properties.FACING));
                ltbe.setReceiverPowered(false);
            }
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) { return true; }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserTransmitterBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, TerritorialRegistry.LASER_BLOCK_ENTITY, LaserTransmitterBlockEntity::tick);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        NbtCompound tag = stack.getSubNbt("beam");
        if(tag != null) {
            // Colour
            DyeColor dyeColour = Optional.of(DyeColor.byId(tag.getInt("colour"))).orElse(DyeColor.WHITE);

            // TODO - Crash when hovering over tooltip in Beacon GUI. Class not found exception :/
            String dyeNameCapitalized = dyeColour.getName().substring(0, 1).toUpperCase() + dyeColour.getName().substring(1);
            tooltip.add(new LiteralText(TextUtils.getTextColourFormatting(dyeColour) + dyeNameCapitalized));

            // Modifiers
            int strengthMod = tag.getByte("strength");
            tooltip.add(new TranslatableText("tooltip.territorial.lens_strength_" + strengthMod));
            if(tag.getBoolean("light")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.light"));
            if(tag.getBoolean("highlight")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.highlight"));
            if(tag.getBoolean("death")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.death"));
            if(tag.getBoolean("rainbow")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.rainbow"));
            if(tag.getBoolean("sparkle")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.sparkle"));
        }
    }

    static {
        FACING = Properties.FACING;
        POWER = Properties.POWER;
        POWERED = Properties.POWERED;
    }
}
