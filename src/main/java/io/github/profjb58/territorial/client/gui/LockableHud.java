package io.github.profjb58.territorial.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.WTiledSprite;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.api.event.client.LockableDisplayEvents;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.misc.access.InGameHudAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class LockableHud {
    private static final int ACTION_MESSAGE_DURATION = 80;
    private static final String PADLOCK_TEXTURE_DIRECTORY = "textures/item/padlocks/";

    private boolean isDisplaying = false;
    private final WTiledSprite lockImage;
    private final LiteralText lockInfoText;

    public LockableHud(@NotNull LockableBlock lb) {
        String lfc = lb.lockType().getFormatColour();
        lockInfoText = new LiteralText(lfc + "Lock Name: §f" + lb.lockId() + "    " + lfc + "Owner: §f" + lb.lockOwnerName());
        lockImage = new WTiledSprite(32, 32, new Identifier(Territorial.MOD_ID,
                PADLOCK_TEXTURE_DIRECTORY + lb.lockType().getRegistryName() + ".png"));;
    }

    public void show() {
        var inGameHud = MinecraftClient.getInstance().inGameHud;
        boolean cancelAction = LockableDisplayEvents.BEFORE_HUD_DISPLAY.invoker().beforeHudDisplay(inGameHud, lockInfoText, lockImage);

        if(!cancelAction) {
            ((InGameHudAccess) inGameHud).territorial$setOverlayMessage(lockInfoText, ACTION_MESSAGE_DURATION);
            CottonHud.add(lockImage, CottonHud.Positioner.horizontallyCentered(-100));
            isDisplaying = true;
            LockableDisplayEvents.AFTER_HUD_DISPLAY.invoker().afterHudDisplay(inGameHud, lockInfoText, lockImage);
        }
    }

    public void clear() {
        if(isDisplaying) {
            var inGameHud = MinecraftClient.getInstance().inGameHud;

            if(lockImage != null) CottonHud.remove(lockImage);
            ((InGameHudAccess) inGameHud).territorial$clearOverlayMessage();
        }
        isDisplaying = false;
    }
}
