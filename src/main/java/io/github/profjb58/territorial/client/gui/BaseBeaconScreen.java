package io.github.profjb58.territorial.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.profjb58.territorial.block.entity.BaseBeaconBlockEntity;
import io.github.profjb58.territorial.screen.BaseBeaconScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
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

import static io.github.profjb58.territorial.client.gui.BaseBeaconScreen.ArrowSelectorButtonWidget.ArrowDirection;

import java.util.List;

public class BaseBeaconScreen extends HandledScreen<BaseBeaconScreenHandler> {
    static final Identifier TEXTURE = new Identifier("textures/gui/container/beacon.png");
    private static final Text PRIMARY_POWER_TEXT, SECONDARY_POWER_TEXT, MODE_TEXT, TEAM_TEXT, RANGE_TEXT;
    private static final int SIDE_PANEL_WIDTH = 80;

    private final List<BaseBeaconScreen.BeaconButtonWidget> buttons = Lists.newArrayList();
    @Nullable
    StatusEffect primaryEffect;
    @Nullable
    StatusEffect secondaryEffect;

    public BaseBeaconScreen(BaseBeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 309;
        this.backgroundHeight = 219;

        handler.addListener(new ScreenHandlerListener() {
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
            }

            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
                var beaconScreenHandler = (BaseBeaconScreenHandler) handler;
                BaseBeaconScreen.this.primaryEffect = beaconScreenHandler.getPrimaryEffect();
                BaseBeaconScreen.this.secondaryEffect = beaconScreenHandler.getSecondaryEffect();
            }
        });
    }

    private <T extends ClickableWidget & BaseBeaconScreen.BeaconButtonWidget> void addButton(T button) {
        this.addDrawableChild(button);
        this.buttons.add(button);
    }

    protected void init() {
        super.init();
        this.buttons.clear();
        this.addButton(new BaseBeaconScreen.DoneButtonWidget(this.x + 164 + SIDE_PANEL_WIDTH, this.y + 107));
        this.addButton(new BaseBeaconScreen.CancelButtonWidget(this.x + 190 + SIDE_PANEL_WIDTH, this.y + 107));
        final StatusEffect[][] effectsByLevel = getScreenHandler().useAlternateEffects()
                ? BaseBeaconBlockEntity.ALTERNATE_EFFECTS_BY_LEVEL : BaseBeaconBlockEntity.EFFECTS_BY_LEVEL;

        int j, k, l;
        StatusEffect statusEffect;
        BaseBeaconScreen.EffectButtonWidget effectButtonWidget;
        for(int i = 0; i <= 2; ++i) {
            j = effectsByLevel[i].length;
            k = j * 22 + (j - 1) * 2;

            for(l = 0; l < j; ++l) {
                statusEffect = effectsByLevel[i][l];
                effectButtonWidget = new BaseBeaconScreen.EffectButtonWidget(this.x + 76 + SIDE_PANEL_WIDTH + l * 24 - k / 2, this.y + 22 + i * 25, statusEffect, true, i);
                effectButtonWidget.active = false;
                this.addButton(effectButtonWidget);
            }
        }
        j = effectsByLevel[3].length + 1;
        k = j * 22 + (j - 1) * 2;

        for(l = 0; l < j - 1; ++l) {
            statusEffect = effectsByLevel[3][l];
            effectButtonWidget = new BaseBeaconScreen.EffectButtonWidget(this.x + 167 + SIDE_PANEL_WIDTH + l * 24 - k / 2, this.y + 47, statusEffect, false, 3);
            effectButtonWidget.active = false;
            this.addButton(effectButtonWidget);
        }

        BaseBeaconScreen.EffectButtonWidget effectButtonWidget2
                = new BaseBeaconScreen.LevelTwoEffectButtonWidget(this.x + 167 + SIDE_PANEL_WIDTH + (j - 1) * 24 - k / 2, this.y + 47, effectsByLevel[0][0]);
        effectButtonWidget2.visible = false;
        this.addButton(effectButtonWidget2);

        var arrowSelectorButtonWidget = new ArrowSelectorButtonWidget(this.x + 20, this.y + 30, ArrowDirection.RIGHT);
        var arrowSelectorButtonWidget1 = new ArrowSelectorButtonWidget(this.x + 20, this.y + 60, ArrowDirection.LEFT);
        this.addDrawableChild(arrowSelectorButtonWidget);
        this.addDrawableChild(arrowSelectorButtonWidget1);
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
        drawCenteredText(matrices, this.textRenderer, PRIMARY_POWER_TEXT, 62 + SIDE_PANEL_WIDTH, 10, 14737632);
        drawCenteredText(matrices, this.textRenderer, SECONDARY_POWER_TEXT, 169 + SIDE_PANEL_WIDTH, 10, 14737632);

        // Side panel text
        drawCenteredText(matrices, this.textRenderer, MODE_TEXT, 40, 10, 14737632);

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
        drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 512, 256);
        this.itemRenderer.zOffset = 100.0F;
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.NETHERITE_INGOT), i + 20 + SIDE_PANEL_WIDTH, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.EMERALD), i + 41 + SIDE_PANEL_WIDTH, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.DIAMOND), i + 41 + 22 + SIDE_PANEL_WIDTH, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.GOLD_INGOT), i + 42 + 44 + SIDE_PANEL_WIDTH, j + 109);
        this.itemRenderer.renderInGuiWithOverrides(new ItemStack(Items.IRON_INGOT), i + 42 + 66 + SIDE_PANEL_WIDTH, j + 109);
        this.itemRenderer.zOffset = 0.0F;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    static {
        PRIMARY_POWER_TEXT = new TranslatableText("block.minecraft.beacon.primary");
        SECONDARY_POWER_TEXT = new TranslatableText("block.minecraft.beacon.secondary");
        MODE_TEXT = new TranslatableText("gui.territorial.beacon.mode");
        TEAM_TEXT = new TranslatableText("gui.territorial.beacon.team");
        RANGE_TEXT = new TranslatableText("gui.territorial.beacon.range");
    }

    @Environment(EnvType.CLIENT)
    interface BeaconButtonWidget {
        boolean shouldRenderTooltip();

        void renderTooltip(MatrixStack matrices, int mouseX, int mouseY);

        void tick(int level);
    }

    @Environment(EnvType.CLIENT)
    static class SelectorWidget {
        ArrowSelectorButtonWidget leftArrow, rightArrow;
        List<?> elements, sprites;

        SelectorWidget(int x, int y, Text title, List<Text> elements) {
            this(x, y, title, 0, elements, null);
        }

        SelectorWidget(int x, int y, Text title, int titleYOffset, List<Text> elements, @Nullable List<Sprite> sprites) {
            this.elements = elements;
            this.sprites = sprites;
        }
    }

    @Environment(EnvType.CLIENT)
    static class ArrowSelectorButtonWidget extends PressableWidget {
        enum ArrowDirection { RIGHT, LEFT }
        private final int u, v;


        ArrowSelectorButtonWidget(int x, int y, ArrowDirection direction) {
            super(x, y, 14, 22, LiteralText.EMPTY);
            this.u = direction == ArrowDirection.RIGHT ? 132 : 174;
            this.v = 219;
        }

        @Override
        public void onPress() {

        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BaseBeaconScreen.TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int u = this.u;
            if (!this.active)
                u += this.width;
            else if (this.isHovered())
                u += this.width * 2;
            drawTexture(matrices, this.x, this.y, u, this.v, this.width, this.height, 512, 256);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
            // TODO - Add narrations later...
            this.appendDefaultNarrations(builder);
        }
    }

    @Environment(EnvType.CLIENT)
    class DoneButtonWidget extends BaseBeaconScreen.IconButtonWidget {
        public DoneButtonWidget(int x, int y) {
            super(x, y, 90, 220, ScreenTexts.DONE);
        }

        public void onPress() {
            BaseBeaconScreen.this.client.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(BaseBeaconScreen.this.primaryEffect), StatusEffect.getRawId(BaseBeaconScreen.this.secondaryEffect)));
            BaseBeaconScreen.this.client.player.closeHandledScreen();
        }

        public void tick(int level) {
            this.active = (BaseBeaconScreen.this.handler).hasPayment() && BaseBeaconScreen.this.primaryEffect != null;
        }
    }

    @Environment(EnvType.CLIENT)
    class CancelButtonWidget extends BaseBeaconScreen.IconButtonWidget {
        public CancelButtonWidget(int x, int y) {
            super(x, y, 112, 220, ScreenTexts.CANCEL);
        }

        public void onPress() {
            BaseBeaconScreen.this.client.player.closeHandledScreen();
        }

        public void tick(int level) {
        }
    }

    @Environment(EnvType.CLIENT)
    private class EffectButtonWidget extends BaseBeaconScreen.BaseButtonWidget {
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
                    BaseBeaconScreen.this.primaryEffect = this.effect;
                } else {
                    BaseBeaconScreen.this.secondaryEffect = this.effect;
                }

                BaseBeaconScreen.this.tickButtons();
            }
        }

        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            BaseBeaconScreen.this.renderTooltip(matrices, this.tooltip, mouseX, mouseY);
        }

        protected void renderExtra(MatrixStack matrices) {
            RenderSystem.setShaderTexture(0, this.sprite.getAtlas().getId());
            drawSprite(matrices, this.x + 2, this.y + 2, this.getZOffset(), 18, 18, this.sprite);
        }

        public void tick(int level) {
            this.active = this.level < level;
            this.setDisabled(this.effect == (this.primary ? BaseBeaconScreen.this.primaryEffect : BaseBeaconScreen.this.secondaryEffect));
        }

        protected MutableText getNarrationMessage() {
            return this.getEffectName(this.effect);
        }
    }

    @Environment(EnvType.CLIENT)
    private class LevelTwoEffectButtonWidget extends BaseBeaconScreen.EffectButtonWidget {
        public LevelTwoEffectButtonWidget(int x, int y, StatusEffect statusEffect) {
            super(x, y, statusEffect, false, 3);
        }

        protected MutableText getEffectName(StatusEffect statusEffect) {
            return (new TranslatableText(statusEffect.getTranslationKey())).append(" II");
        }

        public void tick(int level) {
            if (BaseBeaconScreen.this.primaryEffect != null) {
                this.visible = true;
                this.init(BaseBeaconScreen.this.primaryEffect);
                super.tick(level);
            } else {
                this.visible = false;
            }

        }
    }

    @Environment(EnvType.CLIENT)
    abstract class IconButtonWidget extends BaseBeaconScreen.BaseButtonWidget {
        private final int u;
        private final int v;

        protected IconButtonWidget(int x, int y, int u, int v, Text message) {
            super(x, y, message);
            this.u = u;
            this.v = v;
        }

        protected void renderExtra(MatrixStack matrices) {
            drawTexture(matrices, this.x + 2, this.y + 2, this.u, this.v, 18, 18, 512, 256);
        }

        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            BaseBeaconScreen.this.renderTooltip(matrices, BaseBeaconScreen.this.title, mouseX, mouseY);
        }
    }

    @Environment(EnvType.CLIENT)
    abstract static class BaseButtonWidget extends PressableWidget implements BaseBeaconScreen.BeaconButtonWidget {
        private boolean disabled;

        protected BaseButtonWidget(int x, int y) {
            this(x, y, LiteralText.EMPTY);
        }

        protected BaseButtonWidget(int x, int y, Text message) {
            super(x, y, 22, 22, message);
        }

        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BaseBeaconScreen.TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int j = 0;
            if (!this.active) {
                j += this.width * 2;
            } else if (this.disabled) {
                j += this.width;
            } else if (this.isHovered()) {
                j += this.width * 3;
            }

            drawTexture(matrices, this.x, this.y, j, 219, this.width, this.height, 512, 256);
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
