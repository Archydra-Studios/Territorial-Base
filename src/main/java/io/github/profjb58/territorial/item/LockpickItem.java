package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LockpickItem extends Item {

    // TODO - Lockpick mechanics
    public LockpickItem() {
        super(new FabricItemSettings()
                .group(Territorial.BASE_GROUP)
                .maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        var lockpickItemStack = user.getStackInHand(hand);

        user.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                GenericContainerScreenHandler.createGeneric9x3(i, playerInventory),
                new TranslatableText("container.modname.name")));

        return TypedActionResult.pass(lockpickItemStack);
    }
}
