package io.github.profjb58.territorial.mixin.common;

import io.github.profjb58.territorial.event.template.ChunkClientSyncEvent;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {

    @Inject(method = "sendChunkDataPackets", at = @At("RETURN"))
    private void sendChunkDataPackets(ServerPlayerEntity player, MutableObject<ChunkDataS2CPacket> mutableObject, WorldChunk chunk, CallbackInfo ci) {
        ChunkClientSyncEvent.SYNC_CLIENT.invoker().onSync(player, chunk);
    }
}
