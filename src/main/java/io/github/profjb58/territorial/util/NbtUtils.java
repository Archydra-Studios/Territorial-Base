package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.world.team.ServerTeam;
import io.github.profjb58.territorial.world.team.Team;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import io.github.profjb58.territorial.world.team.Team.Members;

import java.util.*;

public class NbtUtils {

    public static int[] serializeBlockPos(@NotNull BlockPos blockPos) {
        int[] posSerializable = new int[3];
        posSerializable[0] = blockPos.getX();
        posSerializable[1] = blockPos.getY();
        posSerializable[2] = blockPos.getZ();

        return posSerializable;
    }

    public static BlockPos deserializeBlockPos(int[] posSerializable) {
        return new BlockPos(posSerializable[0], posSerializable[1], posSerializable[2]);
    }

    public static NbtCompound removeBlockFeatures(NbtCompound nbtCompound) {
        if(nbtCompound != null) {
            nbtCompound.remove("id");
            nbtCompound.remove("x");
            nbtCompound.remove("y");
            nbtCompound.remove("z");
        }
        return nbtCompound;
    }

    public static NbtCompound getNbtFromMembers(Members members) {
        var membersListNbt = new NbtCompound();

        for(var role : Members.Role.values()) {
            var nbtList = new NbtList();
            var membersSet = members.roleMap().get(role);

            for(UUID member : membersSet) {
                var nbtCompound = new NbtCompound();
                nbtCompound.putUuid("uuid", member);
                nbtList.add(nbtCompound);
            }
            membersListNbt.put(role.getKey(), nbtList);
        }
        return membersListNbt;
    }

    public static Members getMembersFromNbt(NbtCompound nbtCompound) {
        Map<Members.Role, Set<UUID>> teamMembers = new EnumMap<>(Members.Role.class);

        if(nbtCompound != null) {
            for(var role : Members.Role.values()) {
                if(nbtCompound.contains(role.getKey())) {
                    var members = new HashSet<UUID>();
                    var nbtList = nbtCompound.getList(role.getKey(), NbtType.COMPOUND);

                    for(var memberElement : nbtList)
                        members.add(((NbtCompound) memberElement).getUuid("uuid"));
                    teamMembers.put(role, members);
                }
            }
        }
        return new Members(teamMembers);
    }
}
