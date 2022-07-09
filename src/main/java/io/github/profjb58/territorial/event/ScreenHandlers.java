package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.TerritorialClient;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;


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


