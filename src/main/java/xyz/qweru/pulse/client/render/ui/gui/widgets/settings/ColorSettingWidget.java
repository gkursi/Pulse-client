package xyz.qweru.pulse.client.render.ui.gui.widgets.settings;

import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.screens.BetterColorScreen;
import xyz.qweru.pulse.client.render.ui.gui.screens.ColorScreen;
import xyz.qweru.pulse.client.systems.modules.settings.Setting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class ColorSettingWidget extends SettingWidget {

    ColorSetting setting;

    public ColorSettingWidget(float wx, float wy, float ww, float wh, ColorSetting setting) {
        super(wx, wy, ww, wh);
        this.setting = setting;
    }

    RenderContext prev = null;
    @Override
    public void render(RenderContext context) {
        RenderUtil.textRenderer.drawString(
                context.matrixStack(), setting.getName(),
                this.x + 3, this.y + 2 + RenderUtil.fontOffsetY,
                context.colorScheme().TEXT().getRGB());

        Pulse2D.drawRound(
                context.matrixStack(),
                this.x + this.w - RenderUtil.textRenderer.getStringWidth("Edit", false) - 6 - Pulse2D.borderWidth * 3 ,
                y + 2,
                this.w - (this.w - RenderUtil.textRenderer.getStringWidth("Edit", false) - 4 - Pulse2D.borderWidth * 3),
                this.h - 4,
                Pulse2D.cornerRad,
                setting.getJavaColor()
        );
        prev = context;
    }

    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        if(prev == null) return;
        if(key == InputUtil.MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS
                && RenderUtil.isInside(mouseX, mouseY,
                    x,
                y,
                x + w,
                y + h
                )
        ) {
            mc.setScreen(new ColorScreen(prev.parent(), setting));
        }
    }
}
