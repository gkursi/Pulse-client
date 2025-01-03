package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.IClientConnection;
import xyz.qweru.pulse.client.systems.events.HandlePacketEvent;
import xyz.qweru.pulse.client.systems.events.PostSendPacketEvent;
import xyz.qweru.pulse.client.systems.events.PreSendPacketEvent;
import xyz.qweru.pulse.client.systems.modules.impl.movement.LiveOverflow;

import static xyz.qweru.pulse.client.PulseClient.Events;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements IClientConnection {


    @Shadow protected abstract void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush);

    @Override
    public void pulse$sendImmediately(Packet<?> packet, boolean flush) {
        sendImmediately(packet, null,  true);
    }

    @Override
    public void pulse$sendImmediately(Packet<?> packet) {
        pulse$sendImmediately(packet, true);
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if(PulseClient.Events.post(new HandlePacketEvent(packet)).isCancelled()) ci.cancel();

        if(ModuleManager.INSTANCE.getItemByClass(LiveOverflow.class).isEnabled() && LiveOverflow.noWorldBorder.isEnabled() &&
                (packet instanceof WorldBorderCenterChangedS2CPacket ||
                packet instanceof WorldBorderInitializeS2CPacket ||
                packet instanceof WorldBorderSizeChangedS2CPacket ||
                packet instanceof WorldBorderInterpolateSizeS2CPacket ||
                packet instanceof WorldBorderWarningBlocksChangedS2CPacket ||
                packet instanceof WorldBorderWarningTimeChangedS2CPacket)
        ) ci.cancel();
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    void sp(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if(Events.post(new PreSendPacketEvent(packet)).isCancelled()) ci.cancel();
    }

    @Inject(method = "sendImmediately", at = @At("TAIL"), cancellable = true)
    void spPost(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if(Events.post(new PostSendPacketEvent(packet)).isCancelled()) ci.cancel();
    }
}
