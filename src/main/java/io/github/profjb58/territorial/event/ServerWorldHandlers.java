package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.access.StatusEffectInstanceAccess;
import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import io.github.profjb58.territorial.event.template.ServerWorldEvents;
import io.github.profjb58.territorial.mixin.ExplosionAccessor;
import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ServerWorldHandlers {

    private static final int LOCK_FATIGUE_CHECK_TICK_INTERVAL = 40;
    private static final int LOCK_FATIGUE_CHECK_RADIUS = 8;

    // Update limit counters
    private static int lockTicksCounter = 0;

    public static void init() {
        // Start server world ticks
        ServerTickEvents.START_WORLD_TICK.register(serverWorld -> {
            List<ServerPlayerEntity> players = serverWorld.getPlayers();

            // Remove lock fatigue effect if the player moves far enough away from the block
            if(lockTicksCounter == LOCK_FATIGUE_CHECK_TICK_INTERVAL) { // Limit update rate
                for (ServerPlayerEntity player : players) {
                    StatusEffectInstance sei = player.getStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                    if (sei != null) {
                        BlockPos lastPosApplied = ((StatusEffectInstanceAccess) sei).getLastPosApplied();
                        if (lastPosApplied != null) {
                            if (!lastPosApplied.isWithinDistance(player.getBlockPos(), LOCK_FATIGUE_CHECK_RADIUS)) {
                                player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE);
                            }
                        }
                    }
                }
                lockTicksCounter = 0;
            }
            // Increment tick counters
            lockTicksCounter++;
            InteractionHandlers.ticksSinceBlockAttack++;
        });

        // Before explosion event
        ServerWorldEvents.BEFORE_EXPLOSION.register((explosion, world) -> {
            explosion.getAffectedBlocks().removeIf(pos -> {
                LockableBlockEntity lbe = new LockableBlockEntity(world, pos);
                if(lbe.exists()) {
                    float power = ((ExplosionAccessor) explosion).getPower();
                    return !(power > lbe.getBlastResistance());
                }
                return false;
            });
        });

        // Locked block broken by a player that isn't the owner
        ServerWorldEvents.ON_BLOCK_BREAK.register((world, pos, player) -> {
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
            }
        });
    }
}
