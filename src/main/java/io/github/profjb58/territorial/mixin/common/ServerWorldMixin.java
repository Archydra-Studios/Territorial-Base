package io.github.profjb58.territorial.mixin.common;

import io.github.profjb58.territorial.event.template.ServerWorldEvents;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

/*
    @Inject(method = "createExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/Explosion;collectBlocksAndDamageEntities()V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void beforeExplosion(Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType, CallbackInfoReturnable<Explosion> cir, float power, boolean createFire, Explosion.DestructionType destructionType, Explosion explosion, float power, boolean createFire, Explosion.DestructionType destructionType, Explosion explosion, Explosion explosion) {
        ServerWorldEvents.BEFORE_EXPLOSION.invoker().beforeExplosion(explosion, (ServerWorld) (Object) this);
    }*/
}
