package io.github.profjb58.territorial.util;

import io.github.cottonmc.cotton.gui.widget.WTiledSprite;
import net.minecraft.util.math.Direction;
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

    public static float getDistanceAlongAxis(Vec3d vecStart, Vec3d vecFinish, Direction.Axis axis) {
        double distance = 0;
        switch(axis) {
            case X -> distance = Math.abs(vecFinish.x - vecStart.x);
            case Y -> distance = Math.abs(vecFinish.y - vecStart.y);
            case Z -> distance = Math.abs(vecFinish.z - vecStart.z);
        }
        return (float) distance;
    }
}
