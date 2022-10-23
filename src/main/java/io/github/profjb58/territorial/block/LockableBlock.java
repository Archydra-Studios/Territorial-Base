package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.api.event.common.LockableBlockEvents;
import io.github.profjb58.territorial.block.enums.LockEntityResult;
import io.github.profjb58.territorial.block.enums.LockSound;
import io.github.profjb58.territorial.block.enums.LockType;
import io.github.profjb58.territorial.config.LockablesBlacklistHandler;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.inventory.ItemInventory;
import io.github.profjb58.territorial.item.KeyringItem;
import io.github.profjb58.territorial.util.NbtUtils;
import io.github.profjb58.territorial.world.ServerChunkStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record LockableBlock(String lockId, UUID lockOwnerUuid, String lockOwnerName, LockType lockType, BlockPos selfPos, BlockPos blockEntitySourcePos) {

    public LockableBlock(String lockId, UUID lockOwnerUuid, String lockOwnerName, LockType lockType, BlockPos selfPos) {
        this(lockId, lockOwnerUuid, lockOwnerName, lockType, selfPos, selfPos);
    }

    @Environment(EnvType.CLIENT)
    public LockableBlock(String lockId, UUID lockOwnerUuid, String lockOwnerName, LockType lockType) {
        this(lockId, lockOwnerUuid, lockOwnerName, lockType, null);
    }

    public boolean exists() {
        return lockOwnerUuid != null && lockType != null && selfPos != null && blockEntitySourcePos != null;
    }

    public LockEntityResult createEntity(ServerWorld world, ServerPlayerEntity player) {
        // Determines if the lockable block we are creating has a separate source block entity storing the lock data
        boolean hasSourceBlockEntity = blockEntitySourcePos() != null;
        // Decide if we should grab the block entity position from the same position or from a separate source block
        BlockEntity be = hasSourceBlockEntity ? world.getBlockEntity(blockEntitySourcePos) : world.getBlockEntity(selfPos);

        if(be != null) {
            // Grab the target locked block
            var lockedBlock = be.getCachedState().getBlock();
            if(hasSourceBlockEntity)
                lockedBlock = world.getBlockState(selfPos).getBlock();

            if(!LockablesBlacklistHandler.isBlacklisted(lockedBlock)) {
                NbtCompound nbt = be.createNbt();
                if(!nbt.contains("lock_id")) { // No lock has been assigned to the block entity
                    nbt.putString("lock_id", lockId);
                    nbt.putUuid("lock_owner_uuid", lockOwnerUuid);
                    nbt.putString("lock_owner_name", lockOwnerName);
                    nbt.putInt("lock_type", lockType.getTypeInt());

                    // Additional data for locks with a source block entity
                    if(hasSourceBlockEntity)
                        nbt.putIntArray("lock_pos", NbtUtils.serializeBlockPos(selfPos));

                    // Store locks position in persistent storage
                    ServerChunkStorage.get(world, world.getChunk(selfPos).getPos()).addLockedBlock(this);
                    try {
                        be.readNbt(nbt);
                    } catch (Exception ignored) {}

                    // Sync data to the client
                    world.getChunkManager().markForUpdate(be.getPos());

                    // Fire event
                    LockableBlockEvents.CREATE.invoker().create(this, player);
                    return LockEntityResult.SUCCESS;
                }
                else {
                    // Fire event
                    LockableBlockEvents.INTERACT.invoker().interact(this, player, LockableBlockEvents.InteractionType.FAILED);
                    return LockEntityResult.FAIL;
                }
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
            world.playSound(null, selfPos, sound.getSoundEvent(), SoundCategory.BLOCKS,
                    sound.getVolume(), sound.getPitch());
    }

    public StatusEffectInstance getLockFatigueInstance() {
        return new StatusEffectInstance(TerritorialRegistry.LOCK_FATIGUE_EFFECT, Integer.MAX_VALUE,
                lockType.getLockFatigueAmplifier(), false, false);
    }

    public ItemStack getLockItemStack() {
        var padlockStack = Registry.ITEM.get(new Identifier(Territorial.MOD_ID, lockType.getRegistryName())).getDefaultStack();
        padlockStack.setCustomName(new LiteralText(lockId));
        return padlockStack;
    }

    //public float blastResistance() { return (lockType != null) ? blastResistance(lockType) : 0; }
    //public float fatigueMultiplier() { return (lockType != null) ? MathUtils.Locks.getLockFatigueMultiplier(lockFatigueAmplifier()) : 0; }
}
