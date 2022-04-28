package io.github.profjb58.territorial.networking;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.entity.effect.LockFatigueStatusEffect;
import io.github.profjb58.territorial.event.registry.TerritorialClientRegistry;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.world.team.ClientTeamsHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.UUID;

import static io.github.profjb58.territorial.block.EclipseBlock.onAddEclipseEffect;

public class C2SPackets {
    public static final Identifier BREAKING_BLOCK, REMOVE_TEAM, RENAME_TEAM,
            TEAM_MEMBER_ACTION;

    public static final Identifier TEAM_NETWORKING_CHANNEL;

    public static void init()
    {
        // On block starts breaking
        ServerPlayNetworking.registerGlobalReceiver(BREAKING_BLOCK, ((server, player, handler, buf, responseSender) -> {
            final BlockPos target = buf.readBlockPos();
            server.execute(() -> {
                if(!LockFatigueStatusEffect.addEffect(player, target))
                    player.removeStatusEffect(TerritorialRegistry.LOCK_FATIGUE_EFFECT);
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(REMOVE_TEAM, ((server, player, handler, buf, responseSender) -> {
            final UUID teamId = buf.readUuid();
            server.execute(() -> Territorial.TEAMS_HANDLER.onClientRemoveTeam(player, teamId));
        }));

        ServerPlayNetworking.registerGlobalReceiver(RENAME_TEAM, ((server, player, handler, buf, responseSender) -> {
            final UUID teamId = buf.readUuid();
            final String newName = buf.readString();
            server.execute(() -> Territorial.TEAMS_HANDLER.onClientRenameTeam(player, teamId, newName));
        }));

        ServerPlayNetworking.registerGlobalReceiver(TEAM_MEMBER_ACTION, ((server, player, handler, buf, responseSender) -> {

        }));
    }

    static {
        BREAKING_BLOCK = new Identifier(Territorial.MOD_ID, "breaking_block");
        REMOVE_TEAM = new Identifier(Territorial.MOD_ID, "remove_team");
        RENAME_TEAM = new Identifier(Territorial.MOD_ID, "rename_team");
        TEAM_MEMBER_ACTION = new Identifier(Territorial.MOD_ID, "team_action");

        TEAM_NETWORKING_CHANNEL = new Identifier(Territorial.MOD_ID, "team_networking_channel");
    }
}
