package xyz.qweru.pulse.client.systems.modules.settings.impl;

import xyz.qweru.pulse.client.systems.modules.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public class ModeSetting extends Setting {

    private List<String> modes;
    private String current;
    private int currentIndex = 0;

    public ModeSetting(String name, String description, boolean shouldShow, String defaultMode, String... modes) {
        super(name, description, shouldShow);
        this.modes = modes[0] == null ? new ArrayList<>() : List.of(modes);
        this.current = defaultMode;
    }

    public List<String> getModes() {
        return modes;
    }

    public String getCurrent() {
        return current;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setMode(String m) {
        this.current = m;
        onToggle();
    }

    public void cycle() {
        if (currentIndex > modes.size() - 1) {
            currentIndex = 0;
            current = modes.get(0);
        } else {
            current = modes.get(currentIndex);
        }

        currentIndex++;
        onToggle();
    }

    public void addMode(String mode) {
        modes.add(mode);
    }

    public boolean is(String compareMode) {
        return this.current.equalsIgnoreCase(compareMode);
    }
}
