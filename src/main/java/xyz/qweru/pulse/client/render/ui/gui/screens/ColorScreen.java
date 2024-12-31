package xyz.qweru.pulse.client.render.ui.gui.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.ui.color.ColorScheme;
import xyz.qweru.pulse.client.render.ui.color.Colors;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.render.ui.gui.PulseScreen;
import xyz.qweru.pulse.client.render.ui.gui.widgets.ButtonWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.CategoryBackgroundWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.settings.NumberSettingWidget;
import xyz.qweru.pulse.client.systems.modules.settings.builders.NumberSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.annotations.Status;

import static xyz.qweru.pulse.client.PulseClient.mc;

@Status.MarkedForUpgrade
public class ColorScreen extends PulseScreen {
    private final PulseScreen parent;
    private final ColorSetting setting;
    boolean setColor = false;

    static ColorScheme scheme = Colors.DEFAULT;

    public ColorScreen(PulseScreen parent, ColorSetting setting) {
        super("color");
        this.parent = parent;
        this.setting = setting;
        if(parent == null || setting == null) setColor = true;
    }

    NumberSetting redSet;
    NumberSetting greenSet;
    NumberSetting blueSet;
    NumberSetting alphaSet;

    @Override
    protected void init() {
        widgetList.clear();
        PulseClient.LOGGER.debug("opened window");
        this.shouldClose = false;
        float width = 100;
        float height = 92;
        float spacing = 2;

        float x = Math.max(((float) mc.getWindow().getScaledWidth() - width) / 2, 0);
        float y = Math.max((mc.getWindow().getScaledHeight() - height * 2 - 2) / 2, 0);
        CategoryBackgroundWidget cbw = new CategoryBackgroundWidget(x, y, width, height);
        CategoryBackgroundWidget colorDisplay = new CategoryBackgroundWidget(x, y + height + 2, width, height);

        colorDisplay.altColor = true;
        colorDisplay.color = setting.getJavaColor();

        widgetList.add(cbw);
        widgetList.add(colorDisplay);
        setting.addOnToggle(() -> colorDisplay.color = setting.getJavaColor());

        redSet = new NumberSettingBuilder()
                .name("Red")
                .description(".")
                .range(0, 255)
                .defaultValue(setting.getRed().asInt())
                .stepFullNumbers()
                .build();

        greenSet = new NumberSettingBuilder()
                .name("Green")
                .description(".")
                .range(0, 255)
                .defaultValue(setting.getGreen().asInt())
                .stepFullNumbers()
                .build();

        blueSet = new NumberSettingBuilder()
                .name("Blue")
                .description(".")
                .range(0, 255)
                .defaultValue(setting.getBlue().asInt())
                .stepFullNumbers()
                .build();

        alphaSet = new NumberSettingBuilder()
                .name("Alpha")
                .description(".")
                .range(0, 255)
                .defaultValue(setting.getAlpha().asInt())
                .stepFullNumbers()
                .build();

        redSet.addOnToggle(() -> {
            setting.setRed(redSet.getValueInt());
            colorDisplay.color = setting.getJavaColor();
        });

        greenSet.addOnToggle(() -> {
            setting.setGreen(greenSet.getValueInt());
            colorDisplay.color = setting.getJavaColor();
        });

        blueSet.addOnToggle(() -> {
            setting.setBlue(blueSet.getValueInt());
            colorDisplay.color = setting.getJavaColor();
        });

        alphaSet.addOnToggle(() -> {
            setting.setAlpha(alphaSet.getValueInt());
            colorDisplay.color = setting.getJavaColor();
        });

        NumberSettingWidget redW = new NumberSettingWidget(x + 2, y + 2, width - 4, 16, redSet);
        NumberSettingWidget greenW = new NumberSettingWidget(x + 2, y + 16 + 2 * 2, width - 4, 16, greenSet);
        NumberSettingWidget blueW = new NumberSettingWidget(x + 2, y + 16 * 2 + 2 * 3, width - 4, 16, blueSet);
        NumberSettingWidget alphaW = new NumberSettingWidget(x + 2, y + 16 * 3 + 2 * 4, width - 4, 16, alphaSet);
        widgetList.add(redW);
        widgetList.add(greenW);
        widgetList.add(blueW);
        widgetList.add(alphaW);

        widgetList.add(new ButtonWidget(x + 2, y + 16 * 4 + 2 * 5, width - 4, 16, widget -> {
            redSet.setCurrentValue(ThemeInfo.COLORSCHEME.ACCENT().getRed());
            greenSet.setCurrentValue(ThemeInfo.COLORSCHEME.ACCENT().getGreen());
            blueSet.setCurrentValue(ThemeInfo.COLORSCHEME.ACCENT().getBlue());
            alphaSet.setCurrentValue(180);
        }, "Default"));
    }

    @Override
    public void close() {
        super.close();
        mc.setScreen(parent);
    }

    @Override
    public void setColorScheme(ColorScheme colorScheme) {
        if(setColor) scheme = colorScheme;
        super.setColorScheme(colorScheme);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        colorScheme = scheme;
        super.render(context, mouseX, mouseY, delta);
    }
}
