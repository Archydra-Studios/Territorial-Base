package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.enums.LockEntityResult;
import io.github.profjb58.territorial.block.enums.LockSound;
import io.github.profjb58.territorial.block.enums.LockType;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.inventory.ItemInventory;
import io.github.profjb58.territorial.item.KeyringItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public record LockableBlock(String lockId, UUID lockOwnerUuid, String lockOwnerName, LockType lockType, BlockPos blockPos) {

    @Environment(EnvType.CLIENT)
    public LockableBlock(String lockId, String lockOwnerName, LockType lockType) {
        this(lockId, null, lockOwnerName, lockType, null);
    }

    public boolean exists() {
        return lockOwnerUuid != null && lockType != null && blockPos != null;
    }

    public LockEntityResult createEntity(ServerWorld world) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if(be != null) {
            Block block = be.getCachedState().getBlock();
            if(!Territorial.LOCKABLES_BLACKLIST.isBlacklisted(block)) {
                NbtCompound nbt = be.createNbt();
                if(!nbt.contains("lock_id")) { // No lock has been assigned to the block entity
                    nbt.putString("lock_id", lockId);
                    nbt.putUuid("lock_owner_uuid", lockOwnerUuid);
                    nbt.putString("lock_owner_name", lockOwnerName);
                    nbt.putInt("lock_type", lockType.getTypeInt());

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
                else return LockEntityResult.FAIL;
            }
            else return LockEntityResult.BLACKLISTED;
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

    public void playSound(LockSound sound, World world) {
        if(!world.isClient && exists())
            world.playSound(null, blockPos, sound.getSoundEvent(), SoundCategory.BLOCKS,
                    sound.getVolume(), sound.getPitch());
    }

    public StatusEffectInstance getLockFatigueInstance() {
        return new StatusEffectInstance(TerritorialRegistry.LOCK_FATIGUE_EFFECT, Integer.MAX_VALUE,
                lockType.getLockFatigueAmplifier(), false, false);
    }

    public ItemStack getLockItemStack(int amount) {
        var padlockStack = new ItemStack(lockType.getItem(), amount);
        padlockStack.setCustomName(new LiteralText(lockId));
        return padlockStack;
    }

    //public float blastResistance() { return (lockType != null) ? blastResistance(lockType) : 0; }
    //public float fatigueMultiplier() { return (lockType != null) ? MathUtils.Locks.getLockFatigueMultiplier(lockFatigueAmplifier()) : 0; }
}
