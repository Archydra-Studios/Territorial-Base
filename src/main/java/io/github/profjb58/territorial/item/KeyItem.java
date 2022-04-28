package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialServer;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.util.TextUtils;
import io.github.profjb58.territorial.util.ActionLogger;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.profjb58.territorial.util.TextUtils.spacer;

public class KeyItem extends Item {

    private final boolean masterKey;

    public KeyItem(boolean masterKey) {
        super(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(1));
        this.masterKey = masterKey;
    }

    // TODO - Potential key cloning mechanic
    /*@Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        if(!ctx.getWorld().isClient && ctx.getPlayer() != null) {
            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getPlayer();
            if(player.isSneaking()) {
                ServerWorld world = (ServerWorld) ctx.getWorld();
                LockableBlockEntity lbe = new LockableBlockEntity(world, ctx.getBlockPos());
                if(lbe.exists()) {
                    if(lbe.getBlock().getLockOwnerUuid().equals(player.getUuid())) {
                        ItemStack key = player.getStackInHand(player.getActiveHand());
                        key.setCustomName(new LiteralText(lbe.getBlock().getLockId()));
                        player.sendMessage(new TranslatableText("message.territorial.key_copy_success"), true);
                    }
                    else {
                        player.sendMessage(new TranslatableText("message.territorial.key_copy_fail"), true);
                    }
                    TerritorialClient.lockableHud.ignoreCycle();
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.PASS;
    }*/

    // Shift click functionality
    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        var player = ctx.getPlayer();
        if (player != null && !ctx.getWorld().isClient && player.isSneaking()) {
            var lbe = new LockableBlockEntity(ctx.getWorld(), ctx.getBlockPos());
            if(lbe.exists()) {
                var lb = lbe.getBlock();
                if(lb.findMatchingKey((ServerPlayerEntity) player, false) != null) {
                    if(lbe.remove()) {
                        onRemoveLock(ctx, lb); // Remove the lock
                        //WorldLockStorage.get((ServerWorld) ctx.getWorld()).removeLock(lb); // Remove from persistent storage
                    }
                    else {
                        // Really shouldn't happen, but just encase
                        Territorial.LOGGER.error("Lockpick failed to remove NBT lock data :(. Please report this as an issue");
                    }
                }
                if(masterKey) onUseMasterKey(player.getStackInHand(player.getActiveHand()), (ServerPlayerEntity) player, lb);
            }
            else {
                ctx.getPlayer().sendMessage(new TranslatableText("message.territorial.no_lock"), true);
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if(masterKey) {
            tooltip.add(new TranslatableText("tooltip.territorial.master_key"));
        }
        else {
            if(stack.hasCustomName()) {
                tooltip.add(new TranslatableText("tooltip.territorial.shift"));

                if(Screen.hasShiftDown()) {
                    tooltip.add(spacer());
                    TextUtils.ToolTip.addMultilineText(tooltip, "tooltip.territorial.key_shift", 4);
                    tooltip.add(spacer());
                    TextUtils.ToolTip.addMultilineText(tooltip, "tooltip.territorial.key_shift", 2, 4);
                }
            }
            else {
                tooltip.add(new TranslatableText("tooltip.territorial.key_unnamed"));
            }
        }
    }

    public void onUseMasterKey(ItemStack masterKeyStack, ServerPlayerEntity player, LockableBlock lb) {
        masterKeyStack.decrement(1);
        player.sendMessage(new TranslatableText("message.territorial.master_key_vanished"), false);

        if(Territorial.isDedicatedServer()) {
            TerritorialServer.actionLogger.write(ActionLogger.LogType.INFO, ActionLogger.LogModule.LOCKS,
                    "Player " + player.getName().getString() + " used a master key at location " + lb.blockPos());
        }
    }

    void onRemoveLock(ItemUsageContext ctx, LockableBlock lb) {
        var player = (ServerPlayerEntity) ctx.getPlayer();
        if (player != null) {
            ItemStack padlockStack = lb.getLockItemStack(1);
            BlockPos pos = ctx.getBlockPos();
            ctx.getWorld().spawnEntity(new ItemEntity(ctx.getWorld(), pos.getX(), pos.getY(), pos.getZ(), padlockStack));
            ctx.getPlayer().sendMessage(new TranslatableText("message.territorial.lock_removed"), true);
        }
    }

    public boolean isMasterKey() { return masterKey; }
}
