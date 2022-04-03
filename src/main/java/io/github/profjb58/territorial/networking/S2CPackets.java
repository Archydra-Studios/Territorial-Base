package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class S2CPackets {

    public static final Identifier SWITCH_ECLIPSE_ROSE_POS = new Identifier(Territorial.MOD_ID, "switch_eclipse_rose_pos");
    public static final Identifier RESET_ECLIPSE = new Identifier(Territorial.MOD_ID, "reset_eclipse");

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SWITCH_ECLIPSE_ROSE_POS, (client, handler, buf, responseSender) -> {
            final BlockPos closestPos = buf.readBlockPos();

            client.execute(() -> {
                //EclipseStatusEffect.setLastPosApplied(closestPos);
            });
        });

        /*ClientPlayNetworking.registerGlobalReceiver(RESET_ECLIPSE, (client, handler, buf, responseSender) ->
                client.execute(EclipseStatusEffect::startEclipseCooldown));*/
    }
}
