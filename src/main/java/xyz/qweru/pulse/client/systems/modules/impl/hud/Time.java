package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.managers.Manager;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.utils.VarManger;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

public class Time extends HudModule {

    ModeSetting mode = modeSetting()
            .name("Mode")
            .description("Display mode")
            .defaultMode("min:hour")
            .mode("dd/mm/yyyy")
            .mode("m:h d. m")
            .mode("full")
            .mode("min:hour")
            .build();

    public Time() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(100, 10)
                .getBuilder()
                .name("Time")
                .description("Show time")
                .category(Category.HUD)
                .settings("Settings", mode);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        String text = switch (mode.getCurrent()) {
            case "min:hour" -> Managers.VARIABLE.TIME$MINUTE_HOUR;
            case "dd/mm/yyyy" -> Managers.VARIABLE.TIME$DATE_MONTH_YEAR;
            case "m:h d. m" -> Managers.VARIABLE.TIME$MINUTE_HOUR_DATE_MONTH;
            case "full" -> Managers.VARIABLE.TIME$FULL;
            default -> throw new IllegalStateException("Unexpected value: " + mode.getCurrent());
        };

        AtomicDouble tw = new AtomicDouble(width);
        AtomicDouble th = new AtomicDouble(height);
        Pulse2D.drawTextHudBase(context, (float) x, (float) y, tw, th, text);
        width = tw.get();
        height = th.get();
    }
}
