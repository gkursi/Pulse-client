package xyz.qweru.pulse.client.systems.commands.impl;

import net.minecraft.text.Text;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.commands.ClientCommand;
import xyz.qweru.pulse.client.systems.modules.impl.misc.Chat;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.FakePlayer;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Random;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class FakePlayerCommand extends ClientCommand {
    public FakePlayerCommand() {
        super("fakeplayer", "Spawn a fake player entity");
    }

    ArrayList<FakePlayer> list = new ArrayList<>();

    @Override
    public void run(String[] args) {
        try {
            if(args[1].equalsIgnoreCase("add")) {
                FakePlayer fakePlayer = new FakePlayer(args.length > 2 ? args[2] : getSaltString(), mc.player.getHealth(), true);
                fakePlayer.spawn();
                list.add(fakePlayer);
                ChatUtil.sendLocalMsg("Added!");
            } else if(args[1].equalsIgnoreCase("remove")) {
                Iterator<FakePlayer> iterator = list.iterator();
                while (iterator.hasNext()) {
                    FakePlayer fakePlayer = iterator.next();
                    if(fakePlayer.getGameProfile().getName().equals(args[2])) {
                        fakePlayer.despawn();
                        iterator.remove();
                        ChatUtil.sendLocalMsg("Removed " + fakePlayer.getGameProfile().getName() + "!");
                    }
                }
            } else if(args[1].equalsIgnoreCase("clear")) {
                Iterator<FakePlayer> iterator = list.iterator();
                while (iterator.hasNext()) {
                    FakePlayer player = iterator.next();
                    player.despawn();
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            PulseClient.throwException(e);
            ChatUtil.err("Usage: fakeplayer <add / remove> [name]");
        }
    }

    //https://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return "pulse_" + saltStr;

    }
}
