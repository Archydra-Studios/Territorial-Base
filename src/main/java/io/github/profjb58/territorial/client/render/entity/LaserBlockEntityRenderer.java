package io.github.profjb58.territorial.client.render.entity;

import io.github.profjb58.territorial.block.entity.LaserBlockEntity;
import io.github.profjb58.territorial.client.render.CustomRenderLayers;
import io.github.profjb58.territorial.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class LaserBlockEntityRenderer implements BlockEntityRenderer<LaserBlockEntity> {

    private final float[] signalStrengthWidths = { 0.001f, 0.0015f, 0.0030f, 0.0045f, 0.0070f, 0.01f, 0.0135f, 0.02f, 0.025f, 0.035f, 0.06f, 0.1f, 0.16f, 0.25f, 0.38f };

    public LaserBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(LaserBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean powered = be.getCachedState().get(Properties.POWERED);
        int power = be.getCachedState().get(Properties.POWER);

        // Don't render anything if it isn't being powered
        if(!powered || power == 0) return;

        Direction facing = be.getCachedState().get(Properties.FACING);
        float w = signalStrengthWidths[power - 1];
        float l = 30;
        int r = 255;
        int g = 125;
        int b = 0;
        int a = 100;

        VertexConsumer lineConsumer = vertexConsumers.getBuffer(CustomRenderLayers.QUAD_LINES);
        matrices.push();
        RenderUtils.drawQuadLine(matrices, lineConsumer, facing, w/2, l, r, g, b, 255);
        matrices.pop();

        if(MinecraftClient.isFancyGraphicsOrBetter()) {
            matrices.push();
            RenderUtils.drawQuadLine(matrices, lineConsumer, facing, w, l, 200, g, b, a);
            matrices.pop();
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(LaserBlockEntity blockEntity) {
        return true;
    }
}
