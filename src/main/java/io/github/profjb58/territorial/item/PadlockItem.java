package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialServer;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.LockableBlock.LockType;
import io.github.profjb58.territorial.block.LockableBlock.LockSound;
import io.github.profjb58.territorial.util.ActionLogger;
import io.github.profjb58.territorial.util.SideUtils;
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

public class PadlockItem extends Item {

    private final LockType type;

    public PadlockItem(LockType type) {
        super(new Item.Settings().group(Territorial.BASE_GROUP).maxCount(16));
        this.type = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if(player != null) {
            if(player.isSneaking() && !ctx.getWorld().isClient()) {
                ItemStack lock = player.getStackInHand(player.getActiveHand());
                LockableBlock lb = new LockableBlock(lock.getName().getString(), player.getUuid(), type, ctx.getBlockPos());
                if(!lb.getLockId().equals("") && lock.hasCustomName()) {
                    if(lb.createEntity(ctx.getWorld())) {
                        player.sendMessage(new TranslatableText("message.territorial.lock_successful"), true);
                        if(SideUtils.isDedicatedServer()) {
                            TerritorialServer.actionLogger.write(ActionLogger.LogType.INFO,
                                    ActionLogger.LogModule.LOCKS,
                                    player.getName().getString() + " claimed block entity at: " + ctx.getBlockPos());
                        }
                        lb.playSound(LockSound.LOCK_ADDED, player.getEntityWorld());
                    }
                    else {
                        player.sendMessage(new TranslatableText("message.territorial.lock_failed"), true);
                        lb.playSound(LockSound.DENIED_ENTRY, player.getEntityWorld());
                    }
                }
                else {
                    player.sendMessage(new TranslatableText("message.territorial.lock_unnamed"), true);
                    lb.playSound(LockSound.DENIED_ENTRY, player.getEntityWorld());
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
    }


}
