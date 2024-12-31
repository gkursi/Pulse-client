package xyz.qweru.pulse.client.utils.world;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.mixin.iinterface.IClientConnection;
import xyz.qweru.pulse.client.mixin.iinterface.IWorld;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class PacketUtil {
    public static void send(Packet<?> packet) {
        mc.getNetworkHandler().sendPacket(packet);
    }

    public static void sendMove(Vec3d pos) {
        sendMove(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround());
    }

    public static void sendMove(Vec3d pos, boolean onGround) {
        sendMove(pos.getX(), pos.getY(), pos.getZ(), onGround);
    }
    public static void sendMove(double x, double y, double z, boolean onGround) {
        send(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround));
    }

    public static void sendImmediateMove(Vec3d pos) {
        sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, mc.player.isOnGround()));
    }

    public static void sendImmediateRotate(float yaw, float pitch, boolean onGround) {
        sendImmediately(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround));
    }

    public static void sendRotate(float yaw, float pitch) {
        send(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.isOnGround()));
    }

    public static void sendImmediately(Packet<?> packet) {
        ((IClientConnection) mc.getNetworkHandler().getConnection()).pulse$sendImmediately(packet);
    }

    public static void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator) {
        PendingUpdateManager pendingUpdateManager = ((IWorld) world).pulse$getPendingUpdateManager().incrementSequence();

        try {
            int i = pendingUpdateManager.getSequence();
            Packet<ServerPlayPacketListener> packet = packetCreator.predict(i);
            sendImmediately(packet);
        } catch (Throwable var7) {
            if (pendingUpdateManager != null) {
                try {
                    pendingUpdateManager.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }
            }

            throw var7;
        }

        if (pendingUpdateManager != null) {
            pendingUpdateManager.close();
        }

    }

}
