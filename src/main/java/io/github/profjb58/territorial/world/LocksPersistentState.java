package io.github.profjb58.territorial.world;

import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import io.github.profjb58.territorial.util.TagUtils;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

public class LocksPersistentState extends PersistentState {

    HashMap<UUID, LinkedList<BlockPos>> locksUUIDMap = new HashMap<>();
    HashMap<Chunk, LinkedList<BlockPos>> locksChunkMap = new HashMap<>();

    public LocksPersistentState() {
        super("territorial_world_locks");
    }

    public void addLock(Chunk chunk, BlockPos lockPos) {

    }

    public void addLock(UUID uuid, BlockPos lockPos) {
        removeLock(uuid, lockPos); // Remove existing lock if one is already there

        LinkedList<BlockPos> playerLocks;
        if(locksUUIDMap.get(uuid) == null) {
            playerLocks = new LinkedList<>();
        }
        else {
            playerLocks = locksUUIDMap.get(uuid);
        }
        playerLocks.add(lockPos);

        locksUUIDMap.put(uuid, playerLocks);
        this.markDirty();
    }

    public void removeLock(UUID uuid, BlockPos lockPos) {
        if(locksUUIDMap.get(uuid) != null) {
            locksUUIDMap.get(uuid).remove(lockPos);
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
                    CompoundTag lockTag = be.toTag(new CompoundTag());
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

    public static LocksPersistentState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(LocksPersistentState::new, "territorial_world_locks");
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        ListTag worldLocksTags = compoundTag.getList("world_locked_tiles", NbtType.COMPOUND);

        for (Tag worldLocksTag : worldLocksTags) {
            CompoundTag playerLocksTag = (CompoundTag) worldLocksTag;
            UUID playerUuid = playerLocksTag.getUuid("uuid");

            LinkedList<BlockPos> lockedTilesPos = new LinkedList<>();
            ListTag lockedTilesPosTags = playerLocksTag.getList("locked_tiles", NbtType.COMPOUND);
            for (Tag lockedTilesPosTag : lockedTilesPosTags) {
                CompoundTag lockedTilePos = (CompoundTag) lockedTilesPosTag;
                lockedTilesPos.add(TagUtils.deserializeBlockPos(lockedTilePos.getIntArray("lock_pos")));
            }
            locksUUIDMap.put(playerUuid, lockedTilesPos);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        ListTag worldLocksTags = new ListTag();

        for (UUID playerUuid : locksUUIDMap.keySet()) { // Cycle through players in the current world
            CompoundTag playerLocksTag = new CompoundTag();
            playerLocksTag.putUuid("uuid", playerUuid);

            ListTag lockedTilesPosTags = new ListTag(); // Location of players locked tile-entities
            LinkedList<BlockPos> lockedTilesPos = locksUUIDMap.get(playerUuid);

            for(BlockPos lockedTile : lockedTilesPos) {
                CompoundTag lockedTileTag = new CompoundTag();
                lockedTileTag.putIntArray("lock_pos", TagUtils.serializeBlockPos(lockedTile));
                lockedTilesPosTags.add(lockedTileTag);
            }
            playerLocksTag.put("locked_tiles", lockedTilesPosTags);

            worldLocksTags.add(playerLocksTag);
        }
        compoundTag.put("world_locked_tiles", worldLocksTags);
        return compoundTag;
    }
}
