package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

public class PlayerCTL extends HudModule {

    boolean scroll = true;
    public PlayerCTL() {
        hudBuilderOf(this)
                .area(100, 20)
                .getBuilder()
                .name("Playerctl")
                .description("LINUX ONLY, displays currently playing media via playerCTL")
                .category(Category.HUD);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        AtomicDouble w = new AtomicDouble(width);
        AtomicDouble h = new AtomicDouble(height);
        FontRenderer.ColoredString text = FontRenderer.ColoredString.of("Playing: ", context.colorScheme().TEXT());
        text.add(Managers.VARIABLE.SONGDATA$SONG, context.colorScheme().getLabelColor());
        text.add(" by ", context.colorScheme().TEXT());
        text.add(Managers.VARIABLE.SONGDATA$ARTIST, context.colorScheme().getLabelColor());
        Pulse2D.drawTextHudBase(context, (float) x, (float) y, w, h, text);
        this.width = w.get();
        this.height = h.get();
    }
}
