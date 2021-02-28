package io.github.profjb58.territorial.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.UUID;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityClientSerializable{

    private String territorial_lockId;
    private UUID territorial_ownerUuid;
    private int territorial_lockType;

    @Inject(at = @At("HEAD"), method = "toTag")
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        toSharedTag(tag);

        if(territorial_lockId != null) {
            tag.putString("lock_id", territorial_lockId);
        }
    }

    @Inject(at = @At("TAIL"), method = "fromTag")
    public void fromTag(BlockState state, CompoundTag tag, CallbackInfo info) {
        this.territorial_lockId = tag.contains("lock_id") ? tag.getString("lock_id") : null;
        fromSharedTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toSharedTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        fromSharedTag(tag);
    }

    private void fromSharedTag(CompoundTag tag) {
        this.territorial_ownerUuid = tag.contains("lock_owner_uuid") ? tag.getUuid("lock_owner_uuid") : null;
        this.territorial_lockType = tag.contains("lock_type") ? tag.getInt("lock_type") : 0;
    }

    private CompoundTag toSharedTag(CompoundTag tag) {
        if(territorial_ownerUuid != null && territorial_lockType != 0) {
            tag.putUuid("lock_owner_uuid", territorial_ownerUuid);
            tag.putInt("lock_type", territorial_lockType);
        }
        return tag;
    }

    @Environment(EnvType.CLIENT)

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((Boolean)state.get(LIT)) {
            Direction direction = ((Direction)state.get(FACING)).getOpposite();
            double d = 0.27D;
            double e = (double)pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)direction.getOffsetX();
            double f = (double)pos.getY() + 0.7D + (random.nextDouble() - 0.5D) * 0.2D + 0.22D;
            double g = (double)pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)direction.getOffsetZ();
            world.addParticle(this.particle, e, f, g, 0.0D, 0.0D, 0.0D);
        }
    }
}
