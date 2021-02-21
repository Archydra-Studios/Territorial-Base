package io.github.profjb58.territorial.mixin;

import io.github.profjb58.territorial.access.StatusEffectInstanceAccess;
import io.github.profjb58.territorial.util.TagUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements StatusEffectInstanceAccess {

    private static BlockPos territorial_lastPosApplied;

    public void setLastPosApplied(BlockPos pos) { territorial_lastPosApplied = pos;}
    public BlockPos getLastPosApplied() { return territorial_lastPosApplied; }


    @Inject(at = @At("HEAD"), method = "toTag")
    public void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if(territorial_lastPosApplied != null) {
            tag.putIntArray("last_pos_applied", TagUtils.serializeBlockPos(territorial_lastPosApplied));
        }
    }

    @Inject(at = @At("HEAD"), method = "fromTag(Lnet/minecraft/entity/effect/StatusEffect;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/entity/effect/StatusEffectInstance;")
    private static void fromTag(StatusEffect statusEffect, CompoundTag tag, CallbackInfoReturnable<StatusEffectInstance> ci) {
        if (tag.contains("last_pos_applied")) {
            territorial_lastPosApplied = TagUtils.deserializeBlockPos(tag.getIntArray("last_pos_applied"));
        }
    }
}
