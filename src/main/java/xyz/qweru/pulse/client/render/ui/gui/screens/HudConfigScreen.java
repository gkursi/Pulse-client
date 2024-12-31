package xyz.qweru.pulse.client.render.ui.gui.screens;

import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.render.ui.gui.PulseScreen;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.impl.setting.HudEditor;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

public class HudConfigScreen extends PulseScreen {

    public HudConfigScreen() {
        super("Hud config");
        blur = false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button != InputUtil.MOUSE_BUTTON_1) return false;
        for (ClientModule clientModule : ModuleManager.INSTANCE.getItemList()) {
            if(clientModule instanceof HudModule hm) {
                if(!hm.isEnabled()) continue;
                if(RenderUtil.isInside(mouseX, mouseY, (float) hm.getX(), (float) hm.getY(), (float) (hm.getX() + hm.getWidth()), (float) (hm.getY() + hm.getHeight()))) {
                    hm.dragging = true;
                    hm.mouseOffsetX = (int) (mouseX - hm.getX());
                    hm.mouseOffsetY = (int) (mouseY - hm.getY());
                }
            }
        }
        return true;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (ClientModule clientModule : ModuleManager.INSTANCE.getItemList()) {
            if(clientModule instanceof HudModule hm) {
                if(hm.dragging) {
                    hm.setX((int) mouseX - hm.mouseOffsetX);
                    hm.setY((int) mouseY - hm.mouseOffsetY);
                }
            }
        }

        if(!HudEditor.snap.isEnabled()) return;

        for (ClientModule clientModule : ModuleManager.INSTANCE.getItemList()) {
            if(clientModule instanceof HudModule hm && hm.isEnabled()) {
                if(hm.getX() < 4 && hm.getX() > -4) hm.setX(1);
                if(hm.getY() < 4 && hm.getY() > -4) hm.setY(1);

                if(hm.getWidth() < width + 4 && hm.getWidth() > width - 4) hm.setX(width - 1 - hm.getWidth());
                if(hm.getHeight() < height + 4 && hm.getHeight() > height - 4) hm.setY(height - 1 - hm.getHeight());
            }
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(button != InputUtil.MOUSE_BUTTON_1) return false;
        for (ClientModule clientModule : ModuleManager.INSTANCE.getItemList()) {
            if(clientModule instanceof HudModule hm) {
                hm.dragging = false;
                hm.mouseOffsetX = 0;
                hm.mouseOffsetY = 0;
            }
        }
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Renderer2d.renderLine(context.getMatrices(), ThemeInfo.COLORSCHEME.getBorderColor(),
                0, (double) context.getScaledWindowHeight() / 2, context.getScaledWindowWidth(), (double) context.getScaledWindowHeight() / 2);
        Renderer2d.renderLine(context.getMatrices(), ThemeInfo.COLORSCHEME.getBorderColor(),
                (double) context.getScaledWindowWidth() / 2, 0, (double) context.getScaledWindowWidth() / 2, context.getScaledWindowHeight());

    }
}
