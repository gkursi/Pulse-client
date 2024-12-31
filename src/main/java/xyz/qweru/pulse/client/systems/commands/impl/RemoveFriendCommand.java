package xyz.qweru.pulse.client.systems.commands.impl;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.commands.ClientCommand;
import xyz.qweru.pulse.client.utils.player.ChatUtil;

public class RemoveFriendCommand extends ClientCommand {
    public RemoveFriendCommand() {
        super("removeFriend", "remove a player from your friends list");
    }

    @Override
    public void run(String[] args) {
        if(args.length != 2) {
            ChatUtil.err("Invalid arguments! Usage: removeFriend <username>");
            return;
        }
        if(PulseClient.friendSystem.removePlayer(args[1])) ChatUtil.warn("Could not find " + args[1] + " in your friends list.");
        else ChatUtil.sendLocalMsg("Removed!");
    }
}
