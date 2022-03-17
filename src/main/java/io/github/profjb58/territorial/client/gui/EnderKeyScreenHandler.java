package io.github.profjb58.territorial.client.gui;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import io.github.profjb58.territorial.util.TickCounter;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.profjb58.territorial.Territorial.getConfig;

public class EnderKeyScreenHandler extends GenericContainerScreenHandler {

    private static final int INVENTORY_SIZE = 27;

    private final EnderChestInventory enderChestInventory;
    private final Queue<Integer> slotDestructionQueue;
    private final ServerPlayerEntity targetPlayer;
    private final TickCounter tickCounter = new TickCounter(2);

    public EnderKeyScreenHandler(int syncId, PlayerInventory playerInventory, Inventory displayInventory,
                                 EnderChestInventory enderChestInventory, ServerPlayerEntity targetPlayer) {
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, displayInventory, 3);
        this.enderChestInventory = enderChestInventory;
        this.targetPlayer = targetPlayer;

        var slotDestructionList = new LinkedList<Integer>();
        for(int i=0; i < 27; i++) {
            slotDestructionList.add(i);
        }
        Collections.shuffle(slotDestructionList);
        slotDestructionQueue = slotDestructionList;

        createScreen(displayInventory, enderChestInventory);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        player.playSound(SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, player.getWorld().random.nextFloat() * 0.1F + 0.9F);
    }

    private void createScreen(Inventory displayInv, EnderChestInventory enderChestInv) {

        // Fill with a random limited assortment of items from the ender chest
        var random = new Random();
        for (int i = 0; i < getConfig().getEnderKeyRolls(); i++) {
            int randomSlot = random.nextInt(27);
            displayInv.setStack(randomSlot, enderChestInv.getStack(randomSlot));
        }
        // Fill the rest with 'blanks' (glass panes)
        for(int i= 0; i < 27; i++) {
            var invItemStack = displayInv.getStack(i);
            if (invItemStack.isEmpty()) {
                var blankItemStack = new ItemStack(TerritorialRegistry.BLANK_SLOT)
                        .setCustomName(new LiteralText("§k" + enderChestInv.getStack(i).getName().getString()));
                displayInv.setStack(i, blankItemStack);
            }
        }
    }

    public void tick() {
        if(tickCounter.test() && !slotDestructionQueue.isEmpty()) {
            destructSlot(slotDestructionQueue.remove());
        }
        tickCounter.increment();
    }

    public void destructSlot(int slotId) {
        var destructionStack = new ItemStack(TerritorialRegistry.DESTRUCTED_SLOT);
        destructionStack.setCustomName(new LiteralText("§k" + enderChestInventory.getStack(slotId).getName().getString()));
        getInventory().setStack(slotId, destructionStack);
    }

    @Override
    public void onSlotClick(int slotId, int clickData, SlotActionType actionType, PlayerEntity playerEntity) {
        if(slotId >= 0) {
            var itemStack = getSlot(slotId).getStack();
            var item = itemStack.getItem();

            boolean blankItem = item.equals(TerritorialRegistry.BLANK_SLOT)
                    || item.equals(TerritorialRegistry.DESTRUCTED_SLOT);
            if(slotId < INVENTORY_SIZE) {
                if(blankItem) return;
                else warnTargetPlayer();
            }
        }
        super.onSlotClick(slotId, clickData, actionType, playerEntity);
    }

    private void warnTargetPlayer() {
        if(targetPlayer != null) {
            targetPlayer.sendMessage(new TranslatableText("message.territorial.enderchest.warn"), false);
        }
    }

}
