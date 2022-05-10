package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.block.enums.LockSound;
import io.github.profjb58.territorial.event.template.ServerWorldEvents;
import io.github.profjb58.territorial.mixin.common.ExplosionAccessor;
import io.github.profjb58.territorial.mixin.common.ExplosionAccessor;
import io.github.profjb58.territorial.util.MathUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.Collections;
import java.util.LinkedList;

public class DestructionHandlers {

    public static void init() {
        ServerWorldEvents.BEFORE_EXPLOSION.register(DestructionHandlers::beforeExplosion);
        PlayerBlockBreakEvents.BEFORE.register(DestructionHandlers::getLockableBlockDrops);
    }

    /**
     *  Locked block broken by a player that isn't the owner
     */
    private static boolean getLockableBlockDrops(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        var lbe = new LockableBlockEntity(world, pos);
        if(lbe.exists()) {
            var be = lbe.getBlockEntity();

            if(be instanceof Inventory) {
                Inventory beInv = (Inventory) lbe.getBlockEntity();
                if(!beInv.isEmpty()) {

                    var occupiedSlots = new LinkedList<Integer>();
                    for(int i=0; i < beInv.size(); i++) {
                        if (!beInv.getStack(i).isEmpty()) {
                            occupiedSlots.add(i);
                        }
                    }
                    int numSlots = occupiedSlots.size();
                    int numItemsToDrop = MathUtils.Locks.calcNumItemsToDrop(numSlots,
                            AttackHandlers.TICKS_SINCE_BLOCK_ATTACK.value());
                    Collections.shuffle(occupiedSlots); // Randomize which item stacks are dropped

                    for(int i=0; i < (occupiedSlots.size() - numItemsToDrop); i++) {
                        beInv.removeStack(occupiedSlots.get(i));
                    }
                }
            }
            lbe.getBlock().playSound(LockSound.LOCK_DESTROYED, world);
        }
        return true; // Never cancel the block break
    }

    private static void beforeExplosion(Explosion explosion, ServerWorld world) {
        explosion.getAffectedBlocks().removeIf(pos -> {
            var lbe = new LockableBlockEntity(world, pos);
            if(lbe.exists()) {
                float power = ((ExplosionAccessor) explosion).getPower();
                return !(power > lbe.getBlock().lockType().getBlastResistance());
            }
            return false;
        });
    }
}
