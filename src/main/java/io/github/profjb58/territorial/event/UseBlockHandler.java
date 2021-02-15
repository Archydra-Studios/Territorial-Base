package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseBlockHandler implements UseBlockCallback {

    public static void init() {
        UseBlockCallback.EVENT.register(new UseBlockHandler());
    }

    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHitResult) {
        if(!world.isClient()) {
            LockableBlockEntity lbe = new LockableBlockEntity((ServerWorld) world, blockHitResult.getBlockPos());
            if(lbe.exists()) {
                if(!lbe.getLockOwner().equals(playerEntity.getUuid())) {
                    playerEntity.sendMessage(new TranslatableText("message.territorial.locked"), true);
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.PASS;
    }
}
