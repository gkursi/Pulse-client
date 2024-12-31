package xyz.qweru.pulse.client.render.ui.gui.widgets;

import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.utils.render.AnimationUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import java.awt.*;

public class ButtonWidget extends Widget {
    private final ButtonAction action;
    private final String text;

    public ButtonWidget(float wx, float wy, float ww, float wh, ButtonAction action, String text) {
        super(wx, wy, ww, wh);
        this.action = action;
        this.text = text;
    }

    AnimationUtil clickAnimation = new AnimationUtil(0, 300);
    @Override
    public void render(RenderContext context) {
        super.render(context);
        Color border = context.colorScheme().getBorderColor();
        Color inner = context.colorScheme().PRIMARY();
        Color click = context.colorScheme().SECONDARY();

        inner = clickAnimation.getColor(click, inner);

        Pulse2D.drawRound(context.matrixStack(), x, y, w, h, Pulse2D.cornerRad, border);
        Pulse2D.drawRound(context.matrixStack(), x + Pulse2D.borderWidth, y + Pulse2D.borderWidth, w - Pulse2D.borderWidth * 2, h - Pulse2D.borderWidth * 2, Pulse2D.cornerRad, inner);
        RenderUtil.textRenderer.drawString(context.matrixStack(), text, x + Pulse2D.borderWidth + 2, y + Pulse2D.borderWidth + 2 + RenderUtil.fontOffsetY, context.colorScheme().TEXT().getRGB());
    }

    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        super.mouseInput(key, action, mouseX, mouseY);
        if(hovered && action == GLFW.GLFW_PRESS && key == GLFW.GLFW_MOUSE_BUTTON_1) {
            this.action.run(this);
            clickAnimation.reset();
        }
    }

    @FunctionalInterface
    public interface ButtonAction {
        void run(ButtonWidget widget);
    }
}
