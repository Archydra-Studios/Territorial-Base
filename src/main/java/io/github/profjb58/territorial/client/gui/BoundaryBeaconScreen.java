package io.github.profjb58.territorial.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.profjb58.territorial.block.entity.BoundaryBeaconBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoundaryBeaconScreen extends HandledScreen<BoundaryBeaconScreenHandler> {
    static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
    private static final Text PRIMARY_POWER_TEXT = new TranslatableText("block.minecraft.beacon.primary");
    private static final Text SECONDARY_POWER_TEXT = new TranslatableText("block.minecraft.beacon.secondary");
    private final List<BoundaryBeaconScreen.BeaconButtonWidget> buttons = Lists.newArrayList();
    @Nullable
    StatusEffect primaryEffect;
    @Nullable
    StatusEffect secondaryEffect;

    public BoundaryBeaconScreen(BoundaryBeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 230;
        this.backgroundHeight = 219;
        handler.addListener(new ScreenHandlerListener() {
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
            }

            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
                var beaconScreenHandler = (BoundaryBeaconScreenHandler) handler;
                BoundaryBeaconScreen.this.primaryEffect = beaconScreenHandler.getPrimaryEffect();
                BoundaryBeaconScreen.this.secondaryEffect = beaconScreenHandler.getSecondaryEffect();
            }
        });
    }

    private <T extends ClickableWidget & BoundaryBeaconScreen.BeaconButtonWidget> void addButton(T button) {
        this.addDrawableChild(button);
        this.buttons.add(button);
    }

    protected void init() {
        super.init();
        this.buttons.clear();
        this.addButton(new BoundaryBeaconScreen.DoneButtonWidget(this.x + 164, this.y + 107));
        this.addButton(new BoundaryBeaconScreen.CancelButtonWidget(this.x + 190, this.y + 107));

        int j;
        int k;
        int l;
        StatusEffect statusEffect;
        BoundaryBeaconScreen.EffectButtonWidget effectButtonWidget;
        for(int i = 0; i <= 2; ++i) {
            j = BoundaryBeaconBlockEntity.BB_EFFECTS_BY_LEVEL[i].length;
            k = j * 22 + (j - 1) * 2;

            for(l = 0; l < j; ++l) {
                statusEffect = BoundaryBeaconBlockEntity.BB_EFFECTS_BY_LEVEL[i][l];
                effectButtonWidget = new BoundaryBeaconScreen.EffectButtonWidget(this.x + 76 + l * 24 - k / 2, this.y + 22 + i * 25, statusEffect, true, i);
                effectButtonWidget.active = false;
                this.addButton(effectButtonWidget);
            }
        }

        j = BoundaryBeaconBlockEntity.BB_EFFECTS_BY_LEVEL[3].length + 1;
        k = j * 22 + (j - 1) * 2;

        for(l = 0; l < j - 1; ++l) {
            statusEffect = BoundaryBeaconBlockEntity.BB_EFFECTS_BY_LEVEL[3][l];
            effectButtonWidget = new BoundaryBeaconScreen.EffectButtonWidget(this.x + 167 + l * 24 - k / 2, this.y + 47, statusEffect, false, 3);
            effectButtonWidget.active = false;
            this.addButton(effectButtonWidget);
        }

        BoundaryBeaconScreen.EffectButtonWidget effectButtonWidget2 = new BoundaryBeaconScreen.LevelTwoEffectButtonWidget(this.x + 167 + (j - 1) * 24 - k / 2, this.y + 47, BoundaryBeaconBlockEntity.BB_EFFECTS_BY_LEVEL[0][0]);
        effectButtonWidget2.visible = false;
        this.addButton(effectButtonWidget2);
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        this.tickButtons();
    }

    void tickButtons() {
        int i = this.handler.getProperties();
        this.buttons.forEach((button) -> button.tick(i));
    }

    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        drawCenteredText(matrices, this.textRenderer, PRIMARY_POWER_TEXT, 62, 10, 14737632);
        drawCenteredText(matrices, this.textRenderer, SECONDARY_POWER_TEXT, 169, 10, 14737632);

        for (BeaconButtonWidget beaconButtonWidget : this.buttons) {
            if (beaconButtonWidget.shouldRenderTooltip()) {
                beaconButtonWidget.renderTooltip(matrices, mouseX - this.x, mouseY - this.y);
                break;
            }
        }

    }

    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.itemRenderer.zOffset = 100.0F;
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.EMERALD), i + 41, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
        this.itemRenderer.zOffset = 0.0F;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Environment(EnvType.CLIENT)
    interface BeaconButtonWidget {
        boolean shouldRenderTooltip();

        void renderTooltip(MatrixStack matrices, int mouseX, int mouseY);

        void tick(int level);
    }

    @Environment(EnvType.CLIENT)
    class DoneButtonWidget extends BoundaryBeaconScreen.IconButtonWidget {
        public DoneButtonWidget(int x, int y) {
            super(x, y, 90, 220, ScreenTexts.DONE);
        }

        public void onPress() {
            BoundaryBeaconScreen.this.client.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BoundaryBeaconScreen.this.primaryEffect), StatusEffect.getRawId(BoundaryBeaconScreen.this.secondaryEffect)));
            BoundaryBeaconScreen.this.client.player.closeHandledScreen();
        }

        public void tick(int level) {
            this.active = (BoundaryBeaconScreen.this.handler).hasPayment() && BoundaryBeaconScreen.this.primaryEffect != null;
        }
    }

    @Environment(EnvType.CLIENT)
    class CancelButtonWidget extends BoundaryBeaconScreen.IconButtonWidget {
        public CancelButtonWidget(int x, int y) {
            super(x, y, 112, 220, ScreenTexts.CANCEL);
        }

        public void onPress() {
            BoundaryBeaconScreen.this.client.player.closeHandledScreen();
        }

        public void tick(int level) {
        }
    }

    @Environment(EnvType.CLIENT)
    private class EffectButtonWidget extends BoundaryBeaconScreen.BaseButtonWidget {
        private final boolean primary;
        protected final int level;
        private StatusEffect effect;
        private Sprite sprite;
        private Text tooltip;

        public EffectButtonWidget(int x, int y, StatusEffect statusEffect, boolean primary, int level) {
            super(x, y);
            this.primary = primary;
            this.level = level;
            this.init(statusEffect);
        }

        protected void init(StatusEffect statusEffect) {
            this.effect = statusEffect;
            this.sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(statusEffect);
            this.tooltip = this.getEffectName(statusEffect);
        }

        protected MutableText getEffectName(StatusEffect statusEffect) {
            return new TranslatableText(statusEffect.getTranslationKey());
        }

        public void onPress() {
            if (!this.isDisabled()) {
                if (this.primary) {
                    BoundaryBeaconScreen.this.primaryEffect = this.effect;
                } else {
                    BoundaryBeaconScreen.this.secondaryEffect = this.effect;
                }

                BoundaryBeaconScreen.this.tickButtons();
            }
        }

        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            BoundaryBeaconScreen.this.renderTooltip(matrices, this.tooltip, mouseX, mouseY);
        }

        protected void renderExtra(MatrixStack matrices) {
            RenderSystem.setShaderTexture(0, this.sprite.getAtlas().getId());
            drawSprite(matrices, this.x + 2, this.y + 2, this.getZOffset(), 18, 18, this.sprite);
        }

        public void tick(int level) {
            this.active = this.level < level;
            this.setDisabled(this.effect == (this.primary ? BoundaryBeaconScreen.this.primaryEffect : BoundaryBeaconScreen.this.secondaryEffect));
        }

        protected MutableText getNarrationMessage() {
            return this.getEffectName(this.effect);
        }
    }

    @Environment(EnvType.CLIENT)
    private class LevelTwoEffectButtonWidget extends BoundaryBeaconScreen.EffectButtonWidget {
        public LevelTwoEffectButtonWidget(int x, int y, StatusEffect statusEffect) {
            super(x, y, statusEffect, false, 3);
        }

        protected MutableText getEffectName(StatusEffect statusEffect) {
            return (new TranslatableText(statusEffect.getTranslationKey())).append(" II");
        }

        public void tick(int level) {
            if (BoundaryBeaconScreen.this.primaryEffect != null) {
                this.visible = true;
                this.init(BoundaryBeaconScreen.this.primaryEffect);
                super.tick(level);
            } else {
                this.visible = false;
            }

        }
    }

    @Environment(EnvType.CLIENT)
    abstract class IconButtonWidget extends BoundaryBeaconScreen.BaseButtonWidget {
        private final int u;
        private final int v;

        protected IconButtonWidget(int x, int y, int u, int v, Text message) {
            super(x, y, message);
            this.u = u;
            this.v = v;
        }

        protected void renderExtra(MatrixStack matrices) {
            this.drawTexture(matrices, this.x + 2, this.y + 2, this.u, this.v, 18, 18);
        }

        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            BoundaryBeaconScreen.this.renderTooltip(matrices, BoundaryBeaconScreen.this.title, mouseX, mouseY);
        }
    }

    @Environment(EnvType.CLIENT)
    abstract static class BaseButtonWidget extends PressableWidget implements BoundaryBeaconScreen.BeaconButtonWidget {
        private boolean disabled;

        protected BaseButtonWidget(int x, int y) {
            super(x, y, 22, 22, LiteralText.EMPTY);
        }

        protected BaseButtonWidget(int x, int y, Text message) {
            super(x, y, 22, 22, message);
        }

        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BoundaryBeaconScreen.TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int j = 0;
            if (!this.active) {
                j += this.width * 2;
            } else if (this.disabled) {
                j += this.width;
            } else if (this.isHovered()) {
                j += this.width * 3;
            }

            this.drawTexture(matrices, this.x, this.y, j, 219, this.width, this.height);
            this.renderExtra(matrices);
        }

        protected abstract void renderExtra(MatrixStack matrices);

        public boolean isDisabled() {
            return this.disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public boolean shouldRenderTooltip() {
            return this.hovered;
        }

        public void appendNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }
    }
}
