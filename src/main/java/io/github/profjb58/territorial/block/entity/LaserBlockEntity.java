package io.github.profjb58.territorial.block.entity;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LaserBlockEntity extends BlockEntity {

    private static final int MAX_DISTANCE = 30;
    private static final int TICK_COUNTER_UPDATE = 10;

    private static int tickCounter = TICK_COUNTER_UPDATE;
    private BlockPos endPos;
    private int length;
    private float width = 0.01f;

    public LaserBlockEntity(BlockPos pos, BlockState state) {
        super(TerritorialRegistry.LASER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    public BlockPos getLaserEndPos() { return endPos; }
    public int getLaserLength() { return length; }
    public float getLaserWidth() { return width; }


    public static void clientTick(World world, BlockPos pos, BlockState state, LaserBlockEntity be) {
        if (tickCounter >= TICK_COUNTER_UPDATE) {
            tickCounter = 0;
            Direction facing = be.getCachedState().get(Properties.FACING);

            for (int i = 0; i < MAX_DISTANCE; i++) {
                BlockPos posIterator = pos.offset(facing);
                ClientWorld clientWorld = (ClientWorld) be.getWorld();

                if (clientWorld != null) {
                    BlockState bs = clientWorld.getBlockState(posIterator);
                    if (bs != null) {
                        //if (bs.getOpacity(world, posIterator) >= 15 && !bs.isOf(Blocks.BEDROCK)) {
                            be.endPos = posIterator;
                            be.length = i + 1;
                        //}
                    }
                }
            }
        }
        tickCounter++;
    }
}
