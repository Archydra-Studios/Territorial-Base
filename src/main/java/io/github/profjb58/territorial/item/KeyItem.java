package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialServer;
import io.github.profjb58.territorial.api.event.common.LockableBlockEvents;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.entity.LockableBlockEntity;
import io.github.profjb58.territorial.util.TextUtils;
import io.github.profjb58.territorial.util.ActionLogger;
import io.github.profjb58.territorial.api.event.common.LockableBlockEvents.InteractionType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;

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

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.FAIL; // Transfer logic to UseBlockHandler event class
    }

    public ActionResult useOnBlockWhileSneaking(ServerPlayerEntity player, ServerWorld world, BlockHitResult hitResult) {
        var lbe = new LockableBlockEntity(world, hitResult.getBlockPos());
        if(lbe.exists()) {
            var lb = lbe.getBlock();
            if(masterKey) onUseMasterKey(player.getStackInHand(player.getActiveHand()), player, lb);
            if(lb.findMatchingKey(player, false).getLeft() != null) { // Matching key found
                boolean cancelAction = LockableBlockEvents.INTERACT.invoker().interact(lb, player, InteractionType.REMOVED_PADLOCK);
                if(!cancelAction) {
                    if(lbe.remove()) {
                        onRemoveLock(player, world, hitResult.getBlockPos(), lb); // Remove the lock
                        //WorldLockStorage.get((ServerWorld) ctx.getWorld()).removeLock(lb); // Remove from persistent storage
                        return ActionResult.PASS;
                    }
                    else Territorial.LOGGER.error("Key failed to remove NBT lock data at: " + lb.blockEntitySourcePos().toShortString() + " :(. Please report this as an issue");
                }
            }
            else player.sendMessage(new TranslatableText("message.territorial.wrong_key"), true);
            LockableBlockEvents.INTERACT.invoker().interact(lb, player, InteractionType.FAILED_REMOVE_PADLOCK);
        }
        else player.sendMessage(new TranslatableText("message.territorial.no_lock"), true);
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if(masterKey)
            tooltip.add(new TranslatableText("tooltip.territorial.master_key"));
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
            else tooltip.add(new TranslatableText("tooltip.territorial.key_unnamed"));
        }
    }

    public boolean onUseMasterKey(ItemStack masterKeyStack, ServerPlayerEntity player, LockableBlock lb) {
        masterKeyStack.decrement(1);
        player.sendMessage(new TranslatableText("message.territorial.master_key_vanished"), false);

        if(Territorial.isDedicatedServer()) {
            TerritorialServer.actionLogger.write(ActionLogger.LogType.INFO, ActionLogger.LogModule.LOCKS,
                    "Player " + player.getName().getString() + " used a master key at location " + lb.selfPos());
        }
        return LockableBlockEvents.INTERACT.invoker().interact(lb, player, LockableBlockEvents.InteractionType.OPEN_MASTER_KEY);
    }

    void onRemoveLock(ServerPlayerEntity player, ServerWorld world, BlockPos pos, LockableBlock lb) {
        var padlockStack = lb.getLockItemStack();
        world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), padlockStack));
        player.sendMessage(new TranslatableText("message.territorial.lock_removed"), true);
    }

    public boolean isMasterKey() { return masterKey; }
}
