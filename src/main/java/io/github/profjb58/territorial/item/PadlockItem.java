package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.util.ActionLogger;
import io.github.profjb58.territorial.util.SideUtils;
import io.github.profjb58.territorial.world.data.LocksPersistentState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class PadlockItem extends Item {

    /* Lock type codes:
    -1 = creative
     1 = iron
     2 = gold
     3 = diamond
     4 = netherite */

    int type;

    public PadlockItem(int type) {
        super(new Item.Settings().group(Territorial.BASE_GROUP).maxCount(16));
        this.type = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if(player != null && !ctx.getWorld().isClient) {
            if(player.isSneaking() && !ctx.getWorld().isClient()) {

                ItemStack lock = player.getStackInHand(player.getActiveHand());
                String lockName = lock.getName().getString();
                if(!lockName.equals("") && lock.hasCustomName()) {

                    BlockEntity be = ctx.getWorld().getBlockEntity(ctx.getBlockPos());
                    if(be != null) {
                        CompoundTag tag = be.toTag(new CompoundTag());
                        if(!tag.contains("lock_id")) { // No lock has been assigned to the block entity

                            tag.putString("lock_id", lockName);
                            tag.putUuid("lock_owner_uuid", player.getUuid());
                            tag.putInt("lock_type", type);

                            if(!player.isCreative()) {
                                player.getStackInHand(player.getActiveHand()).decrement(1);
                            }
                            player.sendMessage(new TranslatableText("message.territorial.lock_successful"), true);
                            if(SideUtils.isDedicatedServer()) {
                                Territorial.actionLogger.write(ActionLogger.LogType.INFO,
                                        ActionLogger.LogModule.LOCKS,
                                        player.getName().getString() + " claimed block entity at: " + be.getPos());
                            }
                            LocksPersistentState lps = LocksPersistentState.get((ServerWorld) ctx.getWorld());
                            lps.addLock(player.getUuid(), ctx.getBlockPos());
                        }
                        else {
                            player.sendMessage(new TranslatableText("message.territorial.lock_failed"), true);
                        }

                        try {
                            be.fromTag(be.getCachedState(), tag);
                        } catch (Exception ignored) {}

                        return ActionResult.SUCCESS;
                    }
                }
                else {
                    player.sendMessage(new TranslatableText("message.territorial.lock_unnamed"), true);
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
