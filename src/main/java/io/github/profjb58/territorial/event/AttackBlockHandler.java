package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.networking.C2SPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AttackBlockHandler implements AttackBlockCallback {

    public static void init() {
        AttackBlockCallback.EVENT.register(new AttackBlockHandler());
    }

    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockPos blockPos, Direction direction) {
        if(world.isClient()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(blockPos);
            ClientPlayNetworking.send(C2SPackets.CLIENT_ATTACK_BLOCK, buf);
        }
        return ActionResult.PASS;
    }

}
