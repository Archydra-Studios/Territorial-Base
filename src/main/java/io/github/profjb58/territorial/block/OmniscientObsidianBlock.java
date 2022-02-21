package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.util.MovementUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class OmniscientObsidianBlock extends Block {

    public OmniscientObsidianBlock() {
        super(FabricBlockSettings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F).luminance((state) -> 10));
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double x = (pos.getX() + 0.5D) + (random.nextDouble() - 0.5D);
        double y = (pos.getY() + 0.5D) + (random.nextDouble() - 0.5D);
        double z = (pos.getZ() + 0.5D) + (random.nextDouble() - 0.5D);

        double vx = random.nextDouble() - 0.5D;
        double vy = random.nextDouble() - 0.5D;
        double vz = random.nextDouble() - 0.5D;
        world.addParticle(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if(!world.isClient) {
            if(!player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                Random random = player.getRandom();
                double randomPercent = random.nextDouble();

                player.damage(DamageSource.MAGIC, random.nextInt(14 - 8) + 8);

                if(randomPercent < 0.5) {
                    Vec3f unitVec = player.getHorizontalFacing().getUnitVector();
                    player.takeKnockback(1, unitVec.getX() * (random.nextDouble(0.5) + 0.5), unitVec.getZ() * (random.nextDouble(0.5) + 0.5));
                }
                else {
                    MovementUtils.randomTeleport((ServerWorld) world, (ServerPlayerEntity) player, 0, 6, true, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT);
                }
            }
        }
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 0.5F, 0.2F, true);
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if(!player.hasStatusEffect(StatusEffects.INVISIBILITY)) player.damage(DamageSource.MAGIC, 1);
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);
    }
}
