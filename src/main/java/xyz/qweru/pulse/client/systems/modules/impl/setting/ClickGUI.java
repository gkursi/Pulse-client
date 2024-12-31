package xyz.qweru.pulse.client.systems.modules.impl.setting;

import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.color.Colors;
import xyz.qweru.pulse.client.render.ui.gui.PulseScreen;
import xyz.qweru.pulse.client.render.ui.gui.screens.MainScreen;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.BooleanSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ModeSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.builders.NumberSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.render.ui.color.ColorScheme;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class ClickGUI extends ClientModule {

    public ModeSetting theme = new ModeSettingBuilder()
            .name("Theme")
            .description("UI colorscheme")
            .shouldShow(true)
            .defaultMode(Colors.DEFAULT.NAME())
            .build();

    public static BooleanSetting blur = new BooleanSettingBuilder()
            .name("Blur")
            .description("Should the gui have blurred background")
            .defaultValue(true)
            .build();

    public static NumberSetting blurStrength = new NumberSettingBuilder()
            .name("Blur strength")
            .description("How much should the gui background be blurred")
            .min(0f)
            .max(10f)
            .defaultValue(5f)
            .stepFullNumbers()
            .shouldShow(true)
            .build();

    public static NumberSetting blurDirection = new NumberSettingBuilder()
            .name("Blur direction")
            .description("Blur direction")
            .min(0f)
            .max(1f)
            .defaultValue(0.5f)
            .shouldShow(true)
            .build();

    public static BooleanSetting roundCorners = new BooleanSettingBuilder()
            .name("Round corners")
            .description("Should the gui have round corners")
            .defaultValue(true)
            .build();

    public static NumberSetting cornerRadius = new NumberSettingBuilder()
            .name("Corner radius")
            .description("Radius for round corners")
            .range(0, 10)
            .defaultValue(1)
            .setValueModifier(value -> (float) Util.round(value, 2))
            .build();

    public static NumberSetting borderWidth = new NumberSettingBuilder()
            .name("Border width")
            .description("Width for (most) borders")
            .range(0, 5)
            .defaultValue(Pulse2D.borderWidth)
            .setValueModifier(value -> (float) Util.round(value, 2))
            .build();

    public static ModeSetting font = new ModeSettingBuilder()
            .name("Font")
            .description("select font for text rendering")
            .defaultMode("Flux-medium")
            .mode("Noto-regular")
            .mode("Flux-bold")
            .mode("Flux-extralight")
            .mode("Flux-medium")
            .mode("Flux-thin")
            .mode("Flux-light")
            .mode("Verdana")
            .mode("Comforta-bold")
            .mode("Comforta-light")
            .mode("Comforta-medium")
            .mode("Comforta-regular")
            .mode("Comforta-semibold")
            .mode("Notosans-light")
            .build();

    public static ModeSetting MSAASamples = new ModeSettingBuilder()
            .name("MSAA samples")
            .description("smaller number = (slightly) better performance. WARNING: not all graphics cards support msaa!!!")
            .defaultMode("Disabled")
            .mode("Disabled")
            .mode("2")
            .mode("4")
            .mode("8")
            .mode("16")
            .mode("32")
            .build();

    public static BooleanSetting ICON_MSAA = new BooleanSetting("Smooth icons", "Use MSAA smoothing for icons (recommended)", false, true);

    public static ModeSetting borderMode = new ModeSettingBuilder()
            .name("Border color")
            .description("Border colors")
            .defaultMode("Accent")
            .mode("Secondary")
            .mode("None")
            .mode("Accent")
            .build();

    public static BooleanSetting icons = new BooleanSettingBuilder()
            .name("Icons")
            .description("Enable icon rendering")
            .defaultValue(true)
            .build();

    public static ModeSetting iconColor = new ModeSettingBuilder()
            .name("Icon color")
            .description("Icon colors")
            .defaultMode("Accent")
            .mode("Secondary")
            .mode("Accent")
            .build();

    public static ModeSetting enabledColor = new ModeSettingBuilder()
            .name("Active color")
            .description("Active module colors")
            .defaultMode("Accent")
            .mode("Secondary")
            .mode("Accent")
            .build();

    public static ModeSetting fontMode = new ModeSettingBuilder()
            .name("Font mode")
            .description("font mode")
            .defaultMode("Regular")
            .mode("All lowercase")
            .mode("Regular")
            .build();

    public static NumberSetting backgroundOpacity = new NumberSettingBuilder()
            .name("Background opacity")
            .description("Opacity for gui background")
            .range(0, 255)
            .defaultValue(190)
            .stepFullNumbers()
            .build();

    public static BooleanSetting colRandomizer = new BooleanSettingBuilder()
            .name("Color noise")
            .description("Make all the colors slightly different")
            .defaultValue(false)
            .build();

    public static BooleanSetting gradient = new BooleanSettingBuilder()
            .name("Gradients")
            .description("Enable gradients")
            .defaultValue(true)
            .build();

    public static BooleanSetting customFontOffset = new BooleanSetting("Use font offset", "Custom font y offset", false, true);

    public static NumberSetting offset = new NumberSettingBuilder()
            .name("Font offset")
            .description("Custom y font offset")
            .range(-25, 25)
            .defaultValue(RenderUtil.fontOffsetY)
            .stepFullNumbers()
            .build();


    public ClickGUI() {
        super("ClickGUI", "Settings for the cgui", GLFW.GLFW_KEY_RIGHT_CONTROL, Category.SETTING);

        builder(this)
                .settings(theme, enabledColor)
                .settings("Blur", blur, blurStrength, blurDirection)
                .settings("Corners", roundCorners, cornerRadius)
                .settings("Font", font, fontMode, customFontOffset, offset)
                .settings("MSAA", MSAASamples, ICON_MSAA)
                .settings("Border", borderMode, borderWidth)
                .settings("Icons", icons, iconColor)
                .settings("Background", backgroundOpacity, colRandomizer, gradient);

        ThreadManager.fixedPool.submit(() -> {
            while (PulseClient.INSTANCE == null);
            for (ColorScheme scheme : PulseClient.INSTANCE.themeManager.getItemList()) {
                theme.addMode(scheme.NAME());
            }
        });

        theme.addOnToggle(() -> {
            PulseClient.INSTANCE.themeManager.setTheme(theme.getCurrent());
        });

        roundCorners.addOnToggle(() -> {
            Pulse2D.cornerRad = roundCorners.isEnabled() ? cornerRadius.getValue() : 0;
        });

        cornerRadius.addOnToggle(() -> {
            Pulse2D.cornerRad = roundCorners.isEnabled() ? cornerRadius.getValue() : 0;
        });

        font.addOnToggle(() -> {
            RenderUtil.curFontName = font.getCurrent();
            RenderUtil.updateFont();
        });

        borderWidth.addOnToggle(() -> {
            Pulse2D.borderWidth = borderWidth.getValue();
        });

        offset.addOnToggle(() -> {
            if(customFontOffset.isEnabled()) RenderUtil.fontOffsetY = offset.getValue();
        });

        customFontOffset.addOnToggle(() -> {
            if(customFontOffset.isEnabled()) RenderUtil.fontOffsetY = offset.getValue();
            else RenderUtil.updateFont();
        });
    }

    @Override
    public void enable() {
        super.enable();
        if (mc.currentScreen instanceof PulseScreen) {
            mc.setScreen(null);
        } else {
            mc.setScreen(PulseClient.INSTANCE.windowManager.getItemByClass(MainScreen.class));
        }
        this.toggle();
    }
}
