package io.github.profjb58.territorial.misc.access;

import net.minecraft.text.Text;

public interface InGameHudAccess {
    void territorial$setOverlayMessage(Text message, int duration);

    void territorial$clearOverlayMessage();
}
