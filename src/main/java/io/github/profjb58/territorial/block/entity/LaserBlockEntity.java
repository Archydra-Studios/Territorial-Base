package io.github.profjb58.territorial.block.entity;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.mixin.AnvilChunkStorageAccessor;
import io.github.profjb58.territorial.util.PosUtils;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaserBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    public static final float[] SIGNAL_STRENGTH_WIDTHS = { 0.001f, 0.0015f, 0.0030f, 0.0045f, 0.0070f, 0.01f, 0.0135f, 0.02f, 0.025f, 0.035f, 0.06f, 0.1f, 0.16f, 0.25f, 0.38f };
    private static final int TICK_UPDATE_RATE = 10;
    private static int tickCounter = 0;

    private int strength, colour, reach, prevReach, maxReach, prevPower;
    private float sparkleDistance = 0;
    private Vec3d startPos, endPos;
    private Map<String, Boolean> mods = new HashMap<>();

    public LaserBlockEntity(BlockPos pos, BlockState state) {
        super(TerritorialRegistry.LASER_BLOCK_ENTITY, pos, state);
        strength = 0;
        reach = 0;
        prevReach = 0;
        maxReach = Territorial.getConfig().getLaserTransmitterMaxReach();
        mods.put("sparkle", false);
        mods.put("rainbow", false);
        mods.put("highlight", false);
        mods.put("light", false);
        mods.put("death", false);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LaserBlockEntity be) {
        int power = state.get(Properties.POWER);
        if(power == 0) return;

        if(tickCounter >= TICK_UPDATE_RATE) {
            tickCounter = 0;
            Direction facing = state.get(Properties.FACING);
            BlockPos posIterator;
            BlockState bs;

            for(int i = 0; i < be.maxReach; i++) {
                posIterator = pos.offset(facing, i);
                bs = world.getBlockState(posIterator);
                if((bs.getOpacity(world, posIterator) >= 15 && !bs.isOf(Blocks.BEDROCK)) || (i == (be.maxReach -1))) {
                    be.reach = i;
                    break;
                }
            }

            if(!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;
                int watchDistance = ((AnvilChunkStorageAccessor) serverWorld.getChunkManager().threadedAnvilChunkStorage).getWatchDistance();
                int watchDistanceMaxReach = (watchDistance < 2) ? 16 : (watchDistance * 16) - 16;

                if(be.maxReach != watchDistanceMaxReach) {
                    be.maxReach = Math.min(watchDistanceMaxReach, Territorial.getConfig().getLaserTransmitterMaxReach());
                    be.markDirty();
                    be.sync();
                }
            }
        }
        tickCounter++;

        if(!world.isClient) {
            if(be.strength > 0 || be.mods.get("death") || be.mods.get("highlight")) {
                ServerWorld serverWorld = (ServerWorld) world;

                // if the reach distance changes adjust the hitbox
                if(be.reach != be.prevReach || power != be.prevPower) {
                    be.prevReach = be.reach;
                    be.prevPower = power;
                    Direction facing = state.get(Properties.FACING);

                    be.startPos = Vec3d.ofCenter(pos)
                            .add(PosUtils.zeroMove(Vec3d.of(facing.getVector()).multiply(0.5), SIGNAL_STRENGTH_WIDTHS[power - 1] / 2));
                    be.endPos = Vec3d.ofCenter(pos)
                            .add(PosUtils.zeroMove(Vec3d.of(facing.getVector().multiply(be.reach)), -(SIGNAL_STRENGTH_WIDTHS[power - 1]/ 2)));
                }

                if(be.startPos != null && be.endPos != null) {
                    List<Entity> entities = serverWorld.getOtherEntities(null, new Box(be.startPos, be.endPos));

                    for(Entity entity : entities) {
                        if(entity.isPlayer() && be.strength > 0) {
                            ((PlayerEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200));
                        }
                        if(be.strength > 1) {
                            entity.setOnFireFor(4);
                        }
                        if(be.mods.get("highlight")) {
                            entity.setGlowing(true);
                        }
                        if(be.mods.get("death")) {
                            entity.damage(DamageSource.GENERIC, 3.0f);
                        }
                    }
                }
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        toSharedTag(tag);
        tag.putByte("strength", (byte) strength);
        tag.putBoolean("highlight", mods.get("highlight"));
        tag.putBoolean("death", mods.get("death"));
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        fromSharedTag(tag);
        strength = tag.getByte("strength");
        mods.put("highlight", tag.getBoolean("highlight"));
        mods.put("death", tag.getBoolean("death"));
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return toSharedTag(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        fromSharedTag(tag);
    }

    private NbtCompound toSharedTag(NbtCompound tag) {
        tag.putInt("reach", reach);
        tag.putInt("colour", colour);
        tag.putInt("max_reach", maxReach);
        tag.putBoolean("rainbow", mods.get("rainbow"));
        tag.putBoolean("sparkle", mods.get("sparkle"));
        tag.putBoolean("light", mods.get("light"));
        return tag;
    }

    private void fromSharedTag(NbtCompound tag) {
        reach = tag.getInt("reach");
        colour = tag.getInt("colour");
        maxReach = tag.getInt("max_reach");
        mods.put("rainbow", tag.getBoolean("rainbow"));
        mods.put("sparkle", tag.getBoolean("sparkle"));
        mods.put("light", tag.getBoolean("light"));
    }

    public void setStrength(int strength) { this.strength = strength; }
    public void setColour(int colour) { this.colour = colour; }
    public void assignMods(Map<String, Boolean> mods) {
        this.mods = mods;
    }
    public void incrementSparkleDistance() { sparkleDistance += 0.3f; }
    public void resetSparkleDistance() { sparkleDistance = 0; }

    public int getReach() { return reach; }
    public int getMaxReach() { return maxReach; }
    public float getSparkleDistance() { return sparkleDistance; }
    public DyeColor getColour() { return DyeColor.byId(colour); }
    public Map<String, Boolean> getMods() { return mods; }
}
