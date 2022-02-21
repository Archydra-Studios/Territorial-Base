package io.github.profjb58.territorial.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.PassiveEntity;

public class BloodshedCurseEnchantment extends Enchantment {
    public BloodshedCurseEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) { return 25; }

    @Override
    public int getMaxPower(int level) { return 50; }

    @Override
    public int getMaxLevel() { return 1; }

    @Override
    public boolean isTreasure() { return true; }

    @Override
    public boolean isCursed() { return true; }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if(target instanceof PassiveEntity) {
            user.getMainHandStack().damage(10, user, (p) -> {
                p.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 120));
        }
        super.onTargetDamaged(user, target, level);
    }
}
