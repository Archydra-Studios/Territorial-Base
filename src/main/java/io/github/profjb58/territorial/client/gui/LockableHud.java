package io.github.profjb58.territorial.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WTiledSprite;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.misc.access.InGameHudAccess;
import io.github.profjb58.territorial.mixin.client.InGameHudMixin;
import io.github.profjb58.territorial.mixin.client.OverlayRemainingAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LockableHud {

    private static final int ACTION_MESSAGE_DURATION = 80;

    private WTiledSprite lockImage;
    private boolean showingLockInfo = false;

    public void showLockInfo(LockableBlock lb) {
        if(!showingLockInfo) {
            var window = MinecraftClient.getInstance().getWindow();
            int hudHeight = window.getScaledHeight();
            int hudWidth = window.getScaledWidth();

            String lfc = getLockableFormattingColour(lb.lockType());
            var lockInfoText = new LiteralText(lfc + "Id: §f" + lb.lockId() + "   " + lfc + "Owner: §f" + lb.lockOwnerName());
            var inGameHud = MinecraftClient.getInstance().inGameHud;
            ((InGameHudAccess) inGameHud).territorial$setOverlayMessage(lockInfoText, ACTION_MESSAGE_DURATION);

            var item = lb.getLockItemStack(1).getItem();
            lockImage = new WTiledSprite(32, 32, new Identifier(Territorial.MOD_ID, "textures/item/padlocks/" + item.toString() + ".png"));
            CottonHud.add(lockImage, CottonHud.Positioner.horizontallyCentered(-100));
            showingLockInfo = true;
        }
    }

    public void overlayFinishedDisplaying() {
        if(lockImage != null && showingLockInfo) {
            CottonHud.remove(lockImage);
            showingLockInfo = false;
        }
    }

    private String getLockableFormattingColour(LockableBlock.LockType lockType) {
        return switch (lockType) {
            case NETHERITE -> "§0";
            case DIAMOND -> "§b";
            case IRON -> "§7";
            case GOLD -> "§6";
            case UNBREAKABLE -> "§d";
        };
    }
}
