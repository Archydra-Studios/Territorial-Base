package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.inventory.ItemInventory;
import io.github.profjb58.territorial.item.KeyringItem;
import io.github.profjb58.territorial.util.MathUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

import static io.github.profjb58.territorial.block.LockableBlock.LockType.*;

public record LockableBlock(String lockId, UUID lockOwnerUuid, String lockOwnerName, LockType lockType, BlockPos blockPos) {

    public enum LockType { UNBREAKABLE, IRON, GOLD, DIAMOND, NETHERITE }
    public enum LockSound { DENIED_ENTRY, LOCK_ADDED, LOCK_DESTROYED }
    public enum LockEntityResult { NO_ENTITY_EXISTS, FAIL, SUCCESS }

    public boolean exists() {
        return lockOwnerUuid != null && lockType != null && blockPos != null;
    }

    public LockEntityResult createEntity(ServerWorld world) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if(be != null) {
            NbtCompound nbt = be.createNbt();
            if(!nbt.contains("lock_id")) { // No lock has been assigned to the block entity
                nbt.putString("lock_id", lockId);
                nbt.putUuid("lock_owner_uuid", lockOwnerUuid);
                nbt.putString("lock_owner_name", lockOwnerName);
                nbt.putInt("lock_type", lockTypeInt());

                // Store locks position in persitent storage
                //WorldLockStorage lps = WorldLockStorage.get((ServerWorld) world);
                //lps.addLock(this);
                try {
                    be.readNbt(nbt);
                } catch (Exception ignored) {}

                // Sync data to the client
                world.getChunkManager().markForUpdate(be.getPos());
                return LockEntityResult.SUCCESS;
            }
            else {
                return LockEntityResult.FAIL;
            }
        }
        return LockEntityResult.NO_ENTITY_EXISTS;
    }

    public Pair<ItemStack, Inventory> findMatchingKey(ServerPlayerEntity player, boolean checkInventory) {
        var itemStack = player.getStackInHand(player.getActiveHand());
        String itemStackName = itemStack.getName().getString();

        if(player.isHolding(TerritorialRegistry.MASTER_KEY) ||
                (player.isHolding(TerritorialRegistry.KEY) && itemStackName.equals(lockId))) {
            return new Pair<>(itemStack, player.getInventory());
        }
        else if(checkInventory) {
            // Cycle through the players items to check if they contain a matching key
            for(ItemStack invItemStack : player.getInventory().main) {
                if(checkValidKey(invItemStack)) {
                    return new Pair<>(invItemStack, player.getInventory());
                }
                else if(invItemStack.getItem() instanceof KeyringItem) {
                    ItemInventory itemInventory = new ItemInventory(invItemStack, 9);
                    itemInventory.loadFromAttachedItemTag();
                    for(ItemStack keyringInvStack : itemInventory.getItems()) {
                        if(checkValidKey(keyringInvStack)) return new Pair<>(keyringInvStack, itemInventory);
                    }
                }
            }
        }
        return new Pair<>(null, null);
    }

    private boolean checkValidKey(ItemStack itemStack) {
        var item = itemStack.getItem();
        String itemStackName = itemStack.getName().getString();
        return item == TerritorialRegistry.MASTER_KEY || (item == TerritorialRegistry.KEY && itemStackName.equals(lockId));
    }

    public void playSound(LockSound sound, World world)
    {
        SoundEvent soundEvent;
        float volume = 0.5f;
        float pitch = 0.5f;

        if(!world.isClient && exists()) {
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
            world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, volume, pitch);
        }
    }

    public StatusEffectInstance getLockFatigueInstance() {
        return new StatusEffectInstance(TerritorialRegistry.LOCK_FATIGUE_EFFECT, Integer.MAX_VALUE,
                lockFatigueAmplifier(), false, false);
    }

    public ItemStack getLockItemStack(int amount) {
        ItemStack padlock = switch (lockType) {
            case UNBREAKABLE -> new ItemStack(TerritorialRegistry.PADLOCK_UNBREAKABLE, amount);
            case IRON -> new ItemStack(TerritorialRegistry.PADLOCK, amount);
            case GOLD -> new ItemStack(TerritorialRegistry.PADLOCK_GOLD, amount);
            case DIAMOND -> new ItemStack(TerritorialRegistry.PADLOCK_DIAMOND, amount);
            case NETHERITE -> new ItemStack(TerritorialRegistry.PADLOCK_NETHERITE, amount);
        };
        padlock.setCustomName(new LiteralText(lockId));
        return padlock;
    }

    public int lockTypeInt() {
        return switch (lockType) {
            case UNBREAKABLE -> -1;
            case IRON -> 1;
            case GOLD -> 2;
            case DIAMOND -> 3;
            case NETHERITE -> 4;
        };
    }

    public static LockType lockType(int lockType) {
        return switch (lockType) {
            case -1 -> UNBREAKABLE;
            case 1 -> LockType.IRON;
            case 2 -> LockType.GOLD;
            case 3 -> DIAMOND;
            case 4 -> NETHERITE;
            default -> null;
        };
    }

    private float blastResistance(LockType lockType) {
        return switch (lockType) {
            case UNBREAKABLE -> Float.POSITIVE_INFINITY; // Impossible to break
            case NETHERITE -> 8; // Wither
            case DIAMOND -> 6; // Charged creeper / end crystal
            case GOLD -> 3; // Creeper
            case IRON -> 4; // Tnt
        };
    }

    public int lockFatigueAmplifier() {
        return switch (lockType) {
            case UNBREAKABLE -> Integer.MAX_VALUE; // Shouldn't be modified
            case NETHERITE -> 3;
            case DIAMOND -> 2;
            case IRON -> 1;
            default -> 0;
        };
    }

    public float blastResistance() { return (lockType != null) ? blastResistance(lockType) : 0; }
    public float fatigueMultiplier() { return (lockType != null) ? MathUtils.Locks.getLockFatigueMultiplier(lockFatigueAmplifier()) : 0; }
}
