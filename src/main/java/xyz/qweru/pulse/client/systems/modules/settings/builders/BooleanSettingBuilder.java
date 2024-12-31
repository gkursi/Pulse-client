package xyz.qweru.pulse.client.systems.modules.settings.builders;

import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;

public class BooleanSettingBuilder {
    BooleanSetting bl = new BooleanSetting(null, null, false, true);
    public BooleanSettingBuilder name(String name) { bl.setName(name); return this; }
    public BooleanSettingBuilder description(String description) { bl.setDescription(description); return this; }
    public BooleanSettingBuilder defaultValue(boolean defaultValue) { bl.setState(defaultValue); return this; }
    public BooleanSettingBuilder shouldShow(boolean shouldShow) { bl.setShouldShow(shouldShow); return this; }
    public BooleanSettingBuilder info(String name, String description) { bl.setName(name); bl.setDescription(description); return this; }
    public BooleanSetting build() { return bl; }
}
