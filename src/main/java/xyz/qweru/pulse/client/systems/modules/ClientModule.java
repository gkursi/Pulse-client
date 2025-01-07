package xyz.qweru.pulse.client.systems.modules;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.systems.modules.settings.Setting;
import xyz.qweru.pulse.client.systems.modules.settings.builders.BooleanSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ModeSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.builders.NumberSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.builders.TextSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.SeperatorSetting;
import xyz.qweru.pulse.client.utils.QueueUtil;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.annotations.Status;
import xyz.qweru.pulse.client.utils.player.RotationUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import java.util.ArrayList;
import java.util.List;

@ExcludeModule
public class ClientModule {

    private String name;
    private String description;
    private int bind;
    private Category category;
    private boolean enabled = false;
    private final List<Setting> settings = new ArrayList<>();
    private boolean showSettings = false;
    protected boolean deferEnableToRotationUnlock = false;

    public void deferEnableToRotationUnlock(boolean deferEnableToRotationUnlock) {
        this.deferEnableToRotationUnlock = deferEnableToRotationUnlock;
    }

    public boolean isWaitingForKeybind() {
        return waitingForKeybind;
    }

    public void setWaitingForKeybind(boolean waitingForKeybind) {
        this.waitingForKeybind = waitingForKeybind;
    }

    private boolean waitingForKeybind = false;

    private boolean disableOnExit = false;

    /**
     * Disable module when exiting minecraft
     */
    protected void disableOnExit() {
        disableOnExit = true;
    }

    public boolean shouldDisableOnExit() {
        return disableOnExit;
    }

    public ClientModule(String name, String description, int bind, Category category) {
        this.name = name;
        this.description = description;
        this.bind = bind;
        this.category = category;
    }

    public ClientModule() {
        this.name = "NULL";
        this.description = "NULL";
        this.bind = -1;
        this.category = Category.MISC;
    }

    public void enable() {
        PulseClient.Events.subscribe(this);
        this.enabled = true;
    }

    public void disable() {
        PulseClient.Events.unsubscribe(this);
        this.enabled = false;
    }

    public String getState() {
        return "";
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    protected void addSettings(Setting... settings) {
        this.settings.addAll(List.of(settings));
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public Setting getSettingByName(String settingName) {
        for(Setting s : settings) {
            if(s.getName().equalsIgnoreCase(settingName)) return s;
        }
        return null;
    }

    public boolean shouldShowSettings() {
        return showSettings;
    }

    public void setShowSettings(boolean showSettings) {
        this.showSettings = showSettings;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) enable();
        else disable();
    }

    /**
     * Override to display info on arraylist
     */
    public String getDisplayData() {
        return "";
    }

    public static Builder builder(ClientModule m) {
        return new Builder(m);
    }
    protected Builder builder() {
        return new Builder(this);
    }

    public static class Builder {
        ClientModule cl = new ClientModule(null, null, -1, null);
        public Builder() {}
        public Builder(ClientModule cl) { this.cl = cl; }
        public Builder name(String name) { cl.setName(name); return this; }
        public Builder description(String description) { cl.setDescription(description); return this; }
        public Builder bind(int bind) { cl.setBind(bind); return this; }
        public Builder category(Category category) { cl.setCategory(category); return this; }
        public Builder defaultState(boolean state) { cl.setEnabled(state); return this; }
        public Builder setting(Setting setting) { cl.addSettings(setting); return this; }
        public Builder settings(String catName, Setting... settings) {
            cl.addSettings(new SeperatorSetting(catName));
            cl.addSettings(settings);
            return this;
        }
        public Builder settings(Setting... settings) {
            cl.addSettings(new SeperatorSetting("Settings"));
            cl.addSettings(settings);
            return this;
        }
        public ClientModule build() {
            ModuleManager.INSTANCE.addItem(cl);
            return cl;
        }
    }

    static protected BooleanSettingBuilder booleanSetting() { return new BooleanSettingBuilder(); }
    static protected ModeSettingBuilder modeSetting() { return new ModeSettingBuilder(); }
    static protected NumberSettingBuilder numberSetting() { return new NumberSettingBuilder(); }
    static protected TextSettingBuilder textSetting() { return new TextSettingBuilder(); }
}
