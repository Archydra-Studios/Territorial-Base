package io.github.profjb58.territorial.world.team;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;

public class Team {

    private final UUID id;
    private String name;
    private Banner banner;
    protected Date lastLoginDate;
    protected int numChunkClaims;
    protected boolean isInactive;

    private Members members;
    private Map<RegistryKey<World>, HashSet<BlockPos>> beaconPositions;

    protected Team(UUID id, String name, Banner banner, Date lastLoginDate, int numChunkClaims, boolean isInactive, Members members, @Nullable Map<RegistryKey<World>, HashSet<BlockPos>> beaconPositions) {
        this.id = id;
        this.name = name;
        this.banner = banner;
        this.lastLoginDate = lastLoginDate;
        this.numChunkClaims = numChunkClaims;
        this.isInactive = isInactive;
        this.members = members;
        this.beaconPositions = beaconPositions;
    }

    // New Team
    protected Team(String name, Banner banner, PlayerEntity owner) {
        id = UUID.randomUUID();
        this.name = name;
        this.banner = banner;
        this.members.add(Members.Role.OWNER, owner.getUuid());
    }

    protected void updateLastLogin() { lastLoginDate = Date.from(Instant.EPOCH); }

    public void addBeaconPos(World world, BlockPos pos, int tier) {
        var worldKey = world.getRegistryKey();
        HashSet<BlockPos> beaconPosSet = new HashSet<>();

        if(beaconPositions.containsKey(worldKey)) {
            beaconPosSet = beaconPositions.get(worldKey);
            beaconPosSet.add(pos);
        }
        else {
            beaconPosSet.add(pos);
            beaconPositions.put(worldKey, beaconPosSet);
        }
    }

    public void removeBeaconPos(World world, BlockPos pos) {
        var worldKey = world.getRegistryKey();

        if(beaconPositions.containsKey(worldKey)) {
            HashSet<BlockPos> beaconPosSet = beaconPositions.get(worldKey);
            if(beaconPosSet.size() == 1)
                beaconPositions.remove(worldKey);
            else
                beaconPosSet.remove(pos);
        }
    }

    public UUID getId() { return id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public void assignBanner(Banner banner) { this.banner = banner; }
    public Banner getBanner() { return banner; }
    public Members members() { return members; }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (var worldKey : beaconPositions.keySet()) {
            builder.append("[").append(worldKey.getValue().toString()).append("]= ");
            for(var beaconPos : beaconPositions.get(worldKey)) {
                builder.append(beaconPos.toShortString()).append("\n");
            }
        }
        return "AbstractTeam{" +
                "\n\t name= " + name +
                "\n\t bannerPatterns= " + Arrays.toString(banner.patterns.toArray()) +
                "\n\t beaconPositions="  + builder +
                "}";
    }

    public record Members(EnumMap<Role, HashSet<UUID>> members) {
        public enum Role { OWNER, DEFAULT }

        public void add(Role role, UUID playerUuid) {
            var membersSet = members.get(role);
            membersSet.add(playerUuid);
        }

        public boolean remove(UUID playerUuid) {
            boolean memberFound = false;
            for (HashSet<UUID> membersSet : members.values())
                memberFound = membersSet.remove(playerUuid);
            return memberFound;
        }

        int size() {
            int size = 0;
            for (HashSet<UUID> membersSet : members.values())
                size += membersSet.size();
            return size;
        }

        @Nullable
        public Role getRole(UUID playerUuid) {
            for (Map.Entry<Role, HashSet<UUID>> membersSet : members.entrySet()) {
                Role key = membersSet.getKey();
                HashSet<UUID> value = membersSet.getValue();
                if (value.contains(playerUuid)) return key;
            }
            return null;
        }
    }
    public record Banner(List<Pair<BannerPattern, DyeColor>> patterns, DyeColor baseColour) {}
}
