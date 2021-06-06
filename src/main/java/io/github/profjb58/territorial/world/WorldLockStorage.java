package io.github.profjb58.territorial.world;

/**
public class WorldLockStorage extends PersistentState {

    HashMap<UUID, LinkedList<BlockPos>> locksUUIDMap = new HashMap<>();

    public WorldLockStorage() {
        super("territorial_world_locks");
    }

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

    public static WorldLockStorage get(ServerWorld world) {
        return world.getChunkManager().getPersistentStateManager().getOrCreate(WorldLockStorage::new, "territorial_world_locks");
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
 **/
