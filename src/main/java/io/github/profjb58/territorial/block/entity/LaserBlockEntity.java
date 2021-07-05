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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.*;

public class  LaserBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

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

        // Laser beam modifications
        mods.put("sparkle", false);
        mods.put("rainbow", false);
        mods.put("highlight", false);
        mods.put("light", false);
        mods.put("death", false);
    }

    public void createFromLens(ItemStack stack) {
        NbtCompound tag = stack.getSubTag("beam");
        if(tag != null) {
            setStrength(tag.getByte("strength"));
            setColour(tag.getInt("colour"));
            assignMods(Map.of(
                    "rainbow", tag.getBoolean("rainbow"),
                    "sparkle", tag.getBoolean("sparkle"),
                    "death", tag.getBoolean("death"),
                    "highlight", tag.getBoolean("highlight"),
                    "light", tag.getBoolean("light")
            ));
            markDirty();
            sync();
        }
    }

    public ItemStack getLensStack() {
        ItemStack lensStack = TerritorialRegistry.LENS.getDefaultStack();
        lensStack.putSubTag("beam", writeNbt(new NbtCompound()));
        return lensStack;
    }

    public static void tick(World world, BlockPos pos, BlockState state, LaserBlockEntity be) {
        if(state.get(Properties.POWER) == 0) return;

        if(tickCounter >= TICK_UPDATE_RATE) {
            tickCounter = 0;
            beamTick(world, pos, state, be);
        }
        tickCounter++;
        if(!world.isClient) {
            effectsTick((ServerWorld) world, pos, state, be);
        }
    }

    private static void beamTick(World world, BlockPos pos, BlockState state, LaserBlockEntity be) {
        Direction facing = state.get(Properties.FACING);
        BlockPos posIterator;
        BlockState bs;

        for(int i = 0; i < be.maxReach; i++) {
            posIterator = pos.offset(facing, i);
            bs = world.getBlockState(posIterator);

            if(be.mods.get("light") && !world.isClient && bs.isAir()) {
                world.setBlockState(posIterator, Blocks.LIGHT.getDefaultState());
            }
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

    private static void effectsTick(ServerWorld serverWorld, BlockPos pos, BlockState state, LaserBlockEntity be) {
        if(be.strength > 0 || be.mods.get("death") || be.mods.get("highlight")) {
            int power = state.get(Properties.POWER);

            if(be.reach != be.prevReach || power != be.prevPower) { // if the reach distance changes adjust the hitbox
                be.prevReach = be.reach;
                be.prevPower = power;
                Direction facing = state.get(Properties.FACING);

                be.startPos = Vec3d.ofCenter(pos)
                        .add(PosUtils.zeroMove(Vec3d.of(facing.getVector()).multiply(0.5), SIGNAL_STRENGTH_WIDTHS[power - 1] / 2));
                be.endPos = Vec3d.ofCenter(pos)
                        .add(PosUtils.zeroMove(Vec3d.of(facing.getVector().multiply(be.reach)), -(SIGNAL_STRENGTH_WIDTHS[power - 1]/ 2)));
            }

            if(be.startPos != null && be.endPos != null) {
                List<Entity> entities = serverWorld.getOtherEntities(null, new Box(be.startPos, be.endPos), entity -> {
                    if(Territorial.getConfig().laserTargetsAllMobs()) return true;
                    else return entity.isPlayer();
                });
                applyEffects(entities, state.get(Properties.FACING), be);
            }
        }
    }

    private static void applyEffects(List<Entity> entities, Direction facing, LaserBlockEntity be) {
        Item armorItem;
        int numArmorPieces;
        boolean hitsLowerBody, hasGoldHelmet;

        for(Entity entity : entities) {
            numArmorPieces = 0;
            hasGoldHelmet = false;
            hitsLowerBody = entity.getBlockY() == be.getPos().getY();

            // Check for gold (reflective) armor
            for(ItemStack armorStack : entity.getArmorItems()) {
                armorItem = armorStack.getItem();

                if(entity instanceof HorseEntity && armorItem == Items.GOLDEN_HORSE_ARMOR
                        || (facing == Direction.UP && armorItem == Items.GOLDEN_BOOTS)
                        || (facing == Direction.DOWN && armorItem == Items.GOLDEN_HELMET)) return;
                else {
                    if(hitsLowerBody) {
                        if(armorItem == Items.GOLDEN_LEGGINGS || armorItem == Items.GOLDEN_BOOTS) numArmorPieces++;
                    }
                    else {
                        if(armorItem == Items.GOLDEN_CHESTPLATE || armorItem == Items.GOLDEN_HELMET) numArmorPieces++;
                    }
                    if(numArmorPieces == 2) return;
                }
                if(armorItem == Items.GOLDEN_HELMET) hasGoldHelmet = true;
            }

            if(entity.isPlayer() && be.strength > 0) {
                if(!hasGoldHelmet)
                    ((PlayerEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200 * be.strength));
            }
            if(be.strength > 1) {
                entity.setOnFireFor(3);
            }
            if(be.mods.get("highlight")) {
                if(entity.isLiving()) {
                    ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 810));
                }
            }
            if(be.mods.get("death")) {
                entity.damage(DamageSource.GENERIC, 4.0f);
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
    public void assignMods(Map<String, Boolean> mods) { this.mods = mods; }
    public void incrementSparkleDistance() { sparkleDistance += 0.3f; }
    public void resetSparkleDistance() { sparkleDistance = 0; }

    public int getReach() { return reach; }
    public int getMaxReach() { return maxReach; }
    public float getSparkleDistance() { return sparkleDistance; }
    public DyeColor getColour() { return DyeColor.byId(colour); }
    public Map<String, Boolean> getMods() { return mods; }
}
