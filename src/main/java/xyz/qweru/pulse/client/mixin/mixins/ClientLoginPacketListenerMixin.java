package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.modules.impl.movement.LiveOverflow;
import xyz.qweru.pulse.client.utils.world.PacketUtil;
import xyz.qweru.pulse.client.utils.QueueUtil;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

@Mixin(LoginSuccessS2CPacket.class)
public class ClientLoginPacketListenerMixin {

    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientLoginPacketListener;)V", at = @At("TAIL"))
    public void onSuccess(ClientLoginPacketListener clientLoginPacketListener, CallbackInfo ci) {
        if(LiveOverflow.robotMove.isEnabled()) QueueUtil.onWorldLoad(() -> {
            while (Util.nullCheck(mc));
            double x = ((int)(mc.player.getX()) * 100) / 100.0;
            double z = ((int)(mc.player.getZ()) * 100) / 100.0;
            Vec3d pos = new Vec3d(x, mc.player.getY(), z);
            PulseClient.LOGGER.info("X: {}, Z: {}, vec3d.toString: {}", x, z, pos);
            PacketUtil.sendImmediateMove(pos);
        });
    }
}
