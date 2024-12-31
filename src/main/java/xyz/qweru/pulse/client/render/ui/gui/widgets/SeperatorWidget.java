package xyz.qweru.pulse.client.render.ui.gui.widgets;

import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.systems.modules.settings.impl.SeperatorSetting;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

public class SeperatorWidget extends Widget {

    private final SeperatorSetting setting;

    public SeperatorWidget(float wx, float wy, float ww, float wh, SeperatorSetting setting) {
        super(wx, wy, ww, wh);
        this.setting = setting;
    }

    @Override
    public void render(RenderContext context) {
        FontRenderer text = RenderUtil.textRenderer;

        float lY = y + ((h - 0.5f) / 2);

        float lX = x + Pulse2D.borderWidth + 1;
        float lW = w - Pulse2D.borderWidth*2 - 2;

        if(setting.getTitle() != null) {
            float textY = lY - (text.getHeight(setting.getTitle()) / 2) + RenderUtil.fontOffsetY + 0.5f;
            float textX = x + (lW - text.getWidth(setting.getTitle())) / 2;

            Pulse2D.drawRound(context.matrixStack(), lX, lY, textX - lX - 0.75f, 0.5f, 0, context.colorScheme().SECONDARY());
            Pulse2D.drawRound(context.matrixStack(), textX + text.getWidth(setting.getTitle()) + 0.75f, lY,  (lW + x) - (textX + text.getWidth(setting.getTitle()) + 0.75f), 0.5f, 0, context.colorScheme().SECONDARY());
            text.drawString(context.matrixStack(), setting.getTitle(), textX, textY + RenderUtil.fontOffsetY, context.colorScheme().MUTED_TEXT().getRGB());
        }

        else Pulse2D.drawRound(context.matrixStack(), lX, lY, lW, 0.5f, 0, context.colorScheme().SECONDARY());
    }
}
