package xyz.qweru.pulse.client.systems.modules.impl.setting;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.gui.screens.HudConfigScreen;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ModeSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.builders.NumberSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Rainbows extends ClientModule {

    public Rainbows() {
        builder(this)
                .name("Rainbow")
                .description("rainbow settings")
                .settings(speed, saturation, brightness)
                .settings("Sky", skySpeed)
                .category(Category.SETTING);
    }

    public static NumberSetting speed = new NumberSettingBuilder()
            .name("Speed")
            .description("Rainbow speed")
            .range(0.1F, 10)
            .defaultValue(0.75f)
            .setValueModifier(value -> (float) Util.round(value, 3))
            .build();

    public static NumberSetting skySpeed = new NumberSettingBuilder()
            .name("Sky Speed")
            .description("Sky Rainbow speed")
            .range(1, 100)
            .defaultValue(25)
            .setValueModifier(value -> (float) Util.round(value, 0))
            .build();

    public static NumberSetting saturation = new NumberSettingBuilder()
            .name("Saturation")
            .description("Rainbow saturation")
            .range(0.1F, 10)
            .defaultValue(0.45f)
            .setValueModifier(value -> (float) Util.round(value, 3))
            .build();

    public static NumberSetting brightness = new NumberSettingBuilder()
            .name("Brightness")
            .description("Rainbow brightness")
            .range(0.1F, 10)
            .defaultValue(0.45f)
            .setValueModifier(value -> (float) Util.round(value, 3))
            .build();

    public static Color getRainbow(double xOffset, double yOffset, double speed, double transition, double saturation, double brightness) {
        double offset = transition * (xOffset + yOffset);
        return Pulse2D.rainbow(offset, (float) saturation, (float) brightness, (float) speed);
    }

    public static Color getRainbow(double xOffset, double yOffset) {
        return getRainbow(xOffset, yOffset, speed.getValueDouble(), 1000, saturation.getValueDouble(), brightness.getValueDouble());
    }

}
