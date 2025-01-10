package xyz.qweru.pulse.client.systems.modules.impl.misc;

import net.minecraft.util.math.MathHelper;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.BooleanSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;
import xyz.qweru.pulse.client.utils.render.AnimationUtil;

public class Chat extends ClientModule {

    public static TextSetting prefix = new TextSetting("Prefix text", "prefix text", "-# ", true);
    public static TextSetting suffix = new TextSetting("Suffix text", "Will be appended to all chat", " ⎥ pulse on top!", true);
    public static BooleanSetting fancyChat = new BooleanSettingBuilder()
            .name("Fancy chat")
            .description("Replaces your chars with ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴩqʀꜱᴛᴜᴠᴡxyᴢ")
            .defaultValue(false)
            .shouldShow(true)
            .build();
    public static BooleanSetting onlySuffix = new BooleanSettingBuilder()
            .name("Only suffix")
            .description("Only make the suffix 'fancy'")
            .defaultValue(false)
            .build();

    public static BooleanSetting timestamps = new BooleanSettingBuilder()
            .name("Timestamps")
            .description("Adds timestamps to chat")
            .build();

    public static BooleanSetting noBG = new BooleanSettingBuilder()
            .name("No background")
            .description("Remove chat background")
            .build();

    public static BooleanSetting font = new BooleanSettingBuilder()
            .name("Font")
            .description("Use custom font")
            .build();

    public static BooleanSetting animation = new BooleanSettingBuilder()
            .name("Animation")
            .description("All new messages have an animation")
            .build();

    public Chat() {
        super("Chat", "Chat options", -1, Category.MISC);
        builder(this).settings(suffix, prefix,fancyChat, onlySuffix, timestamps, font, noBG)
                .settings("Animation", animation);

        fancyChat.addOnToggle(() -> onlySuffix.setShouldShow(fancyChat.isEnabled()));
    }

}
