package io.github.profjb58.territorial.mixin.common;

import io.github.profjb58.territorial.misc.access.StatusEffectInstanceAccess;
import io.github.profjb58.territorial.util.NbtUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements StatusEffectInstanceAccess {

    private static BlockPos territorial$lastPosApplied;

    public void territorial$setLastPosApplied(BlockPos pos) { territorial$lastPosApplied = pos;}
    public BlockPos territorial$getLastPosApplied() { return territorial$lastPosApplied; }

    @Inject(at = @At("HEAD"), method = "writeNbt")
    public void writeNbt(NbtCompound tag, CallbackInfoReturnable<NbtCompound> cir) {
        if(territorial$lastPosApplied != null) {
            tag.putIntArray("last_pos_applied", NbtUtils.serializeBlockPos(territorial$lastPosApplied));
        }
    }

    @Inject(at = @At("HEAD"), method = "fromNbt(Lnet/minecraft/entity/effect/StatusEffect;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/effect/StatusEffectInstance;")
    private static void fromNbt(StatusEffect statusEffect, NbtCompound tag, CallbackInfoReturnable<StatusEffectInstance> ci) {
        if (tag.contains("last_pos_applied")) {
            territorial$lastPosApplied = NbtUtils.deserializeBlockPos(tag.getIntArray("last_pos_applied"));
        }
    }
}
