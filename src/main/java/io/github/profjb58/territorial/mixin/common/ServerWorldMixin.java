package io.github.profjb58.territorial.mixin.common;

import io.github.profjb58.territorial.event.template.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

/*
    @Inject(method = "createExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/Explosion;collectBlocksAndDamageEntities()V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void beforeExplosion(Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType, CallbackInfoReturnable<Explosion> cir, float power, boolean createFire, Explosion.DestructionType destructionType, Explosion explosion, float power, boolean createFire, Explosion.DestructionType destructionType, Explosion explosion, Explosion explosion) {
        ServerWorldEvents.BEFORE_EXPLOSION.invoker().beforeExplosion(explosion, (ServerWorld) (Object) this);
    }*/

    @Inject(method = "saveLevel", at = @At("TAIL"))
    public void saveLevel(CallbackInfo ci) {
        ServerWorldEvents.SAVE_LEVEL.invoker().onSaveLevel();
    }
}
