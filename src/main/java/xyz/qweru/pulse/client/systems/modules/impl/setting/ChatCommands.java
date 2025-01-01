package xyz.qweru.pulse.client.systems.modules.impl.setting;

import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;

public class ChatCommands extends ClientModule {

    public static TextSetting PREFIX = new TextSetting("Prefix", "Command prefix", "$", true);

    public ChatCommands() {
        super("Chat commands", "Settings for chat commands", -1, Category.SETTING);
        builder(this).settings(PREFIX);
        setEnabled(true);
    }
}
