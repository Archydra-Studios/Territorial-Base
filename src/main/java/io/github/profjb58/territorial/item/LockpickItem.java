package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.blockEntity.LockableBlockEntity.LockedType;
import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

public class LockpickItem extends Item {

    public LockpickItem() {
        super(new FabricItemSettings()
                .group(Territorial.BASE_GROUP)
                .maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if(player != null) {
            if(player.isSneaking() && !ctx.getWorld().isClient()) {
                LockableBlockEntity lbe = new LockableBlockEntity((ServerWorld) ctx.getWorld(), ctx.getBlockPos());
                if(lbe.exists()) { // Lockable block found
                    if(player.getUuid().equals(lbe.getLockOwner()) || lbe.getLockType() == LockedType.CREATIVE) {
                        if(lbe.remove()) {
                            ctx.getPlayer().sendMessage(new TranslatableText("message.territorial.lock_removed"), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                    else {
                        // Lock picking mechanics
                    }
                }
                ctx.getPlayer().sendMessage(new TranslatableText("message.territorial.no_lock"), true);
            }
        }

        return ActionResult.PASS;
    }
}
