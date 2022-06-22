package io.github.profjb58.territorial.event;

import io.github.profjb58.territorial.TerritorialClient;
import io.github.profjb58.territorial.client.gui.LockableHud;
import io.github.profjb58.territorial.client.render.entity.LaserBlockEntityRenderer;
import io.github.profjb58.territorial.event.template.InGameHudEvents;
import io.github.profjb58.territorial.util.TickCounter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ClientTickHandlers {

    public static void init() {
        ClientTickEvents.START_WORLD_TICK.register((clientWorld) -> LaserBlockEntityRenderer.rainbowColourTick());
        ClientTickEvents.END_CLIENT_TICK.register((client) -> LaserBlockEntityRenderer.rainbowColourTick());

        // Hud events
        InGameHudEvents.OVERLAY_FINISHED_DISPLAYING.register(ClientTickHandlers::onOverlayFinishedDisplaying);
    }

    private static void onOverlayFinishedDisplaying() {
        if(TerritorialClient.lockableHud != null) {
            TerritorialClient.lockableHud.clear();
            TerritorialClient.lockableHud = null;
        }
    }
}
