package xyz.qweru.pulse.client.systems.commands.impl;

import xyz.qweru.pulse.client.systems.commands.ClientCommand;

public class StopMoveCommand extends ClientCommand {

    public static boolean terminate = false;

    public StopMoveCommand() {
        super("stop-move", "Force stops commands like travel and follow");
    }

    @Override
    public void run(String[] args) {
        terminate = true;
    }
}
