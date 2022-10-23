package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.client.ClientCachedStorage;
import io.github.profjb58.territorial.entity.effect.LockFatigueStatusEffect;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.TickCounter;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AttackHandlers {

    static final TickCounter TICKS_SINCE_BLOCK_ATTACK = new TickCounter(Integer.MAX_VALUE);
    private static ClientCachedStorage clientCachedStorage;

    public static void initClient(ClientCachedStorage clientCachedStorage) {
        AttackHandlers.clientCachedStorage = clientCachedStorage;
    }

    public static void init() {
        AttackBlockCallback.EVENT.register(AttackHandlers::onAttackBlock);
        AttackEntityCallback.EVENT.register(AttackHandlers::onAttackEntity);
    }

    private static ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if(world.isClient) {
            var clientPlayer = (ClientPlayerEntity) player;
            if(clientCachedStorage.isLockableBlock(clientPlayer, world.getChunk(pos).getPos(), pos)) {
                LockFatigueStatusEffect.addEffect(player, 1000, Integer.MAX_VALUE);
            }
        } else {
            var lbe = new LockableBlockEntity(world, pos);
            if(lbe.exists()) {
                if(player.hasStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT)) {
                    player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT);
                    LockFatigueStatusEffect.addEffect((ServerPlayerEntity) player, pos);
                } else {
                    return ActionResult.FAIL;
                }
            }
            // Update ticks since last attack on the dedicated or integrated server for the Ender Key GUI
            TICKS_SINCE_BLOCK_ATTACK.reset();
        }
        return ActionResult.PASS;
    }

    private static ActionResult onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult ehr) {
        if(!world.isClient()) {
            if(player.hasStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT)) {
                player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT);
            }
        }
        return ActionResult.PASS;
    }
}
