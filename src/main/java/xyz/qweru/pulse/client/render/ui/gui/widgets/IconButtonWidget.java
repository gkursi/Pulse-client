package xyz.qweru.pulse.client.render.ui.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.render.Renderer2d;
import net.minecraft.util.Identifier;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

public class IconButtonWidget extends Widget {
    private final Identifier icon;
    private final Runnable action;

    public IconButtonWidget(float wx, float wy, float ww, float wh, Identifier icon, Runnable action) {
        super(wx, wy, ww, wh);
        this.icon = icon;
        this.action = action;
    }

    int hoverTicks = 0;
    boolean hovered = false;
    @Override
    public void render(RenderContext context) {
        Pulse2D.drawRound(context.getMatrices(), x, y, w, h, Pulse2D.cornerRad, Pulse2D.injectAlpha(context.colorScheme().SECONDARY(), (hoverTicks > 15) ? 200 : 200 * hoverTicks / 15));
        RenderSystem.setShaderTexture(0, icon);
        RenderSystem.setShaderColor((float) context.colorScheme().TEXT().getRed() / 255, (float) context.colorScheme().TEXT().getGreen() / 255, (float) context.colorScheme().TEXT().getBlue() / 255, (float) context.colorScheme().TEXT().getAlpha() / 255);
        Renderer2d.renderTexture(context.getMatrices(), x, y, w, h);
        if(hovered) hoverTicks++;
        else {
            if(hoverTicks > 15) hoverTicks = 15;
            else if(hoverTicks > 0) hoverTicks--;
            else hoverTicks = 0;
        }
    }

    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        super.mouseInput(key, action, mouseX, mouseY);
        if(RenderUtil.isInside(mouseX, mouseY, this)) {
            this.action.run();
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        hovered = RenderUtil.isInside(mouseX, mouseY, this);
    }
}
