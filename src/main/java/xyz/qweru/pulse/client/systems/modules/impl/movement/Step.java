package xyz.qweru.pulse.client.systems.modules.impl.movement;

import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;

public class Step extends ClientModule {
    public static NumberSetting height = numberSetting()
            .name("Height")
            .description("Step height")
            .defaultValue(1f)
            .range(0, 10)
            .stepFullNumbers()
            .build();

    public Step() {
        builder()
                .name("Step")
                .description("Increase step height")
                .category(Category.MOVEMENT)
                .settings(height);
    }
}
