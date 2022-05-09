package io.github.profjb58.territorial.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface OverlayRemainingAccessor {

    @Accessor("overlayRemaining")
    void setOverlayRemaining(int overlayRemaining);

    @Accessor("overlayRemaining")
    int getOverlayRemaining();
}
