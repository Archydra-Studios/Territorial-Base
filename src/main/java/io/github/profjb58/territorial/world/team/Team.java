package io.github.profjb58.territorial.world.team;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

public class Team implements Serializable {

    private final UUID id;
    private String name;
    private Banner banner;
    private final Members members;

    protected Team(UUID id, String name, Banner banner, Members members) {
        this.id = id;
        this.name = name;
        this.banner = banner;
        this.members = members;
    }

    protected Team(String name, Banner banner, ServerPlayerEntity owner) {
        id = UUID.randomUUID();
        this.name = name;
        this.banner = banner;
        this.members = new Members(Map.of(Members.Role.OWNER, Set.of(owner.getUuid())));
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public Team.Banner getBanner() { return banner; }
    public Team.Members members() { return members; }

    public void setIdentifyingData(String name, Banner banner) {
        this.name = name;
        this.banner = banner;
    }

    public record Banner(ItemStack stack, DyeColor baseColour) {}

    public record Members(Map<Role, Set<UUID>> roleMap) {
        public enum Role {
            // Rank goes from 0 (minimum) to 9 (maximum)
            OWNER("owner", 9), DEFAULT("member", 0);

            private final int rank;
            private final String key;

            Role(String key, int rank) {
                this.key = key;
                this.rank = rank;
            }

            public int getRank() { return this.rank; }
            public String getKey() { return this.key; }
        }

        boolean add(Role role, UUID playerUuid) {
            return roleMap.get(role).add(playerUuid);
        }

        boolean remove(UUID playerUuid) {
            for(Set<UUID> membersSet : roleMap.values())
                return membersSet.remove(playerUuid);
            return false;
        }

        int size() {
            int size = 0;
            for (Set<UUID> membersSet : roleMap.values())
                size += membersSet.size();
            return size;
        }

        @Nullable
        public Role getRole(UUID playerUuid) {
            for (Map.Entry<Role, Set<UUID>> membersSet : roleMap.entrySet()) {
                Role key = membersSet.getKey();
                Set<UUID> value = membersSet.getValue();
                if (value.contains(playerUuid)) return key;
            }
            return null;
        }

        public List<UUID> asList() {
            List<UUID> memberList = new LinkedList<>();
            for(Set<UUID> membersSet : roleMap.values()) {
                memberList.addAll(membersSet);
            }
            return memberList;
        }
    }
}
