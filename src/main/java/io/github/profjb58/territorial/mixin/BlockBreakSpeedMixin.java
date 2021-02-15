package io.github.profjb58.territorial.mixin;

import io.github.profjb58.territorial.event.TerritorialRegistry;
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
            float multiplier;

            StatusEffectInstance lockFatigue = getStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
            if(lockFatigue != null) {
                switch(lockFatigue.getAmplifier()) {
                    case 0:
                        multiplier = 0.3F;
                        break;
                    case 1:
                        multiplier = 0.09F;
                        break;
                    case 2:
                        multiplier = 0.0027F;
                        break;
                    case 3:
                    default:
                        multiplier = 8.1E-4F;
                }
                blockBreakSpeed *= multiplier;
            }
        }
        cir.setReturnValue(blockBreakSpeed);
    }
}
