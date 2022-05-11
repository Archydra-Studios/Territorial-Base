package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.TerritorialServer;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.block.enums.LockSound;
import io.github.profjb58.territorial.block.enums.LockType;
import io.github.profjb58.territorial.util.ActionLogger;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
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

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.FAIL; // Transfer logic to UseBlockHandler event class
    }

    public ActionResult useOnBlock(PlayerEntity player, ServerWorld world, BlockHitResult hitResult) {
        if(player != null && !world.isClient) {
            var lockStack = player.getStackInHand(player.getActiveHand());
            var lb = new LockableBlock(
                    lockStack.getName().getString(),
                    player.getUuid(),
                    player.getName().getString(),
                    type,
                    hitResult.getBlockPos());

            if (!lb.lockId().equals("") && lockStack.hasCustomName()) {
                switch (lb.createEntity(world)) {
                    case SUCCESS -> {
                        if (!player.isCreative()) lockStack.decrement(1);
                        player.sendMessage(new TranslatableText("message.territorial.lock_successful"), true);
                        lb.playSound(LockSound.LOCK_ADDED, player.getEntityWorld());
                    }
                    case FAIL -> {
                        player.sendMessage(new TranslatableText("message.territorial.lock_failed"), true);
                        lb.playSound(LockSound.DENIED_ENTRY, player.getEntityWorld());
                        return ActionResult.FAIL;
                    }
                    case NO_ENTITY_EXISTS, BLACKLISTED -> player.sendMessage(new TranslatableText("message.territorial.lock_not_lockable"), true);
                }
            } else {
                player.sendMessage(new TranslatableText("message.territorial.lock_unnamed"), true);
                lb.playSound(LockSound.DENIED_ENTRY, player.getEntityWorld());
                return ActionResult.FAIL;
            }
        }
        return ActionResult.CONSUME;
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
