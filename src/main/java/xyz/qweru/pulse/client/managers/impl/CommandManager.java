package xyz.qweru.pulse.client.managers.impl;

import org.jetbrains.annotations.Nullable;
import xyz.qweru.pulse.client.systems.commands.ClientCommand;
import xyz.qweru.pulse.client.systems.commands.impl.*;
import xyz.qweru.pulse.client.managers.Manager;

import java.util.Objects;

public class CommandManager extends Manager<ClientCommand> {

    public static CommandManager INSTANCE = new CommandManager();

    public CommandManager() {
        super("Command manager");
        init();
    }

    /**
     * get command by name
     * @param name command name without prefix
     * @return client command obj or null
     */
    public @Nullable ClientCommand getCommandByName(String name) {
        for (ClientCommand clientCommand : itemList) {
            if(Objects.equals(clientCommand.getName(), name)) return clientCommand;
        }
        return null;
    }

    @Override
    public void init() {
        super.init();
        addItem(new HelpCommand());
        addItem(new TravelCommand());
        addItem(new StopMoveCommand());
        addItem(new FollowCommand());
        addItem(new AddFriendCommand());
        addItem(new RemoveFriendCommand());
        addItem(new ConfigCommand());
        addItem(new FakePlayerCommand());
        addItem(new VclipCommand());
    }
}
