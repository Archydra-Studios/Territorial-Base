package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.entity.LaserTransmitterBlockEntity;
import io.github.profjb58.territorial.event.registry.TerritorialRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LensItem extends Item {

    public LensItem() {
        super(new FabricItemSettings().group(Territorial.BASE_GROUP));
    }

    @Override
    public Text getName(ItemStack stack) {
        Text translationText = new TranslatableText(this.getTranslationKey());
        NbtCompound tag = stack.getSubNbt("beam");

        if (tag != null) {
            DyeColor dyeColour = Optional.of(DyeColor.byId(tag.getInt("colour"))).orElse(DyeColor.WHITE);
            String dyeNameCapitalized = dyeColour.getName().substring(0, 1).toUpperCase() + dyeColour.getName().substring(1);
            return new LiteralText(dyeNameCapitalized + " " + translationText.getString());
        }
        return translationText;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext ctx) {
        super.appendTooltip(stack, world, tooltip, ctx);
        NbtCompound tag = stack.getSubNbt("beam");
        if (tag != null) {
            int strengthMod = tag.getByte("strength");
            tooltip.add(new TranslatableText("tooltip.territorial.lens_strength_" + strengthMod));
            if (tag.getBoolean("light")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.light"));
            if (tag.getBoolean("highlight"))
                tooltip.add(new TranslatableText("tooltip.territorial.modifier.highlight"));
            if (tag.getBoolean("death")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.death"));
            if (tag.getBoolean("rainbow")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.rainbow"));
            if (tag.getBoolean("sparkle")) tooltip.add(new TranslatableText("tooltip.territorial.modifier.sparkle"));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        BlockEntity be = ctx.getWorld().getBlockEntity(ctx.getBlockPos());
        World world = ctx.getWorld();

        if (!world.isClient && be instanceof LaserTransmitterBlockEntity lbe) {
            BlockPos pos = ctx.getBlockPos();
            PlayerEntity player = ctx.getPlayer();
            ItemStack lensStack = TerritorialRegistry.LENS.getDefaultStack();

            if(!Objects.equals(lbe.writeNbtStack(lensStack).getSubNbt("beam"), ctx.getStack().getSubNbt("beam"))) {
                ItemEntity lensToDrop = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), lbe.writeNbtStack(lensStack));
                lbe.createFromLens(ctx.getStack());
                if(player != null && !player.isCreative()) {
                    ctx.getStack().decrement(1);
                    world.spawnEntity(lensToDrop);
                }
            }
        }
        return ActionResult.PASS;
    }
}