package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.auth.pulse.PulseAuth;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ModeSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Username extends HudModule {

    BooleanSetting showUID = booleanSetting()
            .description("Show your uid (if you owned the client before it went open source)")
            .name("Show UID")
            .build();

    ModeSetting setting = modeSetting()
            .name("Extra")
            .description("Adds extra text")
            .defaultMode("None")
            .mode("Logged in as")
            .mode("on top!")
            .mode("None")
            .build();

    public Username() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(100, 10)
                .getBuilder()
                .name("Username")
                .description("Shows your username")
                .settings(setting, showUID)
                .category(Category.HUD);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        FontRenderer.ColoredString text;
        if(setting.is("Logged in as")) {
            text = FontRenderer.ColoredString.of("Logged in as ", context.colorScheme().TEXT());
            text.add(mc.getSession().getUsername(), context.colorScheme().getLabelColor());
        } else {
            text = FontRenderer.ColoredString.of(mc.getSession().getUsername(), context.colorScheme().getLabelColor());
        }

        if(setting.is("on top!")) text.add(" on top!", context.colorScheme().TEXT());
        if(showUID.isEnabled()) {
            text.add(" (uid ", context.colorScheme().TEXT());
            text.add(PulseAuth.uid == -1 ? "NONE" : String.valueOf(PulseAuth.uid), context.colorScheme().getLabelColor());
            text.add(")", context.colorScheme().TEXT());
        }

        AtomicDouble tw = new AtomicDouble(width);
        AtomicDouble th = new AtomicDouble(height);
        Pulse2D.drawTextHudBase(context, (float) x, (float) y, tw, th, text);
        width = tw.get();
        height = th.get();
    }
}
