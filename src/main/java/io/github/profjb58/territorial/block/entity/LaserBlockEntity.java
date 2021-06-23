package io.github.profjb58.territorial.block.entity;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.TagUtils;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class LaserBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private final int MAX_DISTANCE;
    private int strength, colour;
    private BlockPos startPos, endPos;
    private Map<String, Boolean> mods = new HashMap<>();

    public LaserBlockEntity(BlockPos pos, BlockState state) {
        super(TerritorialRegistry.LASER_BLOCK_ENTITY, pos, state);

        MAX_DISTANCE = Territorial.getConfig().getLaserTransmitterMaxReach();
        strength = 0;
        mods.put("sparkle", false);
        mods.put("rainbow", false);
        mods.put("highlight", false);
        mods.put("light", false);
        mods.put("death", false);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, LaserBlockEntity be) {

        /*if (tickCounter >= TICK_COUNTER_UPDATE) {
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
        tickCounter++;*/
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        tag.putByte("strength", (byte) strength);
        tag.putInt("colour", colour);

        if(startPos != null && endPos != null) {
            tag.putIntArray("start_pos", TagUtils.serializeBlockPos(startPos));
            tag.putIntArray("end_pos", TagUtils.serializeBlockPos(endPos));
        }

        for(Map.Entry<String, Boolean> entry : mods.entrySet()) {
            tag.putBoolean(entry.getKey(), entry.getValue());
        }
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        strength = tag.getByte("strength");
        colour = tag.getInt("colour");

        if(startPos != null && endPos != null) {
            startPos = TagUtils.deserializeBlockPos(tag.getIntArray("start_pos"));
            endPos = TagUtils.deserializeBlockPos(tag.getIntArray("end_pos"));
        }

        for(Map.Entry<String, Boolean> entry : mods.entrySet()) {
            entry.setValue(tag.getBoolean(entry.getKey()));
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        if(startPos != null && endPos != null) {
            tag.putIntArray("start_pos", TagUtils.serializeBlockPos(startPos));
            tag.putIntArray("end_pos", TagUtils.serializeBlockPos(endPos));
        }
        tag.putInt("colour", colour);
        tag.putBoolean("rainbow", mods.get("rainbow"));
        tag.putBoolean("sparkle", mods.get("sparkle"));
        tag.putBoolean("light", mods.get("light"));
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        if(startPos != null && endPos != null) {
            startPos = TagUtils.deserializeBlockPos(tag.getIntArray("start_pos"));
            endPos = TagUtils.deserializeBlockPos(tag.getIntArray("end_pos"));
        }
        colour = tag.getInt("colour");
        mods.put("rainbow", tag.getBoolean("rainbow"));
        mods.put("sparkle", tag.getBoolean("sparkle"));
        mods.put("light", tag.getBoolean("light"));
    }

    public void setStrength(int strength) { this.strength = strength; }
    public void setColour(int colour) { this.colour = colour; }

    public void setPosRange(BlockPos startPos, BlockPos endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public void assignMods(Map<String, Boolean> mods) {
        this.mods = mods;
    }

    public int getStrength() { return strength; }
    public DyeColor getColour() { return DyeColor.byId(colour); }

    public BlockPos getStartPos() { return startPos; }
    public BlockPos getEndPos() { return endPos; }

    public boolean isRainbow() { return mods.get("rainbow"); }
    public boolean isSparkle() { return mods.get("sparkle"); }
    public boolean isLight() { return mods.get("light"); }
}
