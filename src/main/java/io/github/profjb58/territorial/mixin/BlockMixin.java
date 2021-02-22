package io.github.profjb58.territorial.mixin;

import io.github.profjb58.territorial.event.template.ServerWorldEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(at = @At("HEAD"), method = "onBreak")
    public void onBreakServer(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci)
    {
        if(!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            ServerPlayerEntity spe = (ServerPlayerEntity) player;
            ServerWorldEvents.ON_BLOCK_BREAK.invoker().onBlockBreak(serverWorld, pos, spe);
        }
    }
}
