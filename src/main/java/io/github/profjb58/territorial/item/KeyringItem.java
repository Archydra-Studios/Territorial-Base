package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.client.gui.KeyringScreenHandler;
import io.github.profjb58.territorial.inventory.ItemInventory;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KeyringItem extends Item {

    public KeyringItem() {
        super(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);

        if(user.world != null && !user.world.isClient) {
            user.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
                    packetByteBuf.writeItemStack(itemStack); // Pass stack data to the inventory
                }

                @Override
                public Text getDisplayName() {
                    return new TranslatableText(itemStack.getItem().getTranslationKey());
                }

                @Override
                public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new KeyringScreenHandler(syncId, inv, itemStack);
                }
            });
        }
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        ItemInventory itemInventory = new ItemInventory(stack, 9);
        itemInventory.loadFromAttachedItemTag();

        for(ItemStack itemStack : itemInventory.getItems()) {
            if (itemStack.getItem() instanceof KeyItem) {
                KeyItem keyItem = (KeyItem) itemStack.getItem();
                if (keyItem.isMasterKey()) {
                    tooltip.add(new LiteralText("§d" + itemStack.getName().getString()));
                } else {
                    tooltip.add(new LiteralText("§7" + itemStack.getName().getString()));
                }
            }
        }
    }
}
