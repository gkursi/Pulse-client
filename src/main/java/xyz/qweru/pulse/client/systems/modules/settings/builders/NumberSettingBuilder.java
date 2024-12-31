package xyz.qweru.pulse.client.systems.modules.settings.builders;

import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;

public class NumberSettingBuilder {
    NumberSetting bl = new NumberSetting(null, null, 0f, 0f, 0f, true);
    public NumberSettingBuilder name(String name) { bl.setName(name); return this; }
    public NumberSettingBuilder description(String description) { bl.setDescription(description); return this; }
    public NumberSettingBuilder info(String name, String description) { bl.setName(name); bl.setDescription(description); return this; }
    public NumberSettingBuilder defaultValue(float defaultValue) { bl.setCurrentValue(defaultValue); return this; }
    public NumberSettingBuilder min(float min) { bl.setMin(min); return this; }
    public NumberSettingBuilder max(float max) { bl.setMax(max); return this; }
    public NumberSettingBuilder shouldShow(boolean shouldShow) { bl.setShouldShow(shouldShow); return this; }
    public NumberSettingBuilder stepFullNumbers() { bl.setValueModifier((value -> (float) Math.floor(value))); return this; }
    public NumberSettingBuilder setValueModifier(NumberSetting.ValueModifierRunnable mod) { bl.setValueModifier(mod); return this; }
    public NumberSettingBuilder range(float min, float max) {
        bl.setMin(min);
        bl.setMax(max);
        return this;
    }
    public NumberSetting build() { return bl; }
}
