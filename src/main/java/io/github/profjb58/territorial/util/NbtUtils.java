package io.github.profjb58.territorial.util;

import io.github.profjb58.territorial.world.team.ServerTeam;
import io.github.profjb58.territorial.world.team.Team;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import io.github.profjb58.territorial.world.team.Team.Members;

import javax.annotation.Nullable;
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

    private static NbtCompound getRolesNbt(Members members) {
        var nbtRoles = new NbtCompound();
        var rolesNbtList = new NbtList();
        var roleMap = members.roleMap();

        for(Members.Role role : roleMap.keySet()) {
            var nbtRole = new NbtCompound();
            nbtRole.putString("name", role.name());
            nbtRole.putInt("rank", role.rank());
            rolesNbtList.add(nbtRole);
        }
        nbtRoles.put("roles", rolesNbtList);
        return nbtRoles;
    }

    @Nullable
    private static List<Members.Role> getRolesFromNbt(NbtCompound nbtCompound) {
        var rolesList = new ArrayList<Members.Role>();
        if(nbtCompound.contains("roles")) {
            var rolesNbtList = nbtCompound.getList("roles", NbtType.COMPOUND);

            for(var rolesNbtElement : rolesNbtList) {
                String name = ((NbtCompound) rolesNbtElement).getString("name");
                int rank = ((NbtCompound) rolesNbtElement).getInt("rank");
                rolesList.add(new Members.Role(name, rank));
            }
            return rolesList;
        }
        return null;
    }

    public static NbtCompound getMembersNbt(Members members) {
        var roleMap = members.roleMap();
        var membersNbt = getRolesNbt(members);

        for(var role : roleMap.keySet()) { // Cycle through all available roles
            var membersNbtList = new NbtList();
            var membersSet = roleMap.get(role);

            for(UUID member : membersSet) { // Cycle through all members that have this role
                var nbtMember = new NbtCompound();
                nbtMember.putUuid("uuid", member);
                membersNbtList.add(nbtMember);
            }
            membersNbt.put(role.name(), membersNbtList);
        }
        return membersNbt;
    }

    public static Members getMembersFromNbt(NbtCompound membersNbt) {
        var rolesList = getRolesFromNbt(membersNbt);
        var roleMap = new HashMap<Members.Role, Set<UUID>>();

        if(rolesList != null && rolesList.size() > 0) {
            for(var role : rolesList) {
                if(membersNbt.contains(role.name())) {
                    var members = new HashSet<UUID>();
                    var membersNbtList = membersNbt.getList(role.name(), NbtType.COMPOUND);

                    for(var memberNbtElement : membersNbtList)
                        members.add(((NbtCompound) memberNbtElement).getUuid("uuid"));
                    roleMap.put(role, members);
                }
            }
        }
        return new Members(roleMap);
    }
}
