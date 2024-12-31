package xyz.qweru.pulse.client.mixin.iinterface;

import net.minecraft.network.packet.Packet;

public interface IClientConnection {
    void pulse$sendImmediately(Packet<?> packet, boolean flush);
    void pulse$sendImmediately(Packet<?> packet);
}