package io.github.profjb58.territorial.client.gui;

import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.inventory.ItemInventory;
import io.github.profjb58.territorial.item.KeyItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class KeyringScreenHandler extends BaseScreenHandler {

    private Item prevSlotClickItem = Blocks.AIR.asItem();

    // Called on the client
    public KeyringScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, buf.readItemStack()); // Use the item stacks contents instead of a block pos
    }

    // Called on the server
    public KeyringScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack keyringStack) {
        super(TerritorialRegistry.KEYRING_SCREEN_HANDLER_TYPE, syncId, 9);
        createScreen(playerInventory, keyringStack);
    }

    @Override
    public void onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        if(slotId >= 0) {
            var itemStack = getSlot(slotId).getStack();
            var item = itemStack.getItem();

            if(actionType == SlotActionType.QUICK_MOVE) {
                if(!(item instanceof KeyItem)) return;
            }
            else {
                if(!(item instanceof KeyItem) && !(prevSlotClickItem instanceof KeyItem)) {
                    prevSlotClickItem = item;
                    if(slotId < inventorySize) {
                        return;
                    }
                }
            }
            prevSlotClickItem = item;
        }
        super.onSlotClick(slotId, clickData, actionType, playerEntity);
    }

    @Override
    void createScreen(PlayerInventory playerInventory, ItemStack keyringStack) {
        var itemInventory = new ItemInventory(keyringStack, 9);
        itemInventory.loadFromAttachedItemTag();

        // Close similarity to the Hopper Screen
        int i;
        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(itemInventory, i, 8 + i * 18, 20));
        }
        for(i = 0; i < 3; ++i) {
            for(int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, i * 18 + 51));
            }
        }
        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 109));
        }
    }
}
