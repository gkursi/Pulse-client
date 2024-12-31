package xyz.qweru.pulse.client.systems.modules.settings.builders;

import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;

public class ModeSettingBuilder {
    ModeSetting ms = new ModeSetting(null, null, true, null, null, null);
    public ModeSettingBuilder name(String name) { ms.setName(name); return this; }
    public ModeSettingBuilder description(String description) {ms.setDescription(description); return this; }
    public ModeSettingBuilder mode(String mode) { ms.addMode(mode); return this; }
    public ModeSettingBuilder defaultMode(String mode) { ms.setMode(mode); return this; }
    public ModeSettingBuilder shouldShow(boolean shouldShow) { ms.setShouldShow(shouldShow); return this; }
    public ModeSettingBuilder info(String name, String description) { ms.setName(name); ms.setDescription(description); return this; }
    public ModeSetting build() { return ms; }
}
