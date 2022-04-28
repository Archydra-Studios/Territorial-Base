package io.github.profjb58.territorial.client.gui;

import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.mixin.client.OverlayRemainingAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.item.Item;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LockableHud {

    private boolean ignoreCycle = false;
    //private WTiledSprite lockImage;

    public void showLockInfo(LockableBlock lb, ClientPlayerEntity player) {
        if(!ignoreCycle) {
            if(lb.exists()) {
                reset();

                var window = MinecraftClient.getInstance().getWindow();
                int hudHeight = window.getScaledHeight();
                int hudWidth = window.getScaledWidth();

                String lockId, lockOwner;
                if(lb.lockOwnerUuid().equals(player.getUuid())) {
                    lockId = lb.lockId();
                    lockOwner = lb.lockOwnerName();
                }
                else {
                    lockId = "§k" + lb.lockId();
                    lockOwner = "§k" + lb.lockOwnerName();
                }

                String fc = getLockableFormattingColour(lb.lockType());
                LiteralText lockInfoText = new LiteralText(fc + "Id: §f" + lockId + "   " + fc + "Owner: §f" + lockOwner);
                player.sendMessage(lockInfoText, true);

                var item = lb.getLockItemStack(1).getItem();
                //lockImage = new WTiledSprite(32, 32, new Identifier(Territorial.MOD_ID, "textures/item/" + item.toString() + ".png"));

                //CottonHud.add(lockImage, (hudWidth / 2) - 16, hudHeight - 100);
            }
        }
        else {
            var inGameHud = MinecraftClient.getInstance().inGameHud;
            if(((OverlayRemainingAccessor) inGameHud).getOverlayRemaining() == 0) {
                ignoreCycle = false;
            }
        }
    }

    public void reset() {
        //CottonHud.remove(lockImage);
        ((OverlayRemainingAccessor) MinecraftClient.getInstance().inGameHud).setOverlayRemaining(0); // Clears the action message
    }

    private String getLockableFormattingColour(LockableBlock.LockType lockType) {
        return switch (lockType) {
            case NETHERITE -> "§0";
            case DIAMOND -> "§b";
            case IRON -> "§7";
            case GOLD -> "§6";
            case UNBREAKABLE -> "§d";
        };
    }

    public void ignoreCycle() {
        reset();
        ignoreCycle = true;
    }

    public boolean isIgnoringCycle() { return ignoreCycle; }
}
