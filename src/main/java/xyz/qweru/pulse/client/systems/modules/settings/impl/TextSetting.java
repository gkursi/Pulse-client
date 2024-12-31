package xyz.qweru.pulse.client.systems.modules.settings.impl;

import xyz.qweru.pulse.client.systems.modules.settings.Setting;

public class TextSetting extends Setting {

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.onToggle();
    }

    private String value;

    public TextSetting(String name, String description, String defaultValue, boolean shouldShow) {
        super(name, description, shouldShow);
        this.value = defaultValue;
    }


}
