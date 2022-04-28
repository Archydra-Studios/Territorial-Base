package io.github.profjb58.territorial.client.gui;

import io.github.profjb58.territorial.screen.BaseBeaconScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class BoundaryBeaconScreen extends BaseBeaconScreen {

    public BoundaryBeaconScreen(BaseBeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
}
