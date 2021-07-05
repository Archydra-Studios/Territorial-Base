package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.entity.effect.LockFatigueInstance;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.inventory.ItemInventory;
import io.github.profjb58.territorial.item.KeyringItem;
import io.github.profjb58.territorial.util.LockUtils;
import io.github.profjb58.territorial.world.WorldLockStorage;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class LockableBlock {

    private final String lockId;
    private final UUID lockOwnerUuid;
    private final String lockOwnerName;
    private final LockType lockType;
    private final BlockPos blockPos;
    private float blastResistance, fatigueMultiplier;

    public enum LockType {
        UNBREAKABLE,
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

    public enum LockEntityResult {
        NO_ENTITY_EXISTS,
        FAIL,
        SUCCESS
    }

    public class KeyResult {

    }

    public LockableBlock(String lockId, UUID lockOwnerUuid, String lockOwnerName, LockType lockType, BlockPos blockPos) {
        this.lockId = lockId;
        this.lockOwnerUuid = lockOwnerUuid;
        this.lockOwnerName = lockOwnerName;
        this.lockType = lockType;
        this.blockPos = blockPos;

        if(lockType != null) {
            this.blastResistance = getBlastResistance(lockType);
            this.fatigueMultiplier = LockUtils.Calculations.getLockFatigueMultiplier(getLockFatigueAmplifier());
        }
    }

    public boolean exists() {
        return lockOwnerUuid != null && lockType != null && blockPos != null;
    }

    public LockEntityResult createEntity(World world) {
        if(!world.isClient) {
            BlockEntity be = world.getBlockEntity(blockPos);
            if(be != null) {
                CompoundTag tag = be.toTag(new CompoundTag());
                if(!tag.contains("lock_id")) { // No lock has been assigned to the block entity
                    tag.putString("lock_id", lockId);
                    tag.putUuid("lock_owner_uuid", lockOwnerUuid);
                    tag.putString("lock_owner_name", lockOwnerName);
                    tag.putInt("lock_type", getLockTypeInt());

                    // Store locks position in persitent storage
                    WorldLockStorage lps = WorldLockStorage.get((ServerWorld) world);
                    lps.addLock(this);
                    try {
                        be.fromTag(be.getCachedState(), tag);
                    } catch (Exception ignored) {}

                    // Sync data to the client
                    ((BlockEntityClientSerializable) be).sync();
                    return LockEntityResult.SUCCESS;
                }
                else {
                    return LockEntityResult.FAIL;
                }
            }
        }
        return LockEntityResult.NO_ENTITY_EXISTS;
    }

    public Pair<ItemStack, Inventory> findMatchingKey(ServerPlayerEntity player, boolean checkInventory) {
        ItemStack itemStack = player.getStackInHand(player.getActiveHand());
        String itemStackName = itemStack.getName().getString();

        if(player.isHolding(TerritorialRegistry.MASTER_KEY) ||
                (player.isHolding(TerritorialRegistry.KEY) && itemStackName.equals(lockId))) {
            return new Pair<>(itemStack, player.inventory);
        }
        else if(checkInventory){
            // Cycle through the players items to check if they contain a matching key
            for(ItemStack invItemStack : player.inventory.main) {
                if(checkValidKey(invItemStack)) {
                    return new Pair<>(invItemStack, player.inventory);
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
        Item item = itemStack.getItem();
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

    public LockFatigueInstance getLockFatigueInstance() {
        return new LockFatigueInstance(
                TerritorialRegistry.LOCK_FATIGUE, Integer.MAX_VALUE,
                getLockFatigueAmplifier(),
                false, false);
    }

    public ItemStack getLockItemStack(int amount) {
        ItemStack padlock;
        switch(lockType) {
            case UNBREAKABLE:
                padlock = new ItemStack(TerritorialRegistry.PADLOCK_UNBREAKABLE, amount);
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
            case UNBREAKABLE: // Shouldn't be modified
                return Integer.MAX_VALUE;
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
            case UNBREAKABLE:
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
                return LockType.UNBREAKABLE;
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
            case UNBREAKABLE:
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

    public UUID getLockOwnerUuid() { return lockOwnerUuid; }
    public String getLockOwnerName() { return lockOwnerName; }
    public String getLockId() { return lockId; }
    public LockType getLockType() { return lockType; }
    public BlockPos getBlockPos() { return blockPos; }
    public float getBlastResistance() { return blastResistance; }
    public float getFatigueMultiplier() { return fatigueMultiplier; }
}
