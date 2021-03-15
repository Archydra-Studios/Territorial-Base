package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.effect.LockFatigueInstance;
import io.github.profjb58.territorial.event.TerritorialRegistry;
import io.github.profjb58.territorial.util.LockUtils;
import io.github.profjb58.territorial.world.WorldLockStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class LockableBlock {

    private final String lockId;
    private final UUID lockOwner;
    private final LockType lockType;
    private final BlockPos blockPos;
    private float blastResistance, fatigueMultiplier;

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

    public LockableBlock(String lockId, UUID lockOwner, LockType lockType, BlockPos blockPos) {
        this.lockId = lockId;
        this.lockOwner = lockOwner;
        this.lockType = lockType;
        this.blockPos = blockPos;

        if(lockType != null) {
            this.blastResistance = getBlastResistance(lockType);
            this.fatigueMultiplier = LockUtils.Calculations.getLockFatigueMultiplier(getLockFatigueAmplifier());
        }
    }

    public boolean exists() {
        return lockOwner != null && lockType != null && blockPos != null;
    }

    public boolean createEntity(World world) {
        if(!world.isClient) {
            BlockEntity be = world.getBlockEntity(blockPos);
            if(be != null) {
                CompoundTag tag = be.toTag(new CompoundTag());
                if(!tag.contains("lock_id")) { // No lock has been assigned to the block entity
                    tag.putString("lock_id", lockId);
                    tag.putUuid("lock_owner_uuid", lockOwner);
                    tag.putInt("lock_type", getLockTypeInt());

                    // Store locks position in persitent storage
                    WorldLockStorage lps = WorldLockStorage.get((ServerWorld) world);
                    lps.addLock(this);
                    try {
                        be.fromTag(be.getCachedState(), tag);
                    } catch (Exception ignored) {}
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasMatchingKey(ServerPlayerEntity player) {
        ItemStack itemStack = player.getStackInHand(player.getActiveHand());
        String itemStackName = itemStack.getName().getString();

        if(player.isHolding(TerritorialRegistry.KEY) && itemStackName.equals(lockId)) {
            return true;
        }
        else {
            // Cycle through the players items to check if they contain a matching key
            for(ItemStack invItemStack : player.inventory.main) {
                Item invItem = invItemStack.getItem();
                String invItemStackName = invItemStack.getName().getString();
                if(invItem == TerritorialRegistry.KEY && invItemStackName.equals(lockId)) {
                    return true;
                }
            }
        }
        return false;
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

    public LockFatigueInstance getLockFatigueInstance() {
        return new LockFatigueInstance(
                TerritorialRegistry.LOCK_FATIGUE, Integer.MAX_VALUE,
                getLockFatigueAmplifier(),
                false, false);
    }

    public ItemStack getLockItemStack(int amount) {
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
        padlock.setCustomName(new LiteralText(lockId));
        return padlock;
    }

    public int getLockFatigueAmplifier() {
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

    public int getLockTypeInt() {
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

    private float getBlastResistance(LockType lockType) {
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

    public UUID getLockOwner() { return lockOwner; }
    public String getLockId() { return lockId; }
    public LockType getLockType() { return lockType; }
    public BlockPos getBlockPos() { return blockPos; }
    public float getBlastResistance() { return blastResistance; }
    public float getFatigueMultiplier() { return fatigueMultiplier; }
}
