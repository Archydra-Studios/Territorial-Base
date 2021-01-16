package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PadlockItem extends Item {

    public PadlockItem() {
        super(new Item.Settings().group(Territorial.BASE_GROUP).maxCount(16));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if(player != null && !ctx.getWorld().isClient) {
            BlockEntity be = ctx.getWorld().getBlockEntity(ctx.getBlockPos());

            if(be != null) {
                CompoundTag tag = be.toTag(new CompoundTag());
                if(!tag.contains("lock_uuid")) {
                    UUID uuid = UUID.randomUUID();

                    // Owner of the lock, used to also reference the clan they belong to
                    tag.putUuid("lock_owner_uuid", player.getUuid());
                    tag.putUuid("lock_uuid", uuid);

                    if(!player.isCreative()) {
                        player.getStackInHand(player.getActiveHand()).decrement(1);
                    }
                    player.sendMessage(new TranslatableText("message.territorial.lock_successful"), true);
                }
                else {
                    player.sendMessage(new TranslatableText("message.territorial.lock_failed"), true);
                }
                
                /*try {*/
                    be.fromTag(be.getCachedState(), tag);
                /*} catch (Exception ignored) {}*/

                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
    }
}
