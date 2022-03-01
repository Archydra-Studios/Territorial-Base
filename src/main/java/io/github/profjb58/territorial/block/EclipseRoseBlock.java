package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.networking.S2CPackets;
import io.github.profjb58.territorial.util.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class EclipseRoseBlock extends WitherRoseBlock {

    private static final int ECLIPSE_TRIGGER_RADIUS = 8; // Radius in which the eclipse rose can be triggered from a player
    private static final int ECLIPSE_COOLDOWN = 60; // Cool down in ticks before the eclipse state is reset

    private boolean eclipse = false; // Whether the rose is currently projecting an eclipse
    private BlockPos lastClosestPosApplied; // Closest position of the flower which last applied the eclipse effect

    private int randomDisplayTickCount = 0;
    private int cooldownTicker = 0;

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

            if (random.nextBoolean()) {
                if(randomDisplayTickCount >= 3) {
                    for(int i=0 ; i < 5; i++) {
                        // Generate larger particle effects
                        world.addParticle(ParticleTypes.SQUID_INK,
                                x + random.nextDouble() / 5.0D,
                                (double)pos.getY() + (1.5D - random.nextDouble()),
                                z + random.nextDouble() / 5.0D,
                                0.0D, 0.5D, 0.0D);
                    }
                    eclipseDisplayTick(world, immutablePos);
                    randomDisplayTickCount = 0;
                }
                else {
                    randomDisplayTickCount++;
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void eclipseDisplayTick(World world, BlockPos pos) {
        var minPos = pos.add(-ECLIPSE_TRIGGER_RADIUS, -ECLIPSE_TRIGGER_RADIUS, -ECLIPSE_TRIGGER_RADIUS);
        var maxPos = pos.add(ECLIPSE_TRIGGER_RADIUS, ECLIPSE_TRIGGER_RADIUS, ECLIPSE_TRIGGER_RADIUS);

        // Get surrounding entities around the block
        var entities = world.getOtherEntities(null, new Box(minPos, maxPos),
                entity -> {
                    if(entity instanceof AbstractClientPlayerEntity player) {
                        // Check if the player is in survival/adventure mode and the dimension has a sky
                        return (!player.isCreative() || !player.isSpectator()) && player.world.getDimension().hasSkyLight();
                    }
                    return false;
                });

        for(var entity : entities) {
            // We know the entity will be a player, therefore cast it
            var player = (PlayerEntity) entity;
            if(!eclipse) {
                boolean triggerEclipse = false;
                if(lastClosestPosApplied == null) {
                    triggerEclipse = true;
                }
                else {
                    var squaredDistance = player.getBlockPos().getSquaredDistance(pos);
                    var prevSquaredDistance = player.getBlockPos().getSquaredDistance(lastClosestPosApplied);

                    if(squaredDistance <= prevSquaredDistance) {
                        triggerEclipse = true;
                    }
                }
                if(triggerEclipse) {
                    lastClosestPosApplied = pos;
                    eclipse = true;
                }
            }
            return;
        }
    }

    @Environment(EnvType.CLIENT)
    public void eclipsePhaseTick(ClientWorld clientWorld) {
        // Trigger an eclipse if a player is detected nearby an eclipse rose block
        if(eclipse) {
            if(cooldownTicker > 0) {
                cooldownTicker--;
                if(cooldownTicker == 0) {
                    eclipse = false;
                    return;
                }
            }
            long timeOfDay = clientWorld.getTimeOfDay();

            // Gradually move the sun or moon across the sky
            if(timeOfDay < 17950) {
                clientWorld.setTimeOfDay(timeOfDay + 80);
            }
            else if(timeOfDay > 18050) {
                clientWorld.setTimeOfDay(timeOfDay - 80);
            }
            else {
                clientWorld.setTimeOfDay(18000);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void eclipseRadiusCheckTick() {
        // If the eclipse phase is triggered check if the client entity is still near a rose
        if(eclipse) {
            var player = MinecraftClient.getInstance().player;
            if(player != null && lastClosestPosApplied != null) {
                int[] distanceVec = new int[3];
                distanceVec[0] = Math.abs(player.getBlockPos().getX() - lastClosestPosApplied.getX());
                distanceVec[1] = Math.abs(player.getBlockPos().getY() - lastClosestPosApplied.getY());
                distanceVec[2] = Math.abs(player.getBlockPos().getZ() - lastClosestPosApplied.getZ());

                for(int distance : distanceVec) {
                    // If player is outside the eclipse effect radius remove the eclipse lighting effect instantly
                    if (distance > ECLIPSE_TRIGGER_RADIUS) {
                        if (cooldownTicker == 0) {
                            cooldownTicker = ECLIPSE_COOLDOWN;
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if(!state.isOf(newState.getBlock())) {
            if(!world.isClient) {
                for(var player : PlayerLookup.around((ServerWorld) world, pos, ECLIPSE_TRIGGER_RADIUS)) {
                    ArrayList<BlockPos> eclipseRoseBlocks = MathUtils.getBlocksWithinCube((ServerWorld) world,
                            player.getBlockPos(), this, ECLIPSE_TRIGGER_RADIUS);

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
    }

    public void startEclipseCooldown() { this.cooldownTicker = ECLIPSE_COOLDOWN; }
    public boolean isEclipse() { return eclipse; }
    public void setLastClosestPosApplied(BlockPos lastClosestPosApplied) { this.lastClosestPosApplied = lastClosestPosApplied; }
}