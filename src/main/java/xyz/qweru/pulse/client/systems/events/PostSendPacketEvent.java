package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.network.packet.Packet;

public class PostSendPacketEvent implements ICancellable {
    private boolean cancelled = false;
    private Packet<?> packet;

    public PostSendPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
