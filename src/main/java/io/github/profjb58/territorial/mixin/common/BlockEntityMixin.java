package io.github.profjb58.territorial.mixin.common;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    private String territorial$lockId;
    private UUID territorial$ownerUuid;
    private int territorial$lockType;
    private String territorial$ownerName;

    @Inject(at = @At("HEAD"), method = "writeNbt")
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        if (territorial$lockId != null && territorial$ownerUuid != null && territorial$lockType != 0 && territorial$ownerName != null) {
            tag.putString("lock_id", territorial$lockId);
            tag.putUuid("lock_owner_uuid", territorial$ownerUuid);
            tag.putString("lock_owner_name", territorial$ownerName);
            tag.putInt("lock_type", territorial$lockType);
        }
    }

    @Inject(at = @At("TAIL"), method = "readNbt")
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        this.territorial$lockId = tag.contains("lock_id") ? tag.getString("lock_id") : null;
        this.territorial$ownerUuid = tag.contains("lock_owner_uuid") ? tag.getUuid("lock_owner_uuid") : null;
        this.territorial$ownerName = tag.contains("lock_owner_name") ? tag.getString("lock_owner_name") : null;
        this.territorial$lockType = tag.contains("lock_type") ? tag.getInt("lock_type") : 0;
    }
}
