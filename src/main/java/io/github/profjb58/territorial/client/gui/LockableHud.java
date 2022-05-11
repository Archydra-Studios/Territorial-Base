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
    private static final String PADLOCK_TEXTURE_DIRECTORY = "textures/item/padlocks/";

    private WTiledSprite lockImage;
    private boolean showingLockInfo = false;

    public void showLockInfo(LockableBlock lb) {
        if(!showingLockInfo) {
            String lfc = lb.lockType().getFormatColour();
            var lockInfoText = new LiteralText(lfc + "Id: §f" + lb.lockId() + "   " + lfc + "Owner: §f" + lb.lockOwnerName());
            var inGameHud = (InGameHudAccess) MinecraftClient.getInstance().inGameHud;

            inGameHud.territorial$setOverlayMessage(lockInfoText, ACTION_MESSAGE_DURATION);
            lockImage = new WTiledSprite(32, 32, new Identifier(Territorial.MOD_ID,
                    PADLOCK_TEXTURE_DIRECTORY + lb.lockType().getName() + ".png"));

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
}
