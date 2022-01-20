package io.github.profjb58.territorial.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    private String territorial_lockId;
    private UUID territorial_ownerUuid;
    private int territorial_lockType;
    private String territorial_ownerName;

    @Inject(at = @At("HEAD"), method = "writeNbt", cancellable = true)
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        if (territorial_lockId != null && territorial_ownerUuid != null && territorial_lockType != 0 && territorial_ownerName != null) {
            tag.putString("lock_id", territorial_lockId);
            tag.putUuid("lock_owner_uuid", territorial_ownerUuid);
            tag.putString("lock_owner_name", territorial_ownerName);
            tag.putInt("lock_type", territorial_lockType);
        }
    }

    @Inject(at = @At("TAIL"), method = "readNbt")
    public void readNbt(NbtCompound tag, CallbackInfo info) {
        this.territorial_lockId = tag.contains("lock_id") ? tag.getString("lock_id") : null;
        this.territorial_ownerUuid = tag.contains("lock_owner_uuid") ? tag.getUuid("lock_owner_uuid") : null;
        this.territorial_ownerName = tag.contains("lock_owner_name") ? tag.getString("lock_owner_name") : null;
        this.territorial_lockType = tag.contains("lock_type") ? tag.getInt("lock_type") : 0;
    }


}
