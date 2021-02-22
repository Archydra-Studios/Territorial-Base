package io.github.profjb58.territorial.event.template;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;

public final class ServerWorldEvents {

    public static final Event<BeforeExplosion> BEFORE_EXPLOSION = EventFactory.createArrayBacked(BeforeExplosion.class, callbacks -> (explosion, world) -> {
        for (BeforeExplosion callback : callbacks) {
            callback.beforeExplosion(explosion, world);
        }
    });

    public static final Event<OnBlockBreak> ON_BLOCK_BREAK = EventFactory.createArrayBacked(OnBlockBreak.class, callbacks -> (world, pos, player) -> {
        for(OnBlockBreak callback : callbacks) {
            callback.onBlockBreak(world, pos, player);
        }
    });

    @FunctionalInterface
    public interface BeforeExplosion {
        void beforeExplosion(Explosion explosion, ServerWorld world);
    }

    @FunctionalInterface
    public interface OnBlockBreak {
        void onBlockBreak(ServerWorld world, BlockPos pos, ServerPlayerEntity spe);
    }

    private ServerWorldEvents() {}
}
