package io.github.profjb58.territorial.client.render.entity;

import com.mojang.datafixers.util.Pair;
import io.github.profjb58.territorial.block.entity.BoundaryBeaconBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;

public class BoundaryBeaconBlockEntityRenderer extends BaseBeaconBlockEntityRenderer {

    @Override
    public void render(BoundaryBeaconBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        super.render(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);

        List<Pair<BannerPattern, DyeColor>> patterns = blockEntity.getBannerPatterns();
        if(patterns != null) {

            var modelData =  BannerBlockEntityRenderer.getTexturedModelData();
            var bannerPart = modelData.createModel().getChild("flag");

            World world = blockEntity.getWorld();
            if(world != null) {
                matrices.push();
                matrices.translate(0.5, 3.4, 0);
                matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);

                long time = blockEntity.getWorld().getTime();
                float timeMultiplier = (Math.floorMod(time, 100L) + tickDelta) / 100.0F;
                bannerPart.pitch = (-0.0125F + 0.01F * MathHelper.cos(6.2831855F * timeMultiplier)) * 3.1415927F;

                //matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(MinecraftClient.getInstance().cameraEntity.lookingRotR));
                //matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));

                BannerBlockEntityRenderer.renderCanvas(matrices, vertexConsumers, light, overlay, bannerPart, ModelLoader.BANNER_BASE, true, patterns);
                matrices.pop();
            }
        }
    }
}
