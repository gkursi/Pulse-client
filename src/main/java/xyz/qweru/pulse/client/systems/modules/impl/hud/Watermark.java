package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Watermark extends HudModule {

    BooleanSetting showUsername = booleanSetting()
            .description("Show your username")
            .name("Show username")
            .build();

    BooleanSetting showAuthor = booleanSetting()
            .description("Show the client author")
            .name("Show author")
            .build();

    public Watermark() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(100, 10)
                .getBuilder()
                .name("Watermark")
                .description("Client watermark")
                .category(Category.HUD)
                .settings(showAuthor);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        FontRenderer.ColoredString text = FontRenderer.ColoredString.of(PulseClient.NAME + " ", context.colorScheme().getLabelColor());
        text.add(PulseClient.VERSION, context.colorScheme().TEXT());
        if(showAuthor.isEnabled()) {
            text.add(" by ", context.colorScheme().TEXT());
            text.add(PulseClient.AUTHOR, context.colorScheme().getLabelColor());
        }

        AtomicDouble tw = new AtomicDouble(width);
        AtomicDouble th = new AtomicDouble(height);
        Pulse2D.drawTextHudBase(context, (float) x, (float) y, tw, th, text);
        width = tw.get();
        height = th.get();
    }
}
