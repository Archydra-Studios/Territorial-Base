package io.github.profjb58.territorial.block;

import io.github.profjb58.territorial.util.MovementUtils;
import io.github.profjb58.territorial.util.TextUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static io.github.profjb58.territorial.util.TextUtils.spacer;

public class OmniscientObsidianBlock extends CryingObsidianBlock {

    public static final BooleanProperty PLACED = BooleanProperty.of("placed");

    public OmniscientObsidianBlock() {
        super(FabricBlockSettings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F).luminance((state) -> 10));
        setDefaultState(getStateManager().getDefaultState().with(PLACED, false));
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
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if(!world.isClient) {

        }
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, world, pos, stack);
        world.setBlockState(pos, state.with(PLACED, false));
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if(!world.isClient) {
            if(!player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                var random = player.getRandom();
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
        world.setBlockState(pos, state.with(PLACED, true));
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if(!player.hasStatusEffect(StatusEffects.INVISIBILITY)) player.damage(DamageSource.MAGIC, 1);
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(PLACED);
    }
}
