package io.github.profjb58.territorial.util;

import net.minecraft.util.math.Vec3d;

import java.util.stream.DoubleStream;

public class PosUtils {

    public static Vec3d zeroMove(Vec3d vecIn, double amount) {
        double[] vecMoved = DoubleStream.of(vecIn.x, vecIn.y, vecIn.z)
                .map(n -> {
                    if(n == 0) n += amount;
                    return n;
                }).toArray();
        return new Vec3d(vecMoved[0], vecMoved[1], vecMoved[2]);
    }
}
