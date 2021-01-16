package io.github.profjb58.territorial.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {

    private UUID lockUuid, ownerUuid;

    // Deserialize the BlockEntity
    @Inject(at = @At("TAIL"), method = "fromTag")
    public void fromTag(BlockState state, CompoundTag tag, CallbackInfo info) {

        if(tag.contains("lock_uuid")) {
            this.lockUuid = tag.getUuid("lock_uuid");
        }
        if(tag.contains("lock_owner_uuid")) {
            this.ownerUuid = tag.getUuid("lock_owner_uuid");
        }
    }

    // Serialize the BlockEntity
    @Inject(at = @At("HEAD"), method = "toTag")
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {

        if(lockUuid != null && ownerUuid != null) {
            tag.putUuid("lock_uuid", lockUuid);
            tag.putUuid("lock_owner_uuid", ownerUuid);
        }
    }

    public UUID getLock() {
        return lockUuid;
    }

    public UUID getOwner() {
        return ownerUuid;
    }
}
