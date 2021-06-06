package io.github.profjb58.territorial.mixin;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.UUID;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityClientSerializable{

    private String territorial_lockId;
    private UUID territorial_ownerUuid;
    private int territorial_lockType;
    private String territorial_ownerName;
    HashSet<BlockPos> positions = new HashSet<>();

    @Inject(at = @At("HEAD"), method = "writeNbt")
    public void writeNbt(NbtCompound tag, CallbackInfoReturnable<NbtCompound> cir) {
        toSharedTag(tag);
    }

    @Inject(at = @At("TAIL"), method = "readNbt")
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        fromSharedTag(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) { return toSharedTag(tag); }

    @Override
    public void fromClientTag(NbtCompound tag) {
        fromSharedTag(tag);
    }

    private void fromSharedTag(NbtCompound tag) {
        this.territorial_lockId = tag.contains("lock_id") ? tag.getString("lock_id") : null;
        this.territorial_ownerUuid = tag.contains("lock_owner_uuid") ? tag.getUuid("lock_owner_uuid") : null;
        this.territorial_ownerName = tag.contains("lock_owner_name") ? tag.getString("lock_owner_name") : null;
        this.territorial_lockType = tag.contains("lock_type") ? tag.getInt("lock_type") : 0;
    }

    private NbtCompound toSharedTag(NbtCompound tag) {
        if(territorial_lockId != null && territorial_ownerUuid != null && territorial_lockType != 0 && territorial_ownerName != null) {
            tag.putString("lock_id", territorial_lockId);
            tag.putUuid("lock_owner_uuid", territorial_ownerUuid);
            tag.putString("lock_owner_name", territorial_ownerName);
            tag.putInt("lock_type", territorial_lockType);
        }
        return tag;
    }
    
}
