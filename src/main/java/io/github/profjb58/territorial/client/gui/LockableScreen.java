package io.github.profjb58.territorial.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.api.event.client.LockableDisplayEvents;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.mixin.client.HandledScreenAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public final class LockableScreen {
    private final int DISPLAY_CHAR_LIMIT = 20;
    private final int LOCK_IMAGE_HEIGHT = 50;

    @Nullable
    private final LiteralText lockOwnerText;
    private final LiteralText lockIdText, lockInfoTextChat;
    private final Identifier lockTexture;
    private boolean textInfoShown = false;
    private int displayHeight = 85;

    public LockableScreen(ClientPlayerEntity player, LockableBlock lb) {
        String lockOwnerName = lb.lockOwnerName();
        String lockId = lb.lockId();

        // Trim owner and lock name lengths if they are too long
        if(lb.lockOwnerName().length() > DISPLAY_CHAR_LIMIT)
            lockOwnerName = lb.lockOwnerName().substring(0, DISPLAY_CHAR_LIMIT - 3) + "...";

        if(lb.lockId().length() > DISPLAY_CHAR_LIMIT)
            lockId = lb.lockId().substring(0, DISPLAY_CHAR_LIMIT - 3) + "...";

        String lfc = lb.lockType().getFormatColour();
        this.lockIdText = new LiteralText(lfc + "Lock Name: §f" + lockId);
        this.lockInfoTextChat = (LiteralText) new LiteralText("").append(lockIdText);

        // Add owner details if player accessing is not the owner of the lock
        if(!lb.lockOwnerUuid().equals(player.getUuid())) {
            this.lockOwnerText = new LiteralText(lfc + "Owner: §f" + lockOwnerName);
            this.lockInfoTextChat.append("  ").append(this.lockOwnerText);
            this.displayHeight = 110; // Adjust display height to fit owner text in
        }
        else this.lockOwnerText = null;
        this.lockTexture = new Identifier(Territorial.MOD_ID, "textures/item/padlocks/" + lb.lockType().getRegistryName() + ".png");
    }

    public void show(Screen screen, TextRenderer textRenderer, ClientPlayerEntity player, MatrixStack matrices) {
        int backgroundHeight = ((HandledScreenAccessor) screen).getBackgroundHeight();
        boolean includeLockImage;
        int yTextStartPos;

        boolean cancelAction = LockableDisplayEvents.BEFORE_SCREEN_DISPLAY.invoker().beforeScreenDisplay(screen, matrices, lockOwnerText, lockIdText, lockTexture);
        if(!cancelAction) {
            // Adjust display height depending on how much space we have
            if(screen.height - displayHeight > backgroundHeight) {
                includeLockImage = true;
                yTextStartPos = 30;
            }
            else if(screen.height - (displayHeight - LOCK_IMAGE_HEIGHT) > backgroundHeight) {
                includeLockImage = false;
                yTextStartPos = 10;
            }
            else {
                if(!textInfoShown) player.sendMessage(lockInfoTextChat, false);
                textInfoShown = true;
                return;
            }

            if(includeLockImage) {
                RenderSystem.setShaderTexture(0, lockTexture);
                Screen.drawTexture(matrices, (screen.width / 2) - 10, 5, 0, 0, 25, 25, 25, 25);
            }
            Screen.drawCenteredText(matrices, textRenderer, lockIdText, screen.width / 2, yTextStartPos, DyeColor.WHITE.getFireworkColor());
            if(lockOwnerText != null)
                Screen.drawCenteredText(matrices, textRenderer, lockOwnerText, screen.width / 2, yTextStartPos + 10,
                        DyeColor.WHITE.getFireworkColor());
            LockableDisplayEvents.AFTER_SCREEN_DISPLAY.invoker().afterScreenDisplay(screen, matrices, lockOwnerText, lockIdText, lockTexture);
        }
    }
}
