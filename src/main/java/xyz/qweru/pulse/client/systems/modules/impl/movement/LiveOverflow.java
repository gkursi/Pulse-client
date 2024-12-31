package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.world.GameMode;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class LiveOverflow extends ClientModule {

    public static BooleanSetting robotMove = new BooleanSetting("Robot move", "Rounds all coords down to the 3rd floating point", true, true);
    public static BooleanSetting antiFakeCreative = new BooleanSetting("Anti fake creative", "Spoofs clients gamemode to be survival", true, true);
    public static BooleanSetting noWorldBorder = new BooleanSetting("No world border", "Removes the world border", true, true);

    public static PlayerMoveC2SPacket lastPacket = null;

    public LiveOverflow() {
        super("LiveOverflow", "Utils for LO's server", -1, Category.MOVEMENT);
        builder(this).settings(robotMove, antiFakeCreative, noWorldBorder);
    }

    @EventHandler
    public void onWorldTick(WorldTickEvent.Post ignored) {
        if(antiFakeCreative.isEnabled()) mc.interactionManager.setGameMode(GameMode.SURVIVAL);
    }
}
