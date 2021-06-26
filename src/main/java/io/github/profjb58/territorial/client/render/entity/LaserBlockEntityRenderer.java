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

    public LaserBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(LaserBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int power = be.getCachedState().get(Properties.POWER);
        float[] colour = be.getColour().getColorComponents();

        VertexConsumer lineConsumer = vertexConsumers.getBuffer(CustomRenderLayers.QUAD_LINES);
        Direction facing = be.getCachedState().get(Properties.FACING);

        // Don't render anything if it isn't being powered
        if(power != 0) {
            float w = LaserBlockEntity.SIGNAL_STRENGTH_WIDTHS[power - 1];
            float l = be.getReach();

            // Opaque beam
            matrices.push();
            RenderUtils.drawQuadLine(matrices, lineConsumer, facing, w/2, l, colour, 10);
            matrices.pop();

            // Transparent beam (fancy graphics)
            if(MinecraftClient.isFancyGraphicsOrBetter()) {
                matrices.push();
                RenderUtils.drawQuadLine(matrices, lineConsumer, facing, w, l, colour, 180);
                matrices.pop();
            }
        }
        // Lens
        matrices.push();
        RenderUtils.drawQuadLine(matrices, lineConsumer, facing, 0.5f, 0.38f, colour, 220);
        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(LaserBlockEntity blockEntity) {
        return true;
    }
}
