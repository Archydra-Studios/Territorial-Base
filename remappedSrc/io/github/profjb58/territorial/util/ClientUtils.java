package io.github.profjb58.territorial.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ClientUtils {

    public static BlockPos getRaycastPos(PlayerEntity player, double maxDistance) {
        HitResult result = player.raycast(maxDistance, 1f, false);
        Vec3d hitVec = result.getPos();
        return new BlockPos(hitVec.x, hitVec.y, hitVec.z);
    }
}
