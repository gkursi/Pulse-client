package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import xyz.qweru.pulse.client.mixin.iinterface.IEntityVelocityUpdateS2CPacket;
import xyz.qweru.pulse.client.systems.events.HandlePacketEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.utils.annotations.Status;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

@Status.MarkedForUpgrade
public class Velocity extends ClientModule {
    public static ModeSetting mode = new ModeSetting("Bypass", "bypass some acs", true, "Vanilla", "Vulcan", "Vanilla");


    public Velocity() {
        builder(this)
                .name("Velocity")
                .description("Remove knockback")
                .settings(mode)
                .category(Category.MOVEMENT);
    }

    public static TimerUtil removeTimer = new TimerUtil();
    @EventHandler
    void packetEvent(HandlePacketEvent e) {
        if(mc.player == null) return;
        if(e.getPacket() instanceof EntityVelocityUpdateS2CPacket evup && evup.getEntityId() == mc.player.getId()) {
            if(mode.is("Vulcan") && removeTimer.hasReached(1000)) {
                removeTimer.reset();
                return;
            }
            ((IEntityVelocityUpdateS2CPacket) evup).pulse$setX(0);
            ((IEntityVelocityUpdateS2CPacket) evup).pulse$setY(0);
            ((IEntityVelocityUpdateS2CPacket) evup).pulse$setZ(0);
        }
    }
}
