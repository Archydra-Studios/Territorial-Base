package io.github.profjb58.territorial.entity.effect;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class LockFatigueStatusEffect extends StatusEffect {

    public LockFatigueStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 4866583);
        addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "89C60584-6295-11EB-AE93-0242AC130002", -0.10000000149011612D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) { super.applyUpdateEffect(entity, amplifier); }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
    }

    public static boolean addEffect(PlayerEntity player, BlockPos target) {
        if(!player.isCreative()) {
            var lbe = new LockableBlockEntity(player.getEntityWorld(), target);
            if(lbe.exists()) {
                LockableBlock lb = lbe.getBlock();

                // Notify the lock fatigue effect with the last position the effect was applied from
                ((StatusEffectInstanceAccess) lb.getLockFatigueInstance()).setLastPosApplied(target);
                player.addStatusEffect(lb.getLockFatigueInstance());
                return true;
            }
        }
        return false;
    }
}
