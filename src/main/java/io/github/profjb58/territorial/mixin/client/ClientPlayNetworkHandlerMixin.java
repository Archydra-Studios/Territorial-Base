package io.github.profjb58.territorial.mixin.client;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method="onWorldTimeUpdate", at=@At("HEAD"), cancellable = true)
    void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if(player != null && player.hasStatusEffect(TerritorialRegistry.ECLIPSE_EFFECT)) ci.cancel();
    }
}