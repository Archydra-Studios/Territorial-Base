package io.github.profjb58.territorial.mixin;

import io.github.profjb58.territorial.networking.C2SPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionMixin {

    @Shadow
    private boolean breakingBlock;

    @Inject(at = @At("HEAD"), method = "updateBlockBreakingProgress", cancellable = true)
    public void updateBlockBreakingProgress(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> ci)
    {
        /*  Fixes MC issue: https://bugs.mojang.com/browse/MC-69865
            (breakingBlock = false) check is to make sure packets are not repeatedly sent when it's clear that a block is broken */
        if(!breakingBlock) {
            var packetByteBuf = PacketByteBufs.create();
            packetByteBuf.writeBlockPos(blockPos);
            ClientPlayNetworking.send(C2SPackets.BREAKING_BLOCK, packetByteBuf);
        }
    }
}
