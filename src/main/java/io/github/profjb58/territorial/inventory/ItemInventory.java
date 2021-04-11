package io.github.profjb58.territorial.inventory;

import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;

public class ItemInventory implements BaseInventory {

    private final DefaultedList<ItemStack> inventory;
    private final ItemStack attachedItemStack;

    public ItemInventory(ItemStack attachedItemStack, int size) {
        this.inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
        this.attachedItemStack = attachedItemStack;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void markDirty() {
        CompoundTag stackTag = attachedItemStack.getOrCreateTag();
        Inventories.toTag(stackTag, getItems());
    }

    public void loadFromAttachedItemTag() {
        Inventories.fromTag(attachedItemStack.getOrCreateTag(), getItems());

        for(int i=0; i < this.size(); i++) {
            setStack(i, getItems().get(i));
        }
    }
}
