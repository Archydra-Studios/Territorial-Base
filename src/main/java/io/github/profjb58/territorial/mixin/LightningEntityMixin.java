package io.github.profjb58.territorial.mixin;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {

    @Shadow
    private int ambientTick;

    public LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method="tick", at=@At("TAIL"))
    public void tick(CallbackInfo ci) {
        if(ambientTick >= 0) {
            if(!world.isClient) {
                var blockPos = getBlockPos();
                if(world.getBlockState(blockPos).getBlock() instanceof WitherRoseBlock) {
                    world.setBlockState(blockPos, TerritorialRegistry.ECLIPSE_ROSE.getDefaultState());
                }
            }
        }
    }
}
