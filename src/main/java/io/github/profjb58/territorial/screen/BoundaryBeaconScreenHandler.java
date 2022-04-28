package io.github.profjb58.territorial.screen;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;

public class BoundaryBeaconScreenHandler extends BaseBeaconScreenHandler {

    public BoundaryBeaconScreenHandler(int syncId, Inventory inventory, PacketByteBuf buf) {
        super(syncId, inventory, buf);
    }

    public BoundaryBeaconScreenHandler(int syncId, Inventory inventory, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
        super(syncId, inventory, propertyDelegate, context);
    }
}
