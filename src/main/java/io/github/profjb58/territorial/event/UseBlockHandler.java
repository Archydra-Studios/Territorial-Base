package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialServer;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.block.enums.LockSound;
import io.github.profjb58.territorial.item.KeyItem;
import io.github.profjb58.territorial.item.PadlockItem;
import io.github.profjb58.territorial.networking.s2c.SyncLockInfoPacket;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import static io.github.profjb58.territorial.TerritorialClient.lockableHud;

public class UseBlockHandler implements UseBlockCallback {

    public static void init() {
        UseBlockCallback.EVENT.register(new UseBlockHandler());
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(!world.isClient) {
            var heldItem = player.getStackInHand(player.getActiveHand()).getItem();

            // Pass off interaction to corresponding items
            if(heldItem instanceof PadlockItem padlockItem)
                return padlockItem.useOnBlock(player, (ServerWorld) world, hitResult);
            else if(heldItem instanceof KeyItem keyItem)
                return keyItem.useOnBlock((ServerPlayerEntity) player, (ServerWorld) world, hitResult);

            var lbe = new LockableBlockEntity(world, hitResult.getBlockPos());
            if(lbe.exists()) return interactWithLockable(lbe, (ServerPlayerEntity) player, world);
        }
        return ActionResult.PASS;
    }

    public ActionResult interactWithLockable(LockableBlockEntity lbe, ServerPlayerEntity player, World world) {
        //if(!player.hasPermissionLevel(TerritorialServer.minOpLevel)) { // Bypass protections for operators
            var lb = lbe.getBlock();
            var keySearchResult = lb.findMatchingKey(player, true);
            var keyItemStack = keySearchResult.getLeft();
            var keyInventory = keySearchResult.getRight();

            if(keyItemStack == null) {
                if (!lb.lockOwnerUuid().equals(player.getUuid())) // No matching key found
                    new SyncLockInfoPacket(player, lbe, true).send();
                    //player.sendMessage(new TranslatableText("message.territorial.locked"), true);
                else // Owns the lock but no matching key was found
                    new SyncLockInfoPacket(player, lbe, false).send();
                    //player.sendMessage(new TranslatableText("message.territorial.lock_no_key"), true);
                lb.playSound(LockSound.DENIED_ENTRY, world);
                return ActionResult.FAIL;
            }
            else {
                var keyItem = (KeyItem) keyItemStack.getItem();
                if(keyItem.isMasterKey()) { // Found a master key
                    keyItem.onUseMasterKey(keyItemStack, (ServerPlayerEntity) player, lb);
                    keyInventory.markDirty(); // Make sure the container the master key is stored in is updated
                }
            }
            return ActionResult.PASS;
        //}
        //return ActionResult.FAIL;
    }
}
