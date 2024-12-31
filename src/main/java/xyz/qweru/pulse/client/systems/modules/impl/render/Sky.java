package xyz.qweru.pulse.client.systems.modules.impl.render;

import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ColorSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;

import java.awt.*;

public class Sky extends ClientModule {

    ModeSetting color = modeSetting()
            .name("Color mode")
            .description("Color mode")
            .defaultMode("Custom")
            .mode("Rainbow")
            .mode("Custom")
            .build();

    ColorSetting colorSetting = new ColorSettingBuilder()
            .setName("Color")
            .setDescription("Custom color")
            .build();

    public Sky() {
        builder()
                .name("Sky")
                .description("Better sky")
                .settings("Color", color, colorSetting)
                .category(Category.RENDER);
    }

    public static Color getSkyColor(Color previous) {
        if(Managers.MODULE.getItemByClass(Sky.class).isEnabled()) {
            Sky sky = (Sky)Managers.MODULE.getItemByClass(Sky.class);
            if(sky.color.is("Custom")) return sky.colorSetting.getJavaColor();
            else if(sky.color.is("Rainbow")) return Pulse2D.skyRainbow(25, 1);
        }

        return previous;
    }

}
