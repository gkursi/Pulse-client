package xyz.qweru.pulse.client.systems.modules.impl.setting;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.ui.gui.screens.HudConfigScreen;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ModeSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.builders.NumberSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class HudEditor extends ClientModule {
    public HudEditor() {
        builder(this)
                .name("Hud")
                .description("edit the hud")
                .settings(glow, outline, color, snap, hudMode, textColor)
                .category(Category.SETTING);
    }

    public static NumberSetting glow = new NumberSettingBuilder()
            .name("Glow")
            .description("Edit hud glow amount")
            .range(0, 1)
            .defaultValue(0.7f)
            .setValueModifier(value -> (float) Util.round(value, 2))
            .build();

    public static NumberSetting outline = new NumberSettingBuilder()
            .name("Outline")
            .description("Edit hud outline")
            .range(0, 1)
            .defaultValue(0.7f)
            .setValueModifier(value -> (float) Util.round(value, 2))
            .build();

    public static ModeSetting color = new ModeSettingBuilder()
            .name("Color")
            .description("Color setting")
            .defaultMode("Primary")
            .mode("Accent")
            .mode("Rainbow")
            .mode("None")
            .mode("Primary")
            .build();

    public static ModeSetting textColor = new ModeSettingBuilder()
            .name("Label color")
            .description("Text color for labels (labels are things like X: .., Y: .., Z: .., FPS: ...)")
            .defaultMode("Text")
            .mode("Accent")
            .mode("Secondary")
            .mode("Rainbow")
            .mode("Text")
            .build();

    public static ModeSetting hudMode = new ModeSettingBuilder()
            .name("Mode")
            .description("Hud mode")
            .defaultMode("Normal")
            .mode("Minimal")
            .mode("None")
            .mode("Normal")
            .build();


    public static BooleanSetting snap = new BooleanSetting("Snap", "Should snap to edges", true, true);

    @Override
    public void enable() {
        mc.setScreen(PulseClient.INSTANCE.windowManager.getItemByClass(HudConfigScreen.class));
        this.toggle();
    }
}
