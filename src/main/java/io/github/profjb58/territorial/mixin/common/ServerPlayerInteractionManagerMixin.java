package io.github.profjb58.territorial.mixin.common;

import io.github.profjb58.territorial.misc.access.ServerPlayerInteractionManagerAccessor;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin implements ServerPlayerInteractionManagerAccessor {
    @Shadow
    int blockBreakingProgress;

    public void territorial$resetBlockBreakingProgress() {
        blockBreakingProgress = -1;
    }
}
