package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Ping extends HudModule {

    public Ping() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(30, 13)
                .getBuilder()
                .name("Ping")
                .description("Shows your ping")
                .category(Category.HUD);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        int fps = Managers.VARIABLE.PING;
        AtomicDouble nW = new AtomicDouble(width);
        AtomicDouble nH = new AtomicDouble(height);

        FontRenderer.ColoredString string = FontRenderer.ColoredString.of("Ping: ", context.colorScheme().getLabelColor());
        string.add(fps + "", context.colorScheme().MUTED_TEXT());
        Pulse2D.drawTextHudBase(context, (float) x, (float) y, nW, nH, string);

        width = nW.get();
        height = nH.get();
    }
}
