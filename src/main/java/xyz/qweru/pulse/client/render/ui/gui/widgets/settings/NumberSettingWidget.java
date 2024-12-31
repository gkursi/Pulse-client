package xyz.qweru.pulse.client.render.ui.gui.widgets.settings;

import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;


public class NumberSettingWidget extends SettingWidget {
    private final NumberSetting setting;

    public NumberSettingWidget(float wx, float wy, float ww, float wh, NumberSetting setting) {
        super(wx, wy, ww, wh);
        this.setting = setting;
    }

    float barX = x + 3;
    float barY = y + h - 2;
    float barW = w - 6;
    float barH = 1;

    float textX = x + 3;
    float textY = y;

    TimerUtil blinkTimer = new TimerUtil();
    boolean showBlink = true;
    @Override
    public void render(RenderContext context) {
        if(blinkTimer.hasReached(500)) {
            showBlink =! showBlink;
            blinkTimer.reset();
        }

        float barPercent = getBarPercent();
        Pulse2D.drawRound(context.matrixStack(), barX + (barW * barPercent), barY, barW - barW * barPercent, barH, 1, context.colorScheme().SECONDARY());
        Pulse2D.drawRound(context.matrixStack(), barX, barY, barW * barPercent, barH, 1, context.colorScheme().ACCENT());
        String text = setting.getName() + ": ";
        String number = (inputMode ? tempString : String.valueOf(setting.getValue()));
        if(inputMode) {
            number += showBlink ? "_" : "";
        }

        FontRenderer.ColoredString str = FontRenderer.ColoredString.of(text, context.colorScheme().TEXT());
        str.add(number, context.colorScheme().MUTED_TEXT());

        RenderUtil.textRenderer.drawColoredString(context.matrixStack(), str, textX, textY + RenderUtil.fontOffsetY);
        if(hovered) {
            RenderUtil.textRenderer.drawString(context.matrixStack(), setting.getDescription(), 2, context.screenHeight() - 2 - RenderUtil.textRenderer.getStringHeight(setting.getDescription(), false), context.colorScheme().TEXT().getRGB());
        }
    }

    private float getBarPercent() {
        float barPercent = Math.abs(setting.getValue()) / (Math.abs(setting.getMin()) + setting.getMax());
        if(barPercent == 0) {
            barPercent = Math.abs(setting.getMin()) / (Math.abs(setting.getMin()) + setting.getMax());
        } else if(setting.getValue() < 0) {
            barPercent = (Math.abs(setting.getMin()) / (Math.abs(setting.getMin()) + setting.getMax())) - Math.abs(setting.getValue()) / (Math.abs(setting.getMin()) + setting.getMax());
        } else if(setting.getValue() > 0) {
            if(setting.getMin() < 0) {
                barPercent = (Math.abs(setting.getMin()) / (Math.abs(setting.getMin()) + setting.getMax())) + (setting.getValue() / setting.getMax()) / 2;
            } else {
                barPercent = setting.getValue() / setting.getMax();
            }
        }
        return barPercent;
    }

    boolean isMouseOnBar(double mouseX, double mouseY, String a) {
        float expand = 2.5f;
        return RenderUtil.isInside(mouseX, mouseY, barX - expand, barY - expand, barX + barW + expand, barY + barH + expand);
    }

    boolean active = false;
    boolean inputMode = false;
    String tempString = "";
    TimerUtil doubleClickTimer = new TimerUtil();
    long doubleClickTiming = 500L;
    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        if(RenderUtil.isInside(mouseX, mouseY, x, y, x + w, y + h) && key == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
            if(!doubleClickTimer.hasReached(doubleClickTiming)) {
                PulseClient.LOGGER.info("Double click!");
                inputMode = true;
                blinkTimer.reset();
            } else {
                doubleClickTimer.reset();
            }
        }
        if(isMouseOnBar(mouseX, mouseY, "input")) {
            if(key == GLFW.GLFW_MOUSE_BUTTON_1) {
                switch (action) {
                    case GLFW.GLFW_PRESS -> active = true;
                    case GLFW.GLFW_RELEASE -> active = false;
                }
            }
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if(isMouseOnBar(mouseX, mouseY, "moved") && active) {
            float diff = (float) (mouseX - barX);
            float barPercent = diff / barW;
            if(setting.getMin() < 0) {
                float negPercent = Math.abs(setting.getMin()) / (Math.abs(setting.getMin()) + setting.getMax());
                if(barPercent <= negPercent) setting.setCurrentValue(setting.getMin() * (negPercent - barPercent) * 2);
                else setting.setCurrentValue(setting.getMax() * (barPercent - negPercent) * 2);
            } else setting.setCurrentValue(setting.getMax() * barPercent);
        }
    }

    @Override
    public void input(int keycode, int scancode, int action) {
        if(inputMode && action == GLFW.GLFW_PRESS) {
            if (keycode == InputUtil.KEY_ENTER) {
                float value = 0f;
                try {
                    value = Float.parseFloat(tempString);
                } catch (Exception e) {
                    ChatUtil.err("Error while parsing float, check logs for more info.");
                    PulseClient.throwException(e);
                }

                setting.setCurrentValue(clamp(
                        setting.getMin(),
                        setting.getMax(),
                        value
                ));
                tempString = "";
                inputMode = false;
            } else if (keycode == InputUtil.KEY_BACKSPACE) {
                if(tempString.isEmpty()) return;
                tempString = tempString.substring(0, tempString.length() - 1);
            } else {
                tempString+=InputUtil.getKey(keycode);
            }
        }
    }

    float clamp(float min, float max, float value) {
        return Math.min(max, Math.max(value, min));
    }

    @Override
    public boolean close() {
        inputMode = false;
        tempString = "";
        return true;
    }
}
