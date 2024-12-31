package xyz.qweru.pulse.client.systems.modules.impl.setting;

import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ModeSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.player.RotationUtil;

@ExcludeModule
public class Rotations extends ClientModule {

    public static ModeSetting MODE = new ModeSettingBuilder()
            .name("Mode")
            .description("Rotation mode")
            .defaultMode("Instant")
            .mode("Instant Lerp")
            .mode("Lerp")
            .mode("Instant")
            .build();

    public static NumberSetting LERP_STEP = numberSetting()
            .info("Lerp step", "Lerp step value")
            .range(0, 1)
            .defaultValue(0.05f)
            .setValueModifier(value -> (float) Util.round(value, 3))
            .build();

    public static NumberSetting ROTATION_DELAY = numberSetting()
            .info("Delay", "Delay in ms between each rotation step (ignored for any instant modes)")
            .range(0, 200)
            .defaultValue(15)
            .stepFullNumbers()
            .build();

    public Rotations() {
        builder(this)
                .name("Rotations")
                .description("Rotation settings")
                .settings(MODE, LERP_STEP, ROTATION_DELAY)
                .category(Category.SETTING);

        MODE.addOnToggle(() -> {
            switch (MODE.getCurrent()) {
                case "Instant" -> RotationUtil.mode = RotationUtil.Mode.INSTANT;
                case "Lerp" -> RotationUtil.mode = RotationUtil.Mode.LERP;
                case "Instant Lerp" -> RotationUtil.mode = RotationUtil.Mode.INSTANT_LERP;
            }
        });

        LERP_STEP.addOnToggle(() -> RotationUtil.LERP_STEP = LERP_STEP.getValue());
    }

}
