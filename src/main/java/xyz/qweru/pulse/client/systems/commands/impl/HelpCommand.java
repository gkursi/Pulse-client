package xyz.qweru.pulse.client.systems.commands.impl;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.commands.ClientCommand;
import xyz.qweru.pulse.client.managers.impl.CommandManager;
import xyz.qweru.pulse.client.utils.player.ChatUtil;

public class HelpCommand extends ClientCommand {
    public HelpCommand() {
        super("help", "Shows the help message");
    }

    @Override
    public void run(String[] args) {
        ChatUtil.sendLocalMsg(
                Text.empty()
                        .setStyle(Style.EMPTY.withFormatting(Formatting.BOLD).withFormatting(Formatting.LIGHT_PURPLE))
                        .append("PulseClient %s by %s".formatted(PulseClient.VERSION, PulseClient.AUTHOR))
        );
        ChatUtil.sendLocalMsg(
                Text.empty()
                        .setStyle(Style.EMPTY.withFormatting(Formatting.BOLD).withFormatting(Formatting.GRAY))
                        .append("All commands:")
        );
        for(ClientCommand command : CommandManager.INSTANCE.getItemList()) ChatUtil.sendLocalMsg(
                Text.empty()
                        .append(
                                Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.BOLD).withFormatting(Formatting.GOLD))
                                        .append(command.getName())
                        )
                        .append(
                                Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.GRAY))
                                        .append(" - ")
                        )
                        .append(
                                Text.empty().setStyle(Style.EMPTY.withFormatting(Formatting.AQUA))
                                        .append(command.getDescription())
                        )
        );
    }
}
