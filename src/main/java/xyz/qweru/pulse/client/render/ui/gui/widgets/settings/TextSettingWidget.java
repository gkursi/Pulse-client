package xyz.qweru.pulse.client.render.ui.gui.widgets.settings;

import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;


public class TextSettingWidget extends SettingWidget {
    private final TextSetting setting;

    public TextSettingWidget(float wx, float wy, float ww, float wh, TextSetting setting) {
        super(wx, wy, ww, wh);
        this.setting = setting;
    }

    TimerUtil blinkTimer = new TimerUtil();
    boolean showBlink = true;
    @Override
    public void render(RenderContext context) {
        if(blinkTimer.hasReached(500)) {
            showBlink =! showBlink;
            blinkTimer.reset();
        }

        String text = setting.getName() + ": " + (inputMode ? tempString : setting.getValue());
        if(inputMode) {
            text += showBlink ? "_" : "";
        }

        FontRenderer.ColoredString string = FontRenderer.ColoredString.of(setting.getName() + ": ", context.colorScheme().TEXT());
        string.add((inputMode ? tempString : setting.getValue()), context.colorScheme().MUTED_TEXT());

        RenderUtil.textRenderer.drawString(context.matrixStack(), text, x + 2, y + (h - string.getHeight()) / 2 + RenderUtil.fontOffsetY, context.colorScheme().TEXT().getRGB());
        if(hovered) {
            RenderUtil.textRenderer.drawString(context.matrixStack(), setting.getDescription(), 2, context.screenHeight() - 2 - RenderUtil.textRenderer.getStringHeight(setting.getDescription(), false), context.colorScheme().TEXT().getRGB());
        }
    }

    boolean inputMode = false;
    String tempString = "";
    TimerUtil doubleClickTimer = new TimerUtil();
    long doubleClickTiming = 500L;
    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        if(RenderUtil.isInside(mouseX, mouseY, x, y, x + w, y + h) && key == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
            if(!doubleClickTimer.hasReached(doubleClickTiming)) {
                inputMode = true;
                tempString = setting.getValue();
                blinkTimer.reset();
            } else {
                doubleClickTimer.reset();
            }
        }
    }

    // modifiers
    boolean shift = false;

    @Override
    public void input(int keycode, int scancode, int action) {
        if(inputMode && action == GLFW.GLFW_PRESS) {
            if (keycode == InputUtil.KEY_ENTER) {
                setting.setValue(tempString);
                tempString = "";
                inputMode = false;
            } else if (keycode == InputUtil.KEY_BACKSPACE) {
                if(tempString.isEmpty()) return;
                tempString = tempString.substring(0, tempString.length() - 1);
            } else if (keycode == InputUtil.KEY_LEFT_SHIFT) {
                shift = true;
            } else {
                tempString+=InputUtil.getInputString(keycode, shift);
            }
        } else if(inputMode && action == GLFW.GLFW_RELEASE) {
            if (keycode == InputUtil.KEY_LEFT_SHIFT) {
                shift = false;
            }
        }
    }

    @Override
    public boolean close() {
        inputMode = false;
        tempString = "";
        return true;
    }
}
