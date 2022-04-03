package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.entity.effect.EclipseStatusEffect;
import io.github.profjb58.territorial.entity.effect.LockFatigueStatusEffect;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class C2SPackets {

    // Packet to notify the server that a player is breaking a block
    public static final Identifier BREAKING_BLOCK = new Identifier(Territorial.MOD_ID, "breaking_block");

    public static final Identifier ADD_ECLIPSE_EFFECT = new Identifier(Territorial.MOD_ID, "add_eclipse_effect");

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(BREAKING_BLOCK, (server, player, handler, buf, responseSender) -> {
            // Target block
            final BlockPos target = buf.readBlockPos();

            server.execute(() -> {
                if(!LockFatigueStatusEffect.addEffect(player, target)) {
                    player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ADD_ECLIPSE_EFFECT, ((server, player, handler, buf, responseSender) -> {
            final int totalDuration = buf.readInt();

            server.execute(() -> {
                player.addStatusEffect(new StatusEffectInstance(TerritorialRegistry.ECLIPSE_EFFECT, totalDuration));
            });
        }));
    }
}
