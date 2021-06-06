package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.client.gui.EnderKeyScreenHandler;
import io.github.profjb58.territorial.util.TextUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.profjb58.territorial.Territorial.getConfig;
import static io.github.profjb58.territorial.util.TextUtils.spacer;

public class EnderKeyItem extends Item {

    public EnderKeyItem() {
        super(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        if(!Territorial.getConfig().enderKeyEnabled()) {
            tooltip.add(new TranslatableText("tooltip.territorial.item_disabled"));
        }
        else if(stack.hasCustomName()) {
            tooltip.add(new TranslatableText("tooltip.territorial.shift"));

            if(Screen.hasShiftDown()) {
                tooltip.add(spacer());
                TextUtils.ToolTip.addMultilineText(tooltip, "tooltip.territorial.ender_key_shift", 3);
            }
        }
        else {
            tooltip.add(new TranslatableText("tooltip.territorial.ender_key_unnamed"));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.setCurrentHand(hand);
        ItemStack itemStack = player.getStackInHand(hand);

        if(getConfig().enderKeyEnabled() && world != null && !world.isClient) {
            PlayerManager playerManager = ((ServerWorld) world).getServer().getPlayerManager();
            String itemStackName = itemStack.getName().getString();

            if(itemStack.hasCustomName()) {
                ServerPlayerEntity target = playerManager.getPlayer(itemStackName);
                if (target != null && target.isAlive()) {
                    EnderChestInventory enderChestInv = target.getEnderChestInventory();
                    Inventory displayInv = new EnderChestInventory();

                    String targetName = target.getDisplayName().getString();
                    targetName = targetName.length() > 14 ? targetName.substring(0, 13) : targetName;

                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                            new EnderKeyScreenHandler(i, playerInventory, displayInv, enderChestInv, target),
                            new TranslatableText("container.territorial.enderchest", targetName)));

                    if(!player.isCreative()) itemStack.decrement(1);
                } else {
                    player.sendMessage(new TranslatableText("message.territorial.enderchest.unknown_player",
                            itemStackName), true);
                }
            }
            else {
                player.sendMessage(new TranslatableText("message.territorial.enderchest.key_unnamed"), true);
            }
        }
        return TypedActionResult.pass(itemStack);
    }
}
