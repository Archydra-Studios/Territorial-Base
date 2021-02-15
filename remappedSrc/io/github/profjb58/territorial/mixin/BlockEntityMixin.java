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

    private String lockId;
    private UUID ownerUuid;
    private int lockType;

    // Deserialize the BlockEntity
    @Inject(at = @At("TAIL"), method = "fromTag")
    public void fromTag(BlockState state, CompoundTag tag, CallbackInfo info) {
        this.lockId = tag.contains("lock_id") ? tag.getString("lock_id") : null;
        this.ownerUuid = tag.contains("lock_owner_uuid") ? tag.getUuid("lock_owner_uuid") : null;
        this.lockType = tag.contains("lock_type") ? tag.getInt("lock_type") : 0;
    }

    // Serialize the BlockEntity
    @Inject(at = @At("HEAD"), method = "toTag")
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if(lockId != null && ownerUuid != null && lockType != 0) {
            tag.putString("lock_id", lockId);
            tag.putUuid("lock_owner_uuid", ownerUuid);
            tag.putInt("lock_type", lockType);
        }
    }

    public String getName() {
        return lockId;
    }

    public UUID getOwner() {
        return ownerUuid;
    }

    public int getLockType() { return lockType; }
}
