package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.access.StatusEffectInstanceAccess;
import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import io.github.profjb58.territorial.event.TerritorialRegistry;
import io.github.profjb58.territorial.util.LockUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class C2SPackets {

    // Packet to notify the server that a player is breaking a block
    public static final Identifier CLIENT_ATTACK_BLOCK = new Identifier(Territorial.MOD_ID, "client_attack_block");

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(CLIENT_ATTACK_BLOCK, (server, player, handler, buf, responseSender) -> {
            // Target block
            BlockPos target = buf.readBlockPos();

            server.execute(() -> {
                if(player.isCreative()) return;

                BlockEntity be = player.getServerWorld().getBlockEntity(target);
                if(be != null) {
                    LockableBlockEntity lbe = new LockableBlockEntity(player.getServerWorld(), target);
                    if(lbe.exists()) {
                        if(!lbe.getLockOwner().equals(player.getUuid())) {
                            StatusEffectInstance lockFatigueInstance = new StatusEffectInstance(TerritorialRegistry.LOCK_FATIGUE, Integer.MAX_VALUE, 1, false, false, false);

                            // Notify the lock fatigue effect with the last position the effect was applied from
                            ((StatusEffectInstanceAccess) lockFatigueInstance).setLastPosApplied(target);
                            player.addStatusEffect(lockFatigueInstance);
                            return;
                        }
                    }
                }
                // Remove the effect if no block entity is found
                LockUtils.removeLockFatigueEffect(player);
            });
        });
    }
}
