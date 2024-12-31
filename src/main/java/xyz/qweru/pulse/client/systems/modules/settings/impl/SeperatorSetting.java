package xyz.qweru.pulse.client.systems.modules.settings.impl;

import xyz.qweru.pulse.client.systems.modules.settings.Setting;

public class SeperatorSetting extends Setting {

    private final String title;

    public SeperatorSetting() {
        this(null);
    }

    public String getTitle() {
        return title;
    }

    public SeperatorSetting(String title) {
        super("name", "description", true);
        this.title = title;
    }
}
