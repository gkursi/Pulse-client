package xyz.qweru.pulse.client.render.ui.gui.widgets.settings;

import net.minecraft.util.math.Vec2f;
import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.render.AnimationUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import java.awt.*;

public class BooleanSettingWidget extends SettingWidget{
    private final BooleanSetting setting;

    public BooleanSettingWidget(float wx, float wy, float ww, float wh, BooleanSetting setting) {
        super(wx, wy, ww, wh);
        this.setting = setting;
    }

    @Override
    public void render(RenderContext context) {
        Vec2f textPos = RenderUtil.textRenderer.calcPosInBox(setting.getName(), x, y, w, h);
        Pulse2D.drawRound(context.matrixStack(), x + 3, y + 2, h - 4, h - 4, Pulse2D.cornerRad, context.colorScheme().ACCENT());
        Pulse2D.drawRound(context.matrixStack(), x + 4, y + 3, h - 6, h - 6, Pulse2D.cornerRad, getFill(context));
        RenderUtil.textRenderer.drawString(context.matrixStack(), setting.getName(), x + 4 + h - 4 + 2, textPos.y + RenderUtil.fontOffsetY, context.colorScheme().TEXT().getRGB());
        if(hovered) {
            RenderUtil.textRenderer.drawString(context.matrixStack(), setting.getDescription(), 2, context.screenHeight() - 2 - RenderUtil.textRenderer.getStringHeight(setting.getDescription(), false), context.colorScheme().TEXT().getRGB());
        }
    }

    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        if(key == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
            if(RenderUtil.isInside(mouseX, mouseY, x + 5, y + 3, x + 5 + 10, y + 3 + 10)) {
                setting.setState(!setting.isEnabled());
                animation.reset();
            }
        }
    }

    AnimationUtil animation = new AnimationUtil(0, 500);
    public Color getFill(RenderContext context) {
        if(setting.isEnabled()) return animation.getColor(context.colorScheme().PRIMARY(), context.colorScheme().SECONDARY());
        else return animation.getColor(context.colorScheme().SECONDARY(), context.colorScheme().PRIMARY());
    }
}
