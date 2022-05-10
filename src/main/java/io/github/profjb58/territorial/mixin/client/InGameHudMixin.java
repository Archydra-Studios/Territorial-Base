package io.github.profjb58.territorial.mixin.client;

import io.github.profjb58.territorial.event.template.InGameHudEvents;
import io.github.profjb58.territorial.misc.access.InGameHudAccess;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin implements InGameHudAccess {

    @Shadow
    Text overlayMessage;
    @Shadow
    int overlayRemaining;
    private int prevOverlayRemaining;

    @Shadow
    boolean overlayTinted;

    public void territorial$setOverlayMessage(Text message, int duration) {
        this.overlayMessage = message;
        this.overlayRemaining = duration;
        this.overlayTinted = false;
    }

    @Inject(method="tick()V", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if(prevOverlayRemaining > 0 && overlayRemaining == 0)
            InGameHudEvents.OVERLAY_FINISHED_DISPLAYING.invoker().overlayFinishedDisplaying();
        prevOverlayRemaining = overlayRemaining;
    }
}
