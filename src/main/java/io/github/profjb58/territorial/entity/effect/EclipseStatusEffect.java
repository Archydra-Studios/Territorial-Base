package io.github.profjb58.territorial.entity.effect;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.TickCounter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class EclipseStatusEffect extends StatusEffect {

    private static final int TRIGGER_RADIUS, ECLIPSE_COOLDOWN;
    private static final TickCounter COOLDOWN_TICKER;
    private BlockPos lastPosApplied;

    public EclipseStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x010316);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        World world = entity.getEntityWorld();
        if(world.isClient) {
            if (entity instanceof PlayerEntity) {
                eclipsePhaseTick((ClientWorld) world);
                eclipseRadiusCheckTick();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void eclipsePhaseTick(ClientWorld clientWorld) {
        COOLDOWN_TICKER.increment();
        if(COOLDOWN_TICKER.test()) {
            var player = MinecraftClient.getInstance().player;
            if(player != null && player.hasStatusEffect(TerritorialRegistry.ECLIPSE_EFFECT)) {
                player.removeStatusEffect(TerritorialRegistry.ECLIPSE_EFFECT);
            }
            COOLDOWN_TICKER.disable();
            return;
        }
        long timeOfDay = clientWorld.getTimeOfDay();

        // Gradually move the sun or moon across the sky
        if(timeOfDay < 17950) {
            clientWorld.setTimeOfDay(timeOfDay + 80);
        }
        else if(timeOfDay > 18050) {
            clientWorld.setTimeOfDay(timeOfDay - 80);
        }
        else { // TODO - Can probably remove this bit
            clientWorld.setTimeOfDay(18000);
        }
    }

    @Environment(EnvType.CLIENT)
    public void eclipseRadiusCheckTick() {
        // If the eclipse phase is triggered check if the client entity is still near a rose
        var player = MinecraftClient.getInstance().player;
        if(player != null && lastPosApplied != null) {

            BlockPos lastPosAppliedInt = lastPosApplied;
            int triggerRadius = TRIGGER_RADIUS * TRIGGER_RADIUS;
            double distance = player.getBlockPos().getSquaredDistance(lastPosApplied);

            // If player is outside the eclipse effect radius remove the effect
            if(player.getBlockPos().getSquaredDistance(lastPosApplied) > (TRIGGER_RADIUS * TRIGGER_RADIUS)) {
                if(!COOLDOWN_TICKER.isEnabled()) COOLDOWN_TICKER.enable();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void eclipseLogicTick(World world, BlockPos pos) {
        var minPos = pos.add(-TRIGGER_RADIUS, -TRIGGER_RADIUS, -TRIGGER_RADIUS);
        var maxPos = pos.add(TRIGGER_RADIUS, TRIGGER_RADIUS, TRIGGER_RADIUS);

        // Get surrounding entities around the block
        var entities = world.getOtherEntities(null, new Box(minPos, maxPos),
                entity -> {
                    if(entity instanceof AbstractClientPlayerEntity player) {
                        // Check if the player is in survival/adventure mode and the dimension has a sky
                        return (!player.isCreative() || !player.isSpectator()) && player.world.getDimension().hasSkyLight();
                    }
                    return false;
                });

        for(var entity : entities) {
            // We know the entity will be a player, therefore cast it
            var player = (PlayerEntity) entity;
            if(!player.hasStatusEffect(TerritorialRegistry.ECLIPSE_EFFECT)) {
                boolean triggerEclipse = false;

                if(lastPosApplied == null) triggerEclipse = true;
                else {
                    var squaredDistance = player.getBlockPos().getSquaredDistance(pos);
                    var prevSquaredDistance = player.getBlockPos().getSquaredDistance(lastPosApplied);

                    if(squaredDistance <= prevSquaredDistance) {
                        triggerEclipse = true;
                    }
                }
                if(triggerEclipse) {
                    lastPosApplied = pos;
                    player.addStatusEffect(new StatusEffectInstance(TerritorialRegistry.ECLIPSE_EFFECT, 600));
                }
            }
        }
    }

    public static void startEclipseCooldown() { COOLDOWN_TICKER.enable(); }
    public void setLastPosApplied(BlockPos lastPosApplied) { this.lastPosApplied = lastPosApplied; }

    static {
        TRIGGER_RADIUS = Territorial.getConfig().getEclipseTriggerRadius();
        ECLIPSE_COOLDOWN = 60;
        COOLDOWN_TICKER = new TickCounter(ECLIPSE_COOLDOWN);
        COOLDOWN_TICKER.disable();
    }
}
