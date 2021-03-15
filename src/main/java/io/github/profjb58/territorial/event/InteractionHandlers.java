package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import io.github.profjb58.territorial.effect.LockFatigueInstance;
import io.github.profjb58.territorial.item.PadlockItem;
import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

import java.util.Collections;
import java.util.LinkedList;

public class InteractionHandlers {

    static int ticksSinceBlockAttack = 0;

    public static void init() {

        // Use Block handler
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(!world.isClient()) {
                if(!player.isCreative() || !player.isHolding(TerritorialRegistry.LOCKPICK_CREATIVE)) {
                    LockableBlockEntity lbe = new LockableBlockEntity(world, hitResult.getBlockPos());
                    if (lbe.exists()) {
                        LockableBlock lb = lbe.getBlock();
                        ItemStack heldItemStack = player.getStackInHand(player.getActiveHand());

                        if(player.isSneaking() && (heldItemStack.getItem() instanceof PadlockItem)) {
                            if(!player.isCreative() && ) {
                                player.getMainHandStack().decrement(1);
                            }
                            return ActionResult.PASS;
                        }

                        if(!lb.hasMatchingKey((ServerPlayerEntity) player)) {
                            if (!lb.getLockOwner().equals(player.getUuid())) { // No matching key found
                                player.sendMessage(new TranslatableText("message.territorial.locked"), true);
                            }
                            else { // Owns the lock but no matching key was found
                                player.sendMessage(new TranslatableText("message.territorial.lock_no_key"), true);
                            }
                            lb.playSound(LockableBlock.LockSound.DENIED_ENTRY, world);
                            return ActionResult.FAIL;
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Attack block handler
        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            LockableBlockEntity lbe = new LockableBlockEntity(world, blockPos);

            if(world.isClient) {
                if(lbe.exists()) {
                    LockFatigueInstance.addEffect(player, blockPos);
                }
            }
            else {
                if(lbe.exists()) { // Failsafe, fired just in-case client server data isn't synced to the client
                    LockFatigueInstance.addEffect(player, blockPos);
                }
                ticksSinceBlockAttack = 0; // Update ticks since last attack on the dedicated or integrated server
            }
            return ActionResult.PASS;
        });

        // Attack entity handler
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if(!world.isClient()) {
                LockFatigueInstance.removeEffect(player);
            }
            return ActionResult.PASS;
        });

        // Locked block broken by a player that isn't the owner
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            LockableBlockEntity lbe = new LockableBlockEntity(world, pos);
            if(lbe.exists()) {
                BlockEntity be = lbe.getBlockEntity();
                if(be instanceof Inventory) {
                    Inventory beInv = (Inventory) lbe.getBlockEntity();
                    if(!beInv.isEmpty()) {
                        LinkedList<Integer> occupiedSlots = new LinkedList<>();
                        for(int i=0; i < beInv.size(); i++) {
                            if (!beInv.getStack(i).isEmpty()) {
                                occupiedSlots.add(i);
                            }
                        }
                        int numSlots = occupiedSlots.size();
                        int numItemsToDrop = LockUtils.Calculations.calcNumItemsToDrop(numSlots, InteractionHandlers.ticksSinceBlockAttack);
                        Collections.shuffle(occupiedSlots); // Randomize which item stacks are dropped

                        for(int i=0; i < (occupiedSlots.size() - numItemsToDrop); i++) {
                            beInv.removeStack(occupiedSlots.get(i));
                        }
                    }
                }
                lbe.getBlock().playSound(LockableBlock.LockSound.LOCK_DESTROYED, world);
            }
            return true; // Never cancel the block break
        });
    }
}
