package io.github.profjb58.territorial.event.template;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;

public final class ServerWorldEvents {

    public static final Event<BeforeExplosion> BEFORE_EXPLOSION = EventFactory.createArrayBacked(BeforeExplosion.class, callbacks -> (explosion, world) -> {
        for (BeforeExplosion callback : callbacks)
            callback.beforeExplosion(explosion, world);
    });

    public static final Event<SaveLevel> SAVE_LEVEL = EventFactory.createArrayBacked(SaveLevel.class, callbacks -> () -> {
        for(SaveLevel callback : callbacks)
            callback.onSaveLevel();
    });

    @FunctionalInterface
    public interface BeforeExplosion {
        void beforeExplosion(Explosion explosion, ServerWorld world);
    }

    @FunctionalInterface
    public interface SaveLevel {
        void onSaveLevel();
    }

    private ServerWorldEvents() {}
}
