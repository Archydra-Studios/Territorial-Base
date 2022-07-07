package io.github.profjb58.territorial.util.anticheat;

import io.github.profjb58.territorial.util.UuidUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;

import javax.annotation.Nullable;
import java.util.UUID;

public class AntiCheatUtils {

    public static boolean isLootStackDuped(ItemStack itemStack, ServerPlayerEntity targetPlayer) {
        UUID stackUuid = UuidUtils.LootStack.getUuid(itemStack);
        if(stackUuid != null && (!targetPlayer.isCreative() && !targetPlayer.hasPermissionLevel(2))) {
            Inventory[] inventories = { targetPlayer.getInventory(), targetPlayer.getEnderChestInventory() };
            int stackCount = 0;
            ItemStack invItemStack;
            UUID invStackUuid;

            for(Inventory inv : inventories) {
                for(int i=0; i < inv.size(); i++) {
                    invItemStack = inv.getStack(i);

                    if(itemStack.getItem().equals(invItemStack.getItem())) {
                        invStackUuid = UuidUtils.LootStack.getUuid(invItemStack);
                        if(invStackUuid != null && invStackUuid.equals(stackUuid)) {
                            stackCount++;
                            if(stackCount > 1) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void warnLootStackDuped(ItemStack itemStack, ServerPlayerEntity player) {
        var warningMessageFormatted = new TranslatableText("message.territorial.stack_dupe_formatted.warn", itemStack.getItem().getName(), player.getDisplayName());
        var playerManager = player.getServer().getPlayerManager();

        for (PlayerEntity pe : playerManager.getPlayerList()) {
            if (pe.isCreativeLevelTwoOp()) pe.sendMessage(warningMessageFormatted, false);
        }
    }
}
