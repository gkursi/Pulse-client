package xyz.qweru.pulse.client.render.ui.gui.widgets.color;

import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import java.awt.*;

public class ColorWidget extends Widget {
    private final Color start;
    private final Color end;

    public ColorWidget(float wx, float wy, float ww, float wh, Color start, Color end) {
        super(wx, wy, ww, wh);
        this.start = start;
        this.end = end;
    }

    @Override
    public void render(RenderContext context) {
        super.render(context);
        Pulse2D.drawRound(context.getMatrices(), x, y, w, h, Pulse2D.cornerRad, new Pulse2D.GradientRect(start, start, end, end));
    }

    boolean lmbClicked = false;
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if(RenderUtil.isInside(mouseX, mouseY, x, y, x+w, y+h) && lmbClicked) {
            double relativeX = Math.max(0, mouseX - x);
            double progress = relativeX / w;
            PulseClient.LOGGER.info("Progress: {}", progress);
        }
    }

    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        super.mouseInput(key, action, mouseX, mouseY);
        if(key == GLFW.GLFW_MOUSE_BUTTON_1) lmbClicked = action == GLFW.GLFW_PRESS;
    }
}
