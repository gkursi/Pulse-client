package xyz.qweru.pulse.client.render.ui.gui.widgets;

import xyz.qweru.pulse.client.mixin.iinterface.IGameRenderer;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ClickGUI;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.mc;
import static xyz.qweru.pulse.client.render.renderer.Pulse2D.borderWidth;
import static xyz.qweru.pulse.client.render.renderer.Pulse2D.cornerRad;

public class CategoryBackgroundWidget extends Widget {
    public CategoryBackgroundWidget(float wx, float wy, float ww, float wh) {
        super(wx, wy, ww, wh);
    }

    public void resizeTo(float ny) {
        this.h = ny - y;
    }
    public boolean altColor = false;
    public Color color = new Color(0, 0, 0);

    @Override
    public void render(RenderContext context) {
        Color bColor = context.colorScheme().getBorderColor();

        Pulse2D.drawRound(context.matrixStack(), x, y, w, h, cornerRad, bColor);
        Pulse2D.drawRound(context.matrixStack(), x + borderWidth, y + borderWidth,
                w - borderWidth * 2, h - borderWidth  * 2, cornerRad, altColor ? color : context.colorScheme().PRIMARY());

    }

    public void setH(float h) {
        this.h = h;
    }
}
