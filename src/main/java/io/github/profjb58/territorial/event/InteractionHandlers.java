package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
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
                        if (!lb.getLockOwner().equals(player.getUuid())) {
                            player.sendMessage(new TranslatableText("message.territorial.locked"), true);
                            LockUtils.playSound(LockUtils.LockSound.DENIED_ENTRY, lb.getBlockPos(), world);
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
                    LockUtils.addEffect(player, blockPos);
                }
            }
            else {
                if(lbe.exists()) { // Failsafe, fired just in-case client server data isn't synced to the client
                    LockUtils.addEffect(player, blockPos);
                }
                else {
                    LockUtils.removeEffect(player);
                }
                ticksSinceBlockAttack = 0; // Update ticks since last attack on the dedicated or integrated server
            }
            return ActionResult.PASS;
        });

        // Attack entity handler
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if(!world.isClient()) {
                LockUtils.removeEffect(player);
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
                LockUtils.playSound(LockUtils.LockSound.LOCK_DESTROYED, pos, world);
            }
            return true; // Never cancel the block break
        });
    }
}
