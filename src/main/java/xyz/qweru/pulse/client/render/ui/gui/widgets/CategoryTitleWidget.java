package xyz.qweru.pulse.client.render.ui.gui.widgets;

import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ClickGUI;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

public class CategoryTitleWidget extends Widget {
    private final Category category;

    /**
     * Category element
     * @param x x coord
     * @param y y coord
     * @param w width, optimal value = 82
     * @param h height, optimal value = 17
     * @param category target category
     */
    public CategoryTitleWidget(float x, float y, float w, float h, Category category) {
        super(x, y, w, h);
        this.category = category;
    }

    float iconW = h - 4;
    float iconH = iconW;

    @Override
    public void render(RenderContext context) {
        Pulse2D.Elements.container(context, x, y, w, h, false, false);
        if(ClickGUI.icons.isEnabled()) drawIcon(context);
        RenderUtil.textRenderer.drawString(context.matrixStack(), category.label, x + 2 + (ClickGUI.icons.isEnabled() ? iconW + 2 : 0), y + RenderUtil.fontOffsetY, context.colorScheme().TEXT().getRGB());
    }

    void drawIcon(RenderContext context) {
        switch (category) {
            case SETTING -> Pulse2D.Icons.queue(() -> Pulse2D.Icons.config(context, x + 2, y + 2, iconW, iconH));
            case HUD ->Pulse2D.Icons.queue(() -> Pulse2D.Icons.hud(context, x + 2, y + 2, iconW, iconH));
            case MISC -> Pulse2D.Icons.queue(() -> Pulse2D.Icons.misc(context, x + 2, y + 2, iconW, iconH));
            case WORLD -> Pulse2D.Icons.queue(() -> Pulse2D.Icons.world(context, x + 2, y + 2, iconW, iconH));
            case COMBAT -> Pulse2D.Icons.queue(() -> Pulse2D.Icons.combat(context, x + 2, y + 2, iconW, iconH));
            case RENDER -> Pulse2D.Icons.queue(() -> Pulse2D.Icons.render(context, x + 2, y + 2, iconW, iconH));
            case MOVEMENT -> Pulse2D.Icons.queue(() -> Pulse2D.Icons.move(context, x + 2, y + 2, iconW, iconH));
        }
    }
}
