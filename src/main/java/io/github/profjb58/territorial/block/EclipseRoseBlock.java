package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.TickCounter;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.Random;

public class EclipseRoseBlock extends WitherRoseBlock {

    private static final int TRIGGER_RADIUS = Territorial.getConfig().getEclipseTriggerRadius();
    private final TickCounter displayTicker = new TickCounter(3);

    public EclipseRoseBlock() {
        super(StatusEffects.WITHER, FabricBlockSettings.of(Material.PLANT)
                .noCollision()
                .breakInstantly()
                .nonOpaque()
                .sounds(BlockSoundGroup.GRASS));
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        if (world.isClient && world.getDifficulty() != Difficulty.PEACEFUL) {
            if (entity instanceof LivingEntity livingEntity) {
                if(livingEntity instanceof PlayerEntity playerEntity) {
                    if(playerEntity.isCreative()) return;
                }
                var blindnessEffectInstance = new StatusEffectInstance(StatusEffects.BLINDNESS, 120, 0);
                livingEntity.addStatusEffect(blindnessEffectInstance);
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if(world.isClient) {
            final BlockPos immutablePos = new BlockPos(pos);
            var voxelShape = this.getOutlineShape(state, world, pos, ShapeContext.absent());
            var vec3d = voxelShape.getBoundingBox().getCenter();
            double x = (double)pos.getX() + vec3d.x, z = (double)pos.getZ() + vec3d.z;

            if (random.nextBoolean() && displayTicker.test()) {
                for(int i=0 ; i < 5; i++) {
                    double xPos = x + random.nextDouble() / 5.0D;
                    double zPos = z + random.nextDouble() / 5.0D;
                    double yPos = (double)pos.getY() + (1.5D - random.nextDouble());

                    world.addParticle(ParticleTypes.SQUID_INK, xPos,yPos, zPos, 0.0D, 0.5D, 0.0D);
                    TerritorialRegistry.ECLIPSE_EFFECT.eclipseLogicTick(world, pos);
                }
            }
            else {
                displayTicker.increment();
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        /*
        if(!state.isOf(newState.getBlock())) {
            if(!world.isClient) {
                for(var player : PlayerLookup.around((ServerWorld) world, pos, TRIGGER_RADIUS)) {
                    ArrayList<BlockPos> eclipseRoseBlocks = MathUtils.getBlocksWithinCube((ServerWorld) world,
                            player.getBlockPos(), this, TRIGGER_RADIUS);

                    var packetByteBuf = PacketByteBufs.create();
                    if(!eclipseRoseBlocks.isEmpty()) {
                        packetByteBuf.writeBlockPos(eclipseRoseBlocks.get(eclipseRoseBlocks.size() / 2));
                        ServerPlayNetworking.send(player, S2CPackets.SWITCH_ECLIPSE_ROSE_POS, packetByteBuf);
                    }
                    else {
                        ServerPlayNetworking.send(player, S2CPackets.RESET_ECLIPSE, packetByteBuf);
                    }
                }
            }
        }
         */
    }
}