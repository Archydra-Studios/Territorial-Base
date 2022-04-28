package io.github.profjb58.territorial.mixin.common;

import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface ExplosionAccessor {
    @Accessor("power")
    float getPower();
}
