package xyz.qweru.pulse.client.systems.modules.impl.render;

import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.annotations.Status;

@ExcludeModule
@Status.Fixme
// outline shader seems to be broken or im using it incorrectly
public class Shaders extends ClientModule {

    public BooleanSetting itemShaders = booleanSetting()
            .name("Item shaders")
            .description("Item shaders")
            .defaultValue(true)
            .build();

    public Shaders() {
        builder(this)
                .name("Shaders")
                .description("Adds shaders")
                .settings(itemShaders)
                .category(Category.RENDER);
    }

}
