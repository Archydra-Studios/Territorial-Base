package io.github.profjb58.territorial.event;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.client.gui.LockableScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Hand;


public class ScreenHandlers {

    public static void init() {
        ScreenEvents.AFTER_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
            if(TerritorialClient.lockableScreen != null) afterInitLockableScreen(client, screen);
        }));
    }

    private static void afterInitLockableScreen(MinecraftClient client, Screen screen) {
        ScreenEvents.afterRender(screen).register((screenAfterRender, matrices, mouseX, mouseY, tickDelta) -> {
            if(TerritorialClient.lockableScreen != null)
                TerritorialClient.lockableScreen.show(screenAfterRender, client.textRenderer, client.player, matrices);
        });
        ScreenEvents.remove(screen).register(screenAfterRemove -> TerritorialClient.lockableScreen = null);
    }
}


