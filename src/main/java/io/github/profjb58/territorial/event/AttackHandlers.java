package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.entity.effect.LockFatigueInstance;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;

public class AttackHandlers {

    static int ticksSinceBlockAttack = 0;

    public static void init() {
        // Attack block handler
        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            var lbe = new LockableBlockEntity(world, blockPos);

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
    }
}
