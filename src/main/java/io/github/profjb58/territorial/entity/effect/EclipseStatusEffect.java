package io.github.profjb58.territorial.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class EclipseStatusEffect extends StatusEffect {
    public EclipseStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x010316);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return super.canApplyUpdateEffect(duration, amplifier);
    }
}
