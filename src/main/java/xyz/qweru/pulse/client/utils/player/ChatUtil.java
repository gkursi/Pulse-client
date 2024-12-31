package xyz.qweru.pulse.client.utils.player;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.mixin.iinterface.IChatHud;
import xyz.qweru.pulse.client.systems.modules.impl.hud.Notifications;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class ChatUtil {

    public static Text PREFIX = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[")
            .append(
                    Text.literal("Pulse").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(
                            PulseClient.COLOR.getRGB()
                    )))
            )
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("] ");

    public static Text PREFIX_UNFINISHED = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("[")
            .append(
                    Text.literal("Pulse").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(
                            PulseClient.COLOR.getRGB()
                    )))
            )
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append(Text.literal(" > ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));

    public static Text PREFIX_END = Text.empty()
            .setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
            .append("] ");

    public static void sendServerMsg(String msg) {
        if (!msg.startsWith("/")) mc.inGameHud.getChatHud().addToMessageHistory(msg);

        if (msg.startsWith("/")) mc.player.networkHandler.sendChatCommand(msg.substring(1));
        else mc.player.networkHandler.sendChatMessage(msg);
    }

    public static void sendLocalMsg(String msg) {
        ((IChatHud) mc.inGameHud.getChatHud()).pulse$add(Text.empty().append(PREFIX).append("%s".formatted(msg)));
    }

    public static void sendLocalMsg(Text msg) {
        ((IChatHud) mc.inGameHud.getChatHud()).pulse$add(Text.empty().append(PREFIX).append(msg));
    }

    public static void info(String msg) {
        String title = PulseClient.STACK_WALKER.getCallerClass().getSimpleName();
        if(Notifications.notify(title, msg, Notifications.Type.INFO)) return;
        sendLocalMsg(msg);
    }

    public static void sendLocalMsg(Text prefix, Text msg) {
        ((IChatHud) mc.inGameHud.getChatHud()).pulse$add(Text.empty().append(PREFIX_UNFINISHED).append(prefix).append(PREFIX_END).append(msg));
    }

    public static void warn(String msg) {
        String title = PulseClient.STACK_WALKER.getCallerClass().getSimpleName();
        if(Notifications.notify(title, msg, Notifications.Type.WARN)) return;
        sendLocalMsg(Text.empty().append(
                        Text.empty().formatted(Formatting.GOLD).formatted(Formatting.BOLD).append("WARN")
                ), Text.empty()
                .append(
                        Text.empty().formatted(Formatting.GRAY).append(msg)
                )
        );
    }

    public static void err(String msg) {
        String title = PulseClient.STACK_WALKER.getCallerClass().getSimpleName();
        if(Notifications.notify(title, msg, Notifications.Type.ERROR)) return;
        sendLocalMsg(Text.empty().append(
                        Text.empty().formatted(Formatting.DARK_RED).formatted(Formatting.BOLD).append("ERR")
                ), Text.empty()
                .append(
                        Text.empty().formatted(Formatting.GRAY).append(msg)
                )
        );
    }
}
