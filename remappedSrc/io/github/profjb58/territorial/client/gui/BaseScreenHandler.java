package io.github.profjb58.territorial.client.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;

public abstract class BaseScreenHandler extends ScreenHandler {
    int inventorySize;

    protected BaseScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, int inventorySize) {
        super(type, syncId);
        this.inventorySize = inventorySize;
    }

    abstract void createScreen(PlayerInventory player, ItemStack itemStack);

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack toInsert = slot.getStack();
            itemStack = toInsert.copy();
            if (index < inventorySize) {
                if (!this.insertItem(toInsert, inventorySize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(toInsert, 0, inventorySize, false)) {
                return ItemStack.EMPTY;
            }

            if (toInsert.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
