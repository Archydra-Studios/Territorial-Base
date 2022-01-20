package io.github.profjb58.territorial.client.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static io.github.profjb58.territorial.Territorial.getConfig;

public class EnderKeyScreenHandler extends GenericContainerScreenHandler {

    private static final int INVENTORY_SIZE = 27;

    private final EnderChestInventory enderChestInventory;
    private final Queue<Integer> slotDestructionQueue;
    private Item prevSlotClickItem = Items.RED_STAINED_GLASS_PANE;
    private final ServerPlayerEntity targetPlayer;

    public EnderKeyScreenHandler(int syncId, PlayerInventory playerInventory, Inventory displayInventory,
                                 EnderChestInventory enderChestInventory, ServerPlayerEntity targetPlayer) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, displayInventory, 3);
        this.enderChestInventory = enderChestInventory;
        this.targetPlayer = targetPlayer;

        LinkedList<Integer> slotDestructionList = new LinkedList<>();
        for(int i=0; i < 27; i++) {
            slotDestructionList.add(i);
        }
        Collections.shuffle(slotDestructionList);
        slotDestructionQueue = slotDestructionList;

        createScreen(displayInventory, enderChestInventory);
    }

    private void createScreen(Inventory displayInv, EnderChestInventory enderChestInv) {

        // Fill with a random limited assortment of items from the ender chest
        Random random = new Random();
        for (int i = 0; i < getConfig().getEnderKeyRolls(); i++) {
            int randomSlot = random.nextInt(27);
            displayInv.setStack(randomSlot, enderChestInv.getStack(randomSlot));
        }
        // Fill the rest of the slots with 'blanks' (glass panes)
        for(int i= 0; i < 27; i++) {
            ItemStack invItemStack = displayInv.getStack(i);
            if (invItemStack.isEmpty()) {
                ItemStack blankStack = new ItemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .setCustomName(new LiteralText("§k" + enderChestInv.getStack(i).getName().getString()));
                displayInv.setStack(i, blankStack);
            }
        }
    }

    public void tick() {
        if(!slotDestructionQueue.isEmpty()) {
            destructSlot(slotDestructionQueue.remove());
        }
    }

    public void destructSlot(int slotId) {
        ItemStack destructionStack = new ItemStack(Items.RED_STAINED_GLASS_PANE);
        destructionStack.setCustomName(new LiteralText("§k" + enderChestInventory.getStack(slotId).getName().getString()));
        getInventory().setStack(slotId, destructionStack);
    }

    @Override
    public void onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        if(slotId >= 0) {
            ItemStack itemStack = getSlot(slotId).getStack();
            Item item = itemStack.getItem();

            if(actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) {
                if(slotId < INVENTORY_SIZE) {
                    boolean blankItem = item.equals(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                            || item.equals(Items.RED_STAINED_GLASS_PANE);
                    if(actionType == SlotActionType.PICKUP) {
                        prevSlotClickItem = itemStack.getItem();
                        if(blankItem) return;
                        else warnTargetPlayer();
                    }
                    else {
                        if (blankItem) return;
                        else warnTargetPlayer();
                    }
                }
                else {
                    boolean blankItem = prevSlotClickItem.equals(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
                            || prevSlotClickItem.equals(Items.RED_STAINED_GLASS_PANE);
                    if(actionType == SlotActionType.PICKUP && blankItem) {
                        return;
                    }
                    else {
                        return;
                    }
                }
            }
            else {
                return;
            }
        }
        super.onSlotClick(slotId, clickData, actionType, playerEntity);
    }

    private void warnTargetPlayer() {
        if(targetPlayer != null) {
            String playerName = targetPlayer.getDisplayName().getString();
            if(new Random().nextBoolean()) {
                playerName = "§k" + targetPlayer.getDisplayName().getString(); // Obfuscated
            }
            targetPlayer.sendMessage(new TranslatableText("message.territorial.enderchest.warn", playerName), false);
        }
    }

}
