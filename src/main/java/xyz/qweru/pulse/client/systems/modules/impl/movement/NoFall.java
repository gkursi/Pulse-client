package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import xyz.qweru.pulse.client.systems.events.SendPacketEvent;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerMoveC2SPacket;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;

public class NoFall extends ClientModule {
    public NoFall() {
        builder(this)
                .name("NoFall")
                .description("No fall damage")
                .category(Category.MOVEMENT);
    }

    @EventHandler
    public void sendPacketEvent(SendPacketEvent event) {
        Packet<?> packet = event.getPacket();
        if(packet instanceof PlayerMoveC2SPacket moveC2SPacket) {
            ((IPlayerMoveC2SPacket) moveC2SPacket).pulse$setOnGround(true);
        }
    }
}
