package io.github.profjb58.territorial.effect;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class LockFatigueInstance extends StatusEffectInstance {

    public LockFatigueInstance(LockFatigueEffect type, int duration, int amplifier, boolean ambient, boolean visible) {
        super(type, duration, amplifier, ambient, visible);
    }

    public static void removeEffect(PlayerEntity player) {
        if(player.hasStatusEffect(TerritorialRegistry.LOCK_FATIGUE)) {
            player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
        }
    }

    public static boolean addEffect(PlayerEntity player, BlockPos target) {
        if(player.isCreative()) return false;

        LockableBlockEntity lbe = new LockableBlockEntity(player.getEntityWorld(), target);
        if(lbe.exists()) {
            LockableBlock lb = lbe.getBlock();

            // Notify the lock fatigue effect with the last position the effect was applied from
            ((StatusEffectInstanceAccess) lb.getLockFatigueInstance()).setLastPosApplied(target);
            player.addStatusEffect(lb.getLockFatigueInstance());
            return true;
        }
        return false;
    }
}
