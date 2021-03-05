package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.access.StatusEffectInstanceAccess;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import io.github.profjb58.territorial.event.TerritorialRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class LockUtils {

    private static final float TICKS_PER_SECOND = 60;

    public enum LockType {
        CREATIVE,
        IRON,
        GOLD,
        DIAMOND,
        NETHERITE
    }

    public enum LockSound {
        DENIED_ENTRY,
        LOCK_ADDED,
        LOCK_DESTROYED
    }

    public static int getLockTypeInt(LockType lockType) {
        switch(lockType) {
            case CREATIVE:
                return -1;
            case IRON:
                return 1;
            case GOLD:
                return 2;
            case DIAMOND:
                return 3;
            case NETHERITE:
                return 4;
            default:
                return 0;
        }
    }

    public static LockType getLockType(int lockType) {
        switch(lockType) {
            case -1:
                return LockType.CREATIVE;
            case 1:
                return LockType.IRON;
            case 2:
                return LockType.GOLD;
            case 3:
                return LockType.DIAMOND;
            case 4:
                return LockType.NETHERITE;
            default:
                return null;
        }
    }

    public static ItemStack getItemStackFromLock(LockType lockType, String enchantName, int amount) {
        ItemStack padlock;
        switch(lockType) {
            case CREATIVE:
                padlock = new ItemStack(TerritorialRegistry.PADLOCK_CREATIVE, amount);
                break;
            case IRON:
                padlock = new ItemStack(TerritorialRegistry.PADLOCK, amount);
                break;
            case GOLD:
                padlock = new ItemStack(TerritorialRegistry.PADLOCK_GOLD, amount);
                break;
            case DIAMOND:
                padlock = new ItemStack(TerritorialRegistry.PADLOCK_DIAMOND, amount);
                break;
            case NETHERITE:
                padlock = new ItemStack(TerritorialRegistry.PADLOCK_NETHERITE, amount);
                break;
            default:
                padlock = null;
        }
        padlock.setCustomName(new LiteralText(enchantName));
        return padlock;
    }

    public static float getBlastResistance(LockType lockType) {
        switch(lockType) {
            case CREATIVE:
                return Float.POSITIVE_INFINITY; // Impossible to break
            case NETHERITE:
                return 8; // Wither
            case DIAMOND:
                return 6; // Charged creeper / end crystal
            case GOLD:
                return 3; // Creeper
            case IRON:
                return 4; // Tnt
            default:
                return 0;
        }
    }

    public static int getLockFatigueAmplifier(LockType lockType) {
        switch(lockType) {
            case CREATIVE:
                return 4;
            case NETHERITE:
                return 3;
            case DIAMOND:
                return 2;
            case IRON:
                return 1;
            default:
                return 0;
        }
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
            if(!lb.getLockOwner().equals(player.getUuid())) {
                StatusEffectInstance lockFatigueInstance = new StatusEffectInstance(
                        TerritorialRegistry.LOCK_FATIGUE, Integer.MAX_VALUE,
                        LockUtils.getLockFatigueAmplifier(lb.getLockType()),
                        false, false, false);

                // Notify the lock fatigue effect with the last position the effect was applied from
                ((StatusEffectInstanceAccess) lockFatigueInstance).setLastPosApplied(target);
                player.addStatusEffect(lockFatigueInstance);
                return true;
            }
        }
        return false;
    }

    public static void playSound(LockSound sound, BlockPos pos, World world)
    {
        SoundEvent soundEvent;
        float volume = 0.5f;
        float pitch = 0.5f;

        if(!world.isClient) {
            if(sound == LockSound.DENIED_ENTRY) {
                volume = 0.4f;
                soundEvent = SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE;
            }
            else if(sound == LockSound.LOCK_ADDED){
                pitch = 0.65f;
                soundEvent = SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE;
            }
            else {
                soundEvent = SoundEvents.BLOCK_CHAIN_BREAK;
                pitch = 0.05f;
                volume = 0.05f;
            }
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, volume, pitch);
        }
    }

    public static class Calculations {

        public static float getLockFatigueMultiplier(float amplifier) {
        /*  Exponential decay function
            Image: https://imgur.com/a/a53Ta1O
            Demos graph: https://www.desmos.com/calculator/ngqiafekap */

            double breakMultiplier = Territorial.getConfig().breakMultiplier;
            double a = breakMultiplier / (Math.exp(1/1.5) - 1);
            double multiplier = a * (Math.exp(1/(amplifier + 0.5)) -1);
            return (float) multiplier;
        }

        public static int calcNumItemsToDrop(int itemStacksStored, int ticksSinceAttack) {
        /*  Max drop chance for a slot in a locked inventory is = 90%. Every second the player
            spends attacking the block the drop chance decreases by 1% until after 80 seconds it stays at 10%
            Randomization is then applied which will further reduce the drop chance */

            int numItemsToDrop;
            if(ticksSinceAttack > (80 * TICKS_PER_SECOND) || ticksSinceAttack == 0) { // 80 seconds elapsed
                numItemsToDrop = Math.round(0.1F * itemStacksStored);
            }
            else { // < 80 seconds, calculate based on time spent attacking
                float multiplier = 0.9F - ((ticksSinceAttack / TICKS_PER_SECOND) * 0.01F);
                if(multiplier > 0.1F) {
                    numItemsToDrop = Math.round(multiplier * itemStacksStored);
                }
                else { // Safe fail condition
                    numItemsToDrop = Math.round(0.1F * itemStacksStored);
                }
            }
            // Randomize the amount of stacks to retrieve, will always reduce the amount
            Random randomGen = new Random();

            // Generate a random multiplier between 0.5 and 1
            float randomMultiplier = (randomGen.nextFloat() / 2) + 0.5F;

            numItemsToDrop = Math.round(numItemsToDrop * randomMultiplier);
            return numItemsToDrop;
        }
    }
}
