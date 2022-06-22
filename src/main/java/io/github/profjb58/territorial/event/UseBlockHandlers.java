package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.api.event.common.LockableBlockEvents;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.block.enums.LockSound;
import io.github.profjb58.territorial.item.KeyItem;
import io.github.profjb58.territorial.item.PadlockItem;
import io.github.profjb58.territorial.networking.s2c.SyncLockInfoPacket;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseBlockHandlers {

    public static void init() {
        UseBlockCallback.EVENT.register(UseBlockHandlers::interact);
    }

    private static ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(!world.isClient) {
            var heldItem = player.getStackInHand(player.getActiveHand()).getItem();

            // Pass off interaction to corresponding items
            if(heldItem instanceof PadlockItem padlockItem)
                return padlockItem.useOnBlock(player, (ServerWorld) world, hitResult);
            else if(heldItem instanceof KeyItem keyItem && player.isSneaking())
                return keyItem.useOnBlockWhileSneaking((ServerPlayerEntity) player, (ServerWorld) world, hitResult);

            var lbe = new LockableBlockEntity(world, hitResult.getBlockPos());
            if(lbe.exists()) return interactWithLockable(lbe, (ServerPlayerEntity) player, world);
        }
        else if(TerritorialClient.lockableHud != null) TerritorialClient.lockableHud.clear();
        return ActionResult.PASS;
    }

    private static ActionResult interactWithLockable(LockableBlockEntity lbe, ServerPlayerEntity player, World world) {
        //if(!player.hasPermissionLevel(TerritorialServer.minOpLevel)) { // Bypass protections for operators
            var lb = lbe.getBlock();
            var keySearchResult = lb.findMatchingKey(player, true);
            var keyItemStack = keySearchResult.getLeft();
            var keyInventory = keySearchResult.getRight();

            if(keyItemStack == null) {
                if (!lb.lockOwnerUuid().equals(player.getUuid())) // No matching key found
                    new SyncLockInfoPacket(player, lbe, true, SyncLockInfoPacket.DisplayLocation.HUD).send();
                    //player.sendMessage(new TranslatableText("message.territorial.locked"), true);
                else // Owns the lock but no matching key was found
                    new SyncLockInfoPacket(player, lbe, false, SyncLockInfoPacket.DisplayLocation.HUD).send();
                    //player.sendMessage(new TranslatableText("message.territorial.lock_no_key"), true);

                lb.playSound(LockSound.DENIED_ENTRY, world);
                LockableBlockEvents.INTERACT.invoker().interact(lbe.getBlock(), player, LockableBlockEvents.InteractionType.FAILED);
                return ActionResult.FAIL;
            }
            else {
                boolean cancelAction;
                var keyItem = (KeyItem) keyItemStack.getItem();

                if(keyItem.isMasterKey()) { // Found a master key
                    cancelAction = keyItem.onUseMasterKey(keyItemStack, player, lb);
                    keyInventory.markDirty(); // Make sure the container the master key is stored in is updated
                }
                else cancelAction = LockableBlockEvents.INTERACT.invoker().interact(lbe.getBlock(), player, LockableBlockEvents.InteractionType.OPEN_KEY_MATCHED);
                if(cancelAction) return ActionResult.FAIL;
            }
            new SyncLockInfoPacket(player, lbe, false, SyncLockInfoPacket.DisplayLocation.SCREEN).send();
            return ActionResult.PASS;
    }

}
