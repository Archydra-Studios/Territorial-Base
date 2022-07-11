package io.github.profjb58.territorial.mixin.common;

import io.github.profjb58.territorial.misc.access.ServerPlayerInteractionManagerAccessor;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin implements ServerPlayerInteractionManagerAccessor {

    @Shadow
    private int tickCounter;

    @Shadow
    private int blockBreakingProgress;

    public void territorial$resetBlockBreakingProgress() {
        blockBreakingProgress = -1;
    }

    public void territorial$resetTickCounter() { }
}
