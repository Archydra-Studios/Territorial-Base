package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.TerritorialServer;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.LockableBlock.LockType;
import io.github.profjb58.territorial.util.SideUtils;
import io.github.profjb58.territorial.util.debug.ActionLogger;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.profjb58.territorial.util.TextUtils.spacer;

public class PadlockItem extends Item {

    private final LockType type;

    public PadlockItem(LockType type) {
        super(new Item.Settings().group(Territorial.BASE_GROUP).maxCount(16));
        this.type = type;
    }

    // Shift click functionality
    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if(player != null) {
            if(player.isSneaking()) {
                if(ctx.getWorld().isClient) {
                    TerritorialClient.lockableHud.ignoreCycle();
                }
                else {
                    ItemStack lock = player.getStackInHand(player.getActiveHand());
                    LockableBlock lb = new LockableBlock(
                            lock.getName().getString(),
                            player.getUuid(),
                            player.getName().getString(),
                            type,
                            ctx.getBlockPos());

                    if(!lb.getLockId().equals("") && lock.hasCustomName()) {
                        switch (lb.createEntity(ctx.getWorld())) {
                            case SUCCESS -> {
                                if (!player.isCreative()) {
                                    lock.decrement(1);
                                }
                                player.sendMessage(new TranslatableText("message.territorial.lock_successful"), true);
                                lb.playSound(LockableBlock.LockSound.LOCK_ADDED, player.getEntityWorld());
                                if (SideUtils.isDedicatedServer()) {
                                    TerritorialServer.actionLogger.write(ActionLogger.LogType.INFO,
                                            ActionLogger.LogModule.LOCKS,
                                            player.getName().getString() + " claimed block entity at: " + ctx.getBlockPos());
                                }
                            }
                            case FAIL -> {
                                player.sendMessage(new TranslatableText("message.territorial.lock_failed"), true);
                                lb.playSound(LockableBlock.LockSound.DENIED_ENTRY, player.getEntityWorld());
                                return ActionResult.FAIL;
                            }
                            case NO_ENTITY_EXISTS -> player.sendMessage(new TranslatableText("message.territorial.lock_not_lockable"), true);
                        }
                    }
                    else {
                        player.sendMessage(new TranslatableText("message.territorial.lock_unnamed"), true);
                        lb.playSound(LockableBlock.LockSound.DENIED_ENTRY, player.getEntityWorld());
                        return ActionResult.FAIL;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if(stack.hasCustomName()) {
            tooltip.add(new TranslatableText("tooltip.territorial.shift"));
            if(Screen.hasShiftDown()) {
                tooltip.add(spacer());
                tooltip.add(new TranslatableText("tooltip.territorial.padlock_shift"));
            }
        }
        else {
            tooltip.add(new TranslatableText("tooltip.territorial.padlock_unnamed"));
        }
    }
}
