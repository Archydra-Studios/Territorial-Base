package io.github.profjb58.territorial.mixin;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.LockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class BlockBreakSpeedMixin extends LivingEntity {

    protected BlockBreakSpeedMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("RETURN"), method = "getBlockBreakingSpeed", cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir)
    {
        // TODO - useful for claims, can check region around the player entity
        Float blockBreakSpeed = cir.getReturnValue();

        if (hasStatusEffect(TerritorialRegistry.LOCK_FATIGUE)) {
            StatusEffectInstance lockFatigue = getStatusEffect(TerritorialRegistry.LOCK_FATIGUE);

            if(lockFatigue != null) {
                float multiplier = LockUtils.Calculations.getLockFatigueMultiplier(lockFatigue.getAmplifier());
                blockBreakSpeed *= multiplier;
            }
        }
        cir.setReturnValue(blockBreakSpeed);
    }
}
