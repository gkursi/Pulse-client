package xyz.qweru.pulse.client.systems.modules.impl.world;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import xyz.qweru.pulse.client.systems.events.PreSendPacketEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;

@ExcludeModule
public class NoChunkLoad extends ClientModule {
    public NoChunkLoad() {
        builder(this)
                .name("NoChunkLoad")
                .description("Prevents the client from acknowledging received chunks")
                .category(Category.WORLD);
    }

    @EventHandler
    void sendPacket(PreSendPacketEvent e) {
        if(e.getPacket() instanceof AcknowledgeChunksC2SPacket) e.setCancelled(true);
    }
}
