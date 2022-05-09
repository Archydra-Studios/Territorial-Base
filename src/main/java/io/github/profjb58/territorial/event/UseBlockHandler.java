package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.item.KeyItem;
import io.github.profjb58.territorial.item.PadlockItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
        if(!player.isCreative()) {
            var lbe = new LockableBlockEntity(world, hitResult.getBlockPos());
            if (lbe.exists()) {
                if(world.isClient) { // TODO - Improve this...
                    lockableHud.ignoreCycle();
                }
                else {
                    Item heldItem = player.getStackInHand(player.getActiveHand()).getItem();
                    if(player.isSneaking()) { // Pass shift-click functionality to the corresponding item classes
                        if(heldItem instanceof PadlockItem || heldItem instanceof KeyItem) {
                            return ActionResult.PASS;
                        }
                    }
                    LockableBlock lb = lbe.getBlock();
                    var keySearchResult = lb.findMatchingKey((ServerPlayerEntity) player, true);
                    ItemStack keyItemStack = keySearchResult.getLeft();
                    Inventory keyInventory = keySearchResult.getRight();

                    if(keyItemStack == null) {
                        if (!lb.lockOwnerUuid().equals(player.getUuid())) { // No matching key found
                            player.sendMessage(new TranslatableText("message.territorial.locked"), true);
                        }
                        else { // Owns the lock but no matching key was found
                            player.sendMessage(new TranslatableText("message.territorial.lock_no_key"), true);
                        }
                        lb.playSound(LockableBlock.LockSound.DENIED_ENTRY, world);
                        return ActionResult.FAIL;
                    }
                    else {
                        KeyItem keyItem = (KeyItem) keyItemStack.getItem();
                        if(keyItem.isMasterKey()) {
                            keyItem.onUseMasterKey(keyItemStack, (ServerPlayerEntity) player, lb);
                            keyInventory.markDirty(); // Make sure the container the master key is stored in is updated
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
