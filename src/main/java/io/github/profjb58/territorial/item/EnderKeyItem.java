package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.util.TextUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.github.profjb58.territorial.util.TextUtils.spacer;

public class EnderKeyItem extends Item {

    public EnderKeyItem() {
        super(new FabricItemSettings().group(Territorial.BASE_GROUP).maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        if(stack.hasCustomName()) {
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
}
