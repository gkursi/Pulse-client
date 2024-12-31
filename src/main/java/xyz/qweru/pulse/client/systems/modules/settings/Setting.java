package xyz.qweru.pulse.client.systems.modules.settings;

import java.util.concurrent.CopyOnWriteArrayList;

public class Setting {
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShouldShow(boolean shouldShow) {
        this.shouldShow = shouldShow;
    }

    private String description;

    protected boolean shouldShow;

    protected void onToggle() {
        list.forEach(Runnable::run);
    }

    public Setting(String name, String description, boolean shouldShow) {
        this.name = name;
        this.description = description;
        this.shouldShow = shouldShow;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean shouldShow() {
        return shouldShow;
    }

    public void setVisibility(boolean visibility) {
        this.shouldShow = visibility;
    }

    CopyOnWriteArrayList<Runnable> list = new CopyOnWriteArrayList<>();
    public void addOnToggle(Runnable onToggle) {
        list.add(onToggle);
    }
}
