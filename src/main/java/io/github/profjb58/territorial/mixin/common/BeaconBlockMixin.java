package io.github.profjb58.territorial.mixin.common;

import io.github.profjb58.territorial.block.entity.BaseBeaconBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.misc.access.BlockAccess;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BeaconBlock.class)
public abstract class BeaconBlockMixin extends BlockWithEntity implements BlockAccess {

    private static final BooleanProperty territorial$HARMFUL = BooleanProperty.of("harmful");

    protected BeaconBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void BeaconBlock(Settings settings, CallbackInfo ci) {
        if(((Object) this).getClass().equals(BeaconBlock.class))
            setDefaultState(getStateManager().getDefaultState().with(territorial$HARMFUL, true));
    }

    @Inject(method = "createBlockEntity", at = @At("RETURN"), cancellable = true)
    public void createBlockEntity(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {
        cir.setReturnValue(new BaseBeaconBlockEntity(pos, state));
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if(world.isClient)
            cir.setReturnValue(ActionResult.SUCCESS);
        else {
            var blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof BaseBeaconBlockEntity baseBeaconBlockEntity) {
                player.openHandledScreen(baseBeaconBlockEntity);
                player.incrementStat(Stats.INTERACT_WITH_BEACON);
            }
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }

    @Inject(method = "getTicker", at = @At("RETURN"), cancellable = true)
    public void getTicker(World world, BlockState state, BlockEntityType<?> type, CallbackInfoReturnable<BlockEntityTicker<?>> cir) {
        cir.setReturnValue(checkType(type, TerritorialRegistry.BASE_BEACON_BLOCK_ENTITY_TYPE, BaseBeaconBlockEntity::tick));
    }

    @Inject(method = "onPlaced", at = @At("HEAD"), cancellable = true)
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (itemStack.hasCustomName()) {
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BaseBeaconBlockEntity baseBeaconBlockEntity)
                baseBeaconBlockEntity.setCustomName(itemStack.getName());
        }
        ci.cancel();
    }

    @Unique
    @Override
    public List<Property<?>> territorial$getAdditionalProperties() {
        return List.of(territorial$HARMFUL);
    }
}
