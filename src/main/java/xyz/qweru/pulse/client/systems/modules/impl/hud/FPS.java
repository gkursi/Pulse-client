package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.impl.setting.HudEditor;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class FPS extends HudModule {

    public FPS() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(30, 13)
                .getBuilder()
                .name("Fps")
                .description("Shows your fps")
                .category(Category.HUD);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        int fps = mc.getCurrentFps();
        AtomicDouble nW = new AtomicDouble(width);
        AtomicDouble nH = new AtomicDouble(height);

        FontRenderer.ColoredString string = FontRenderer.ColoredString.of("FPS: ", context.colorScheme().getLabelColor());
        string.add(fps + "", context.colorScheme().MUTED_TEXT());
        Pulse2D.drawTextHudBase(context, (float) x, (float) y, nW, nH, string);

        width = nW.get();
        height = nH.get();
    }
}
