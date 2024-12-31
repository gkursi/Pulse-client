package xyz.qweru.pulse.client.systems.modules.settings.impl;

import xyz.qweru.pulse.client.systems.modules.settings.Setting;

public class BooleanSetting extends Setting {
    private boolean state = false;

    public BooleanSetting(String name, String description, boolean defaultValue, boolean shouldShow) {
        super(name, description, shouldShow);
        this.state = defaultValue;
    }

    public boolean isEnabled() {
        return state;
    }

    public void setState(boolean state) {
//        PulseClient.LOGGER.info("boolset state: {}", state);
        this.state = state;
        onToggle();
    }
}
