package io.github.profjb58.territorial.event.template;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

public final class InGameHudEvents {

    public static final Event<OverlayFinishedDisplaying> OVERLAY_FINISHED_DISPLAYING = EventFactory.createArrayBacked(
            OverlayFinishedDisplaying.class, (callbacks) -> () -> {
                for (OverlayFinishedDisplaying callback : callbacks)
                    callback.overlayFinishedDisplaying();
    });


    @FunctionalInterface
    public interface OverlayFinishedDisplaying {
        void overlayFinishedDisplaying();
    }

    private InGameHudEvents() {}
}
