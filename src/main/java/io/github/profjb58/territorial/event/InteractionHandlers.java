package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import io.github.profjb58.territorial.event.template.ServerWorldEvents;
import io.github.profjb58.territorial.networking.C2SPackets;
import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

public class InteractionHandlers {

    static int ticksSinceBlockAttack = 0;

    public static void init() {

        // Use Block handler
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(!world.isClient()) {
                LockableBlockEntity lbe = new LockableBlockEntity((ServerWorld) world, hitResult.getBlockPos());
                if(lbe.exists()) {
                    if(!lbe.getLockOwner().equals(player.getUuid())) {
                        player.sendMessage(new TranslatableText("message.territorial.locked"), true);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Attack block handler
        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            if(world.isClient()) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(blockPos);
                ClientPlayNetworking.send(C2SPackets.CLIENT_ATTACK_BLOCK, buf);
            }
            else {
                ticksSinceBlockAttack = 0; // Update ticks since last attack on the server or integrated server
            }
            return ActionResult.PASS;
        });

        // Attack entity handler
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if(!world.isClient()) {
                LockUtils.removeLockFatigueEffect((ServerPlayerEntity) player);
            }
            return ActionResult.PASS;
        });
    }
}
