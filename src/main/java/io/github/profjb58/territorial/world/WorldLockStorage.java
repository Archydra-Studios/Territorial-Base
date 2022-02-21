package io.github.profjb58.territorial.world;


import dev.architectury.utils.NbtType;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.util.TagUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class WorldLockStorage extends PersistentState {

    HashMap<UUID, LinkedList<BlockPos>> locksUUIDMap = new HashMap<>();

    public WorldLockStorage() {}

    public void addLock(LockableBlock lb) {
        removeLock(lb); // Remove existing lock if one is already there

        UUID lockOwner = lb.getLockOwnerUuid();
        BlockPos pos = lb.getBlockPos();

        LinkedList<BlockPos> playerLocks;
        if(locksUUIDMap.get(lockOwner) == null) {
            playerLocks = new LinkedList<>();
        }
        else {
            playerLocks = locksUUIDMap.get(lockOwner);
        }
        playerLocks.add(pos);

        locksUUIDMap.put(lockOwner, playerLocks);
        this.markDirty();
    }

    public void removeLock(LockableBlock lb) {
        UUID lockOwner = lb.getLockOwnerUuid();
        BlockPos pos = lb.getBlockPos();

        if(locksUUIDMap.get(lockOwner) != null) {
            locksUUIDMap.get(lockOwner).remove(pos);
        }

        if (locksUUIDMap.get(lb.getLockOwnerUuid()) != null) {
            locksUUIDMap.get(lockOwner).remove(pos);
        }
    }

    public void removeAllLocks(UUID uuid) {
        locksUUIDMap.remove(uuid);
    }

    public boolean listLocks(UUID uuidToSearch, PlayerEntity playerExecutedCmd) {
        if(locksUUIDMap.containsKey(uuidToSearch))
        {
            LinkedList<BlockPos> playerLocks = locksUUIDMap.get(uuidToSearch);
            playerExecutedCmd.sendMessage(new TranslatableText("message.territorial.list_locks_header", playerExecutedCmd.getDisplayName().getString()), false);
            int entityCount = 0; // Check if any valid entities are actually found from the list

            for(BlockPos lockPos : playerLocks) {
                BlockEntity be = playerExecutedCmd.getEntityWorld().getBlockEntity(lockPos);
                if(be != null) {
                    NbtCompound lockTag = be.createNbt();
                    if(lockTag.contains("id") && lockTag.contains("lock_id")) { // Lock exists here
                        Text lockNameText = new TranslatableText(be.getCachedState().getBlock().getTranslationKey());
                        String msg = "# " + lockTag.getString("lock_id") + " - §e[" + lockPos.getX() + ", " + lockPos.getY() + ", " + lockPos.getZ() + "] §r-§7 "
                                + lockNameText.getString();
                        playerExecutedCmd.sendMessage(new LiteralText(msg), false);
                        entityCount += 1;
                    }
                }
            }
            return entityCount != 0;
        }
        return false;
    }

    public static WorldLockStorage get(ServerWorld world) {
        return null;
        // return world.getChunkManager().getPersistentStateManager().getOrCreate(WorldLockStorage::new, "territorial_world_locks");
    }

    public void readNbt(NbtCompound NbtCompound) {
        NbtList worldLocksTags = NbtCompound.getList("world_locked_tiles", NbtType.COMPOUND);

        for (NbtElement worldLocksTag : worldLocksTags) {
            NbtCompound playerLocksTag = (NbtCompound) worldLocksTag;
            UUID playerUuid = playerLocksTag.getUuid("uuid");

            LinkedList<BlockPos> lockedTilesPos = new LinkedList<>();
            NbtList lockedTilesPosTags = playerLocksTag.getList("locked_tiles", NbtType.COMPOUND);
            for (NbtElement lockedTilesPosTag : lockedTilesPosTags) {
                NbtCompound lockedTilePos = (NbtCompound) lockedTilesPosTag;
                lockedTilesPos.add(TagUtils.deserializeBlockPos(lockedTilePos.getIntArray("lock_pos")));
            }
            locksUUIDMap.put(playerUuid, lockedTilesPos);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound NbtCompound) {
        NbtList worldLocksTags = new NbtList();

        for (UUID playerUuid : locksUUIDMap.keySet()) { // Cycle through players in the current world
            NbtCompound playerLocksTag = new NbtCompound();
            playerLocksTag.putUuid("uuid", playerUuid);

            NbtList lockedTilesPosTags = new NbtList(); // Location of players locked tile-entities
            LinkedList<BlockPos> lockedTilesPos = locksUUIDMap.get(playerUuid);

            for(BlockPos lockedTile : lockedTilesPos) {
                NbtCompound lockedTileTag = new NbtCompound();
                lockedTileTag.putIntArray("lock_pos", TagUtils.serializeBlockPos(lockedTile));
                lockedTilesPosTags.add(lockedTileTag);
            }
            playerLocksTag.put("locked_tiles", lockedTilesPosTags);

            worldLocksTags.add(playerLocksTag);
        }
        NbtCompound.put("world_locked_tiles", worldLocksTags);
        return NbtCompound;
    }
}
 
