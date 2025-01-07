package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import xyz.qweru.pulse.client.mixin.iinterface.IEntityVelocityUpdateS2CPacket;
import xyz.qweru.pulse.client.systems.events.HandlePacketEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.annotations.Status;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

@Status.MarkedForUpgrade
public class Velocity extends ClientModule {
    public static ModeSetting mode = new ModeSetting("Bypass", "bypass some acs", true, "Vanilla", "Vulcan", "Vanilla");

    public static NumberSetting noKBTimer = numberSetting()
            .info("No KB timer", "(vulcan) how long to not take kb for")
            .range(0, 100000)
            .defaultValue(10000)
            .stepFullNumbers()
            .build();

    public static NumberSetting KBTimer = numberSetting()
            .info("Recharge timer", "(vulcan) how long to take kb for")
            .range(0, 100000)
            .defaultValue(15000)
            .stepFullNumbers()
            .build();

    public Velocity() {
        builder(this)
                .name("Velocity")
                .description("Remove knockback")
                .settings(mode, noKBTimer, KBTimer)
                .category(Category.MOVEMENT);
    }

    public static TimerUtil vulcanTimer = new TimerUtil();
    public static boolean removeKB = false;
    @EventHandler
    void packetEvent(HandlePacketEvent e) {
        if(mc.player == null) return;
        if(e.getPacket() instanceof EntityVelocityUpdateS2CPacket evup && evup.getEntityId() == mc.player.getId()) {
            if(mode.is("Vulcan")) {
                if(!removeKB && vulcanTimer.hasReached(KBTimer.getValue())) {
                    removeKB = true;
                    vulcanTimer.reset();
                } else if(removeKB && vulcanTimer.hasReached(noKBTimer.getValue())) {
                    removeKB = false;
                    vulcanTimer.reset();
                    return;
                } else if(!removeKB) return;
            }
            ((IEntityVelocityUpdateS2CPacket) evup).pulse$setX(0);
            ((IEntityVelocityUpdateS2CPacket) evup).pulse$setY(0);
            ((IEntityVelocityUpdateS2CPacket) evup).pulse$setZ(0);
        }
    }
}
