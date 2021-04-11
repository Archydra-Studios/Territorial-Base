package io.github.profjb58.territorial.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.profjb58.territorial.Territorial;
import io.github.profjb58.territorial.block.LockableBlock;
import io.github.profjb58.territorial.mixin.OverlayRemainingAccessor;
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
    private WTiledSprite lockImage;

    public void showLockInfo(LockableBlock lb, ClientPlayerEntity player) {
        if(!ignoreCycle) {
            if(lb.exists()) {
                reset();

                Window window = MinecraftClient.getInstance().getWindow();
                int hudHeight = window.getScaledHeight();
                int hudWidth = window.getScaledWidth();

                String lockId, lockOwner;
                if(lb.getLockOwnerUuid().equals(player.getUuid())) {
                    lockId = lb.getLockId();
                    lockOwner = lb.getLockOwnerName();
                }
                else {
                    lockId = "§k" + lb.getLockId();
                    lockOwner = "§k" + lb.getLockOwnerName();
                }

                String fc = getLockableFormattingColour(lb.getLockType());
                LiteralText lockInfoText = new LiteralText(fc + "Id: §f" + lockId + "   " + fc + "Owner: §f" + lockOwner);
                player.sendMessage(lockInfoText, true);

                Item item = lb.getLockItemStack(1).getItem();
                lockImage = new WTiledSprite(32, 32, new Identifier(Territorial.MOD_ID, "textures/item/" + item.toString() + ".png"));

                CottonHud.INSTANCE.add(lockImage, (hudWidth / 2) - 16, hudHeight - 100);
            }
        }
        else {
            InGameHud inGameHud = MinecraftClient.getInstance().inGameHud;
            if(((OverlayRemainingAccessor) inGameHud).getOverlayRemaining() == 0) {
                ignoreCycle = false;
            }
        }
    }

    public void reset() {
        CottonHud.INSTANCE.remove(lockImage);
        ((OverlayRemainingAccessor) MinecraftClient.getInstance().inGameHud).setOverlayRemaining(0); // Clears the action message
    }

    private String getLockableFormattingColour(LockableBlock.LockType lockType) {
        switch(lockType) {
            case NETHERITE:
                return "§0";
            case DIAMOND:
                return "§b";
            case IRON:
                return "§7";
            case GOLD:
                return "§6";
            case UNBREAKABLE:
                return "§d";
            default:
                return "§f";
        }
    }

    public void ignoreCycle() {
        reset();
        ignoreCycle = true;
    }

    public boolean isIgnoringCycle() { return ignoreCycle; }
}
