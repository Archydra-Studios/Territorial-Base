package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.entity.effect.LockFatigueStatusEffect;
import io.github.profjb58.territorial.event.registry.TerritorialNetworkRegistry;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class StartBreakingBlockPacket implements C2SPacket {

    private BlockPos targetPos;

    public StartBreakingBlockPacket() {}

    public StartBreakingBlockPacket(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    @Override
    public void execute(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if(!LockFatigueStatusEffect.addEffect(player, targetPos))
            player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(targetPos);
    }

    @Override
    public void read(PacketByteBuf buf) {
        targetPos = buf.readBlockPos();
    }

    @Override
    public Identifier getId() {
        return TerritorialNetworkRegistry.START_BREAKING_BLOCK_PACKET_ID;
    }
}
