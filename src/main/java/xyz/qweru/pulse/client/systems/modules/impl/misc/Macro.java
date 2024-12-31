package xyz.qweru.pulse.client.systems.modules.impl.misc;

import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.BooleanSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.ChatUtil;

public class Macro extends ClientModule {

    public static TextSetting suffix = new TextSetting("Text", "What command / message to send", "/dupe 64", true);

    public Macro() {
        super("Macro", "send message / command with a keybind", -1, Category.MISC);
        builder(this).settings(suffix);
    }

    @Override
    public void enable() {
        super.enable();

        if(Util.nullCheck()) {
            toggle();
            return;
        }
        ChatUtil.sendServerMsg(suffix.getValue());

        this.toggle();
    }
}
