package xyz.qweru.pulse.client.systems.modules.impl.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.events.HandlePacketEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.player.ChatUtil;

import java.util.HashMap;

import static xyz.qweru.pulse.client.PulseClient.mc;

// partially skidded from blackout's autoez
public class PopCounter extends ClientModule {

    BooleanSetting self = new BooleanSetting("Self", "Send pop messages about self", true, true);
    BooleanSetting friends = new BooleanSetting("Friends", "Add additional text to message if a friend popped", true, true);

    public PopCounter() {
        builder()
                .name("PopCounter")
                .description("Automatically send an alert after pop")
                .settings(self, friends)
                .category(Category.MISC);
    }

    HashMap<String, Integer> popCache = new HashMap<>();
    @EventHandler
    private void onReceive(HandlePacketEvent event) {
        if (event.getPacket() instanceof EntityStatusS2CPacket packet) {
            // Pop
            if (packet.getStatus() == 35) {
                Entity entity = packet.getEntity(mc.world);
                if (mc.player != null && mc.world != null && entity instanceof PlayerEntity player) {
                    if (player != mc.player) {
                        boolean friend = PulseClient.friendSystem.isPlayerInSystem(player);
                        String username = player.getGameProfile().getName();
                        int pops = 1;
                        if(popCache.containsKey(username)) pops = popCache.get(username) + 1;
                        ChatUtil.sendLocalMsg("%s has popped %s times!".formatted(username, pops) + (friend && friends.isEnabled() ? " You should go help them!" : ""));
                        popCache.put(username, pops);
                    } else if (self.isEnabled()) {
                        String username = player.getGameProfile().getName();
                        int pops = 1;
                        if(popCache.containsKey(username)) pops = popCache.get(username) + 1;
                        ChatUtil.sendLocalMsg("You have popped %s times!".formatted(pops));
                        popCache.put(username, pops);
                    }
                }
            }
        }
    }

    @EventHandler
    void t(WorldTickEvent.Post e) {
        for (PlayerEntity pl : mc.world.getPlayers()) {
            String username = pl.getGameProfile().getName();
            if ((pl.isDead() || pl.getHealth() <= 0) && popCache.containsKey(username)) {
                if(pl != mc.player) ChatUtil.sendLocalMsg("%s has died after popping %s times.".formatted(username, popCache.get(username)));
                else ChatUtil.sendLocalMsg("You died after popping %s times.".formatted(popCache.get(username)));
                popCache.remove(username);
            }
        }
    }

}
