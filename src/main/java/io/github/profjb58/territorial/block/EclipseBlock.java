package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.networking.AddEclipseEffectPacket;
import io.github.profjb58.territorial.util.TickCounter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.Random;

public interface EclipseBlock {

    @Environment(EnvType.CLIENT)
    default void eclipseDisplayTick(BlockState state, World world, BlockPos pos, Random random, TickCounter ticker, int totalDuration, int maxReach) {
        if (random.nextBoolean() && ticker.test()) {
            var block = state.getBlock();
            var voxelShape = block.getOutlineShape(state, world, pos, ShapeContext.absent());
            var vec3d = voxelShape.getBoundingBox().getCenter();
            double x = pos.getX() + vec3d.x, z = pos.getZ() + vec3d.z;

            for(int i=0 ; i < 5; i++) {
                double xPos = x + random.nextDouble() / 5.0D;
                double zPos = z + random.nextDouble() / 5.0D;
                double yPos = pos.getY() + ((voxelShape.getMax(Direction.Axis.Y) + 1.5D) - random.nextDouble());
                world.addParticle(ParticleTypes.SQUID_INK, xPos,yPos, zPos, 0.0D, 0.5D, 0.0D);
            }

            var effect = MinecraftClient.getInstance().player.getStatusEffect(TerritorialRegistry.ECLIPSE_EFFECT);
            var duration = effect != null ? effect.getDuration() : 0;
            var player = MinecraftClient.getInstance().player;
            if(duration < totalDuration * 0.9D) { // Prevent packet spam
                if(!player.isCreative() && !player.isSpectator() && player.world.getDimension().hasSkyLight()) {
                    if(player.getBlockPos().getSquaredDistance(pos) <= maxReach * maxReach)
                        new AddEclipseEffectPacket(totalDuration).send();
                }
            }
            world.playSound(player, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 0.3F, 0.05F);
            world.playSound(player, pos, SoundEvents.BLOCK_MOSS_BREAK, SoundCategory.BLOCKS, 0.3F, 0.2F);
        }
        else {
            ticker.increment();
        }
    }

    default void applyBlindnessEffect(World world, Entity entity, int duration) {
        if (world.isClient && world.getDifficulty() != Difficulty.PEACEFUL) {
            if (entity instanceof LivingEntity livingEntity) {
                if(livingEntity instanceof PlayerEntity playerEntity) {
                    if(playerEntity.isCreative()) return;
                }
                var blindnessEffectInstance = new StatusEffectInstance(StatusEffects.BLINDNESS, duration, 0);
                livingEntity.addStatusEffect(blindnessEffectInstance);
            }
        }
    }
}
