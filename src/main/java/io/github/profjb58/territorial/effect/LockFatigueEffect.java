package io.github.profjb58.territorial.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class LockFatigueEffect extends StatusEffect {

    public LockFatigueEffect() {
        super(StatusEffectType.HARMFUL, 4866583);
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
}
