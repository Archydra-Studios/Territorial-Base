package io.github.profjb58.territorial.item;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.blockEntity.LockableBlockEntity;
import io.github.profjb58.territorial.util.LockUtils;
import io.github.profjb58.territorial.world.WorldLockStorage;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class LockpickItem extends Item {

    public enum LockPickType {
        NORMAL,
        CREATIVE
    }

    private final LockPickType type;

    public LockpickItem(LockPickType type) {
        super(new FabricItemSettings()
                .group(Territorial.BASE_GROUP)
                .maxCount(1));
        this.type = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if(player != null) {
            if(player.isSneaking() && !ctx.getWorld().isClient) {
                LockableBlockEntity lbe = new LockableBlockEntity(ctx.getWorld(), ctx.getBlockPos());
                if(lbe.exists()) { // Lockable block found
                    LockableBlock lb = lbe.getBlock();
                    if(player.getUuid().equals(lb.getLockOwner()) || type == LockPickType.CREATIVE) {
                        if(lbe.remove()) {
                            player.sendMessage(new TranslatableText("message.territorial.lock_removed"), true);
                            BlockPos pos = ctx.getBlockPos();
                            ItemStack padlockStack = LockUtils.getItemStackFromLock(lb.getLockType(), lb.getLockId(), 1);
                            ItemEntity padlockEntity = new ItemEntity(ctx.getWorld(), pos.getX(), pos.getY(), pos.getZ(), padlockStack);
                            ctx.getWorld().spawnEntity(padlockEntity);

                            WorldLockStorage lps = WorldLockStorage.get((ServerWorld) ctx.getWorld());
                            lps.removeLock(lb);
                            return ActionResult.SUCCESS;
                        }
                        else {
                            // Really shouldn't happen, but just encase
                            Territorial.logger.error("Lockpick failed to remove NBT lock data :(. Please report this as an issue");
                        }
                    }
                    else {
                        // Lock picking mechanics
                        return ActionResult.SUCCESS;
                    }
                }
                ctx.getPlayer().sendMessage(new TranslatableText("message.territorial.no_lock"), true);
                ctx.getPlayer().sendMessage(new TranslatableText("message.territorial.no_lock"), true);
            }
        }

        return ActionResult.PASS;
    }
}
