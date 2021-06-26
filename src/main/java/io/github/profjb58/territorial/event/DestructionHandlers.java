package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.event.template.ServerWorldEvents;
import io.github.profjb58.territorial.mixin.ExplosionAccessor;
import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;

import java.util.Collections;
import java.util.LinkedList;

public class DestructionHandlers {

    public static void init() {
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
                        int numItemsToDrop = LockUtils.Calculations.calcNumItemsToDrop(numSlots, AttackHandlers.ticksSinceBlockAttack);
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

        // Before explosion event
        ServerWorldEvents.BEFORE_EXPLOSION.register((explosion, world) -> {
            explosion.getAffectedBlocks().removeIf(pos -> {

                LockableBlockEntity lbe = new LockableBlockEntity(world, pos);
                if(lbe.exists()) {
                    float power = ((ExplosionAccessor) explosion).getPower();
                    return !(power > lbe.getBlock().getBlastResistance());
                }
                return false;
            });
        });
    }
}
