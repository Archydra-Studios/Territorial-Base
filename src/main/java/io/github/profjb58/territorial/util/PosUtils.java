package io.github.profjb58.territorial.util;

import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.DoubleStream;

public class PosUtils {

    public static Vec3d zeroMove(Vec3d vecIn, double amount) {
        return new Vec3d(
                (vecIn.x == 0) ? vecIn.x + amount : vecIn.x,
                (vecIn.y == 0) ? vecIn.y + amount : vecIn.y,
                (vecIn.z == 0) ? vecIn.z + amount : vecIn.z
        );
    }
}
