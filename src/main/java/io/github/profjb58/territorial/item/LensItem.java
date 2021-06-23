package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.util.TextUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LensItem extends Item {

    public LensItem() { super(new FabricItemSettings().group(Territorial.BASE_GROUP)); }

    @Override
    public Text getName(ItemStack stack) {
        Text translationText = new TranslatableText(this.getTranslationKey());
        NbtCompound tag = stack.getSubTag("beam");

        if(tag != null) {
            DyeColor dyeColour = Optional.of(DyeColor.byId(tag.getInt("colour"))).orElse(DyeColor.WHITE);
            String dyeNameCapitalized = dyeColour.getName().substring(0, 1).toUpperCase() + dyeColour.getName().substring(1);
            return new LiteralText(dyeNameCapitalized + " " + translationText.getString());
        }
        return translationText;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound tag = stack.getSubTag("beam");
        if(tag != null) {
            int strengthMod = tag.getByte("strength");
            tooltip.add(new TranslatableText("tooltip.territorial.lens_strength_" + strengthMod));
            if(tag.getBoolean("light")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.light"));
            if(tag.getBoolean("highlight")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.highlight"));
            if(tag.getBoolean("death")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.death"));
            if(tag.getBoolean("rainbow")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.rainbow"));
            if(tag.getBoolean("sparkle")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.sparkle"));
        }
    }
}
