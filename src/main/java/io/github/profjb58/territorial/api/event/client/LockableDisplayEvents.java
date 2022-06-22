package io.github.profjb58.territorial.api.event.client;

import io.github.cottonmc.cotton.gui.widget.WTiledSprite;
import io.github.profjb58.territorial.api.event.common.LockableBlockEvents;
import io.github.profjb58.territorial.block.LockableBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

/**
 * Client display events for Territorials custom Lockable Block Entities. Can use to override or add additional functionality for your mod
 */
@Environment(EnvType.CLIENT)
public final class LockableDisplayEvents {

    public static Event<BeforeHudDisplay> BEFORE_HUD_DISPLAY = EventFactory.createArrayBacked(BeforeHudDisplay.class, (listeners) ->
            (inGameHud, lockInfoText, lockImage) -> {
                for(BeforeHudDisplay listener : listeners)
                    return listener.beforeHudDisplay(inGameHud, lockInfoText, lockImage);
                return false;
            }
    );

    public static Event<AfterHudDisplay> AFTER_HUD_DISPLAY = EventFactory.createArrayBacked(AfterHudDisplay.class, (listeners) ->
            (inGameHud, lockInfoText, lockImage) -> {
                for (AfterHudDisplay listener : listeners)
                    listener.afterHudDisplay(inGameHud, lockInfoText, lockImage);
            }
    );

    public static Event<BeforeScreenDisplay> BEFORE_SCREEN_DISPLAY = EventFactory.createArrayBacked(BeforeScreenDisplay.class, (listeners) ->
            (screen, matrices, lockOwner, lockId, lockTexture) -> {
                for(BeforeScreenDisplay listener : listeners)
                    return listener.beforeScreenDisplay(screen, matrices, lockOwner, lockId, lockTexture);
                return false;
            }
    );

    public static Event<AfterScreenDisplay> AFTER_SCREEN_DISPLAY = EventFactory.createArrayBacked(AfterScreenDisplay.class, (listeners) ->
            (screen, matrices, lockOwner, lockId, lockTexture) -> {
                for (AfterScreenDisplay listener : listeners)
                    listener.afterScreenDisplay(screen, matrices, lockOwner, lockId, lockTexture);
            }
    );

    public interface BeforeHudDisplay {
        /**
         * Fired before info about the locked block is displayed to the player via the in game HUD
         *
         * @return Whether to cancel the displaying info on the lockable block. Can be useful in order to mitigate display
         *         bugs or implement your own way of displaying info to the player
         */
        boolean beforeHudDisplay(InGameHud inGameHud, LiteralText lockInfoText, WTiledSprite lockImage);
    }

    public interface AfterHudDisplay {
        /**
         * Fired after info about the locked block is displayed to the player via the in game HUD
         */
        void afterHudDisplay(InGameHud inGameHud, LiteralText lockInfoText, WTiledSprite lockImage);
    }

    public interface BeforeScreenDisplay {
        /**
         * Fired before info about the locked block is displayed to the player within the blocks GUI
         *
         * @return Whether to cancel the displaying info on the lockable block. Can be useful in order to mitigate display
         *         bugs or implement your own way of displaying info to the player
         */
        boolean beforeScreenDisplay(Screen screen, MatrixStack matrices, LiteralText lockOwner, LiteralText lockId, Identifier lockTexture);
    }

    public interface AfterScreenDisplay {
        /**
         * Fired after info about the locked block is displayed to the player within the blocks GUI
         */
        void afterScreenDisplay(Screen screen, MatrixStack matrices, LiteralText lockOwner, LiteralText lockId, Identifier lockTexture);
    }
}
