package xyz.qweru.pulse.client.systems.modules.settings.builders;

import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;

public class TextSettingBuilder {
    TextSetting bl = new TextSetting(null, null, null, true);
    public TextSettingBuilder name(String name) { bl.setName(name); return this; }
    public TextSettingBuilder description(String description) { bl.setDescription(description); return this; }
    public TextSettingBuilder defaultValue(String defaultValue) { bl.setValue(defaultValue); return this; }
    public TextSettingBuilder shouldShow(boolean shouldShow) { bl.setShouldShow(shouldShow); return this; }
    public TextSettingBuilder info(String name, String description) { bl.setName(name); bl.setDescription(description); return this; }
    public TextSetting build() { return bl; }
}
