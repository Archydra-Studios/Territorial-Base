package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AttackEntityHandler implements AttackEntityCallback {

    public static void init() {
        AttackEntityCallback.EVENT.register(new AttackEntityHandler());
    }

    @Override
    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        if(!world.isClient()) {
            LockUtils.removeLockFatigueEffect((ServerPlayerEntity) playerEntity);
        }
        return ActionResult.PASS;
    }
}
