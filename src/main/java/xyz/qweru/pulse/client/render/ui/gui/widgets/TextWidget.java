package xyz.qweru.pulse.client.render.ui.gui.widgets;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

public class TextWidget extends Widget {
    private final String text;
    private final float textOffset;
    private final float textOffsetX;
    private final float textOffsetY;

    public TextWidget(String text, float wx, float wy, float textOffset) {
        super(wx, wy, 0, 0);
        this.text = text;
        PulseClient.LOGGER.info("Basic text: {}", text);
        this.textOffset = textOffset;
        textOffsetX = 0;
        textOffsetY = 0;
    }

    public TextWidget(String text, float wx, float wy, float ww, float wh) {
        super(wx, wy, ww, wh);
        this.text = text;
//        PulseClient.LOGGER.info("Centered Text: {}", text);
        this.textOffset = 0;
        if(RenderUtil.textRenderer != null) {
            textOffsetX = (ww - RenderUtil.textRenderer.getWidth(text)) / 2;
            textOffsetY = 2;
        } else {
            textOffsetX = 0;
            textOffsetY = 0;
        }
    }

    @Override
    public void render(RenderContext context) {
        super.render(context);
//        Pulse2D.drawRound(context.matrixStack(), x, y, w, h, Pulse2D.cornerRad, context.colorScheme().ACCENT());
//        Pulse2D.drawRound(context.matrixStack(), x+Pulse2D.borderWidth, y+Pulse2D.borderWidth, w-Pulse2D.borderWidth*2, h-Pulse2D.borderWidth*2, Pulse2D.cornerRad, context.colorScheme().PRIMARY());
        RenderUtil.textRenderer.drawString(context.matrixStack(), text, x + textOffset + textOffsetX,
                y + textOffset + textOffsetY + RenderUtil.fontOffsetY, context.colorScheme().TEXT().getRGB());
    }
}
