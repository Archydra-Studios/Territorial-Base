package io.github.profjb58.territorial.event.template;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;

public final class ServerWorldEvents {

    public static final Event<BeforeExplosion> BEFORE_EXPLOSION = EventFactory.createArrayBacked(BeforeExplosion.class, callbacks -> (explosion, world) -> {
        for (BeforeExplosion callback : callbacks) {
            callback.beforeExplosion(explosion, world);
        }
    });

    @FunctionalInterface
    public interface BeforeExplosion {
        void beforeExplosion(Explosion explosion, ServerWorld world);
    }

    private ServerWorldEvents() {}
}
