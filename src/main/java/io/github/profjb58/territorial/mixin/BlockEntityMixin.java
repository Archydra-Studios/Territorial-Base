package io.github.profjb58.territorial.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {

    private String territorial_lockId;
    private UUID territorial_ownerUuid;
    private int territorial_lockType;

    @Inject(at = @At("HEAD"), method = "toTag")
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if(territorial_lockId != null && territorial_ownerUuid != null && territorial_lockType != 0) {
            tag.putString("lock_id", territorial_lockId);
            tag.putUuid("lock_owner_uuid", territorial_ownerUuid);
            tag.putInt("lock_type", territorial_lockType);
        }
    }

    @Inject(at = @At("TAIL"), method = "fromTag")
    public void fromTag(BlockState state, CompoundTag tag, CallbackInfo info) {
        this.territorial_lockId = tag.contains("lock_id") ? tag.getString("lock_id") : null;
        this.territorial_ownerUuid = tag.contains("lock_owner_uuid") ? tag.getUuid("lock_owner_uuid") : null;
        this.territorial_lockType = tag.contains("lock_type") ? tag.getInt("lock_type") : 0;
    }
}
