package xyz.qweru.pulse.client.systems.commands.impl;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.commands.ClientCommand;
import xyz.qweru.pulse.client.utils.player.ChatUtil;

public class AddFriendCommand extends ClientCommand {
    public AddFriendCommand() {
        super("addFriend", "add a player to your friends list");
    }

    @Override
    public void run(String[] args) {
        if(args.length != 2) {
            ChatUtil.err("Invalid arguments! Usage: addFriend <username>");
            return;
        }
        PulseClient.friendSystem.addPlayer(args[1]);
        ChatUtil.sendLocalMsg("Added!");
    }
}
