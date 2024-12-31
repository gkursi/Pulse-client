package xyz.qweru.pulse.client.utils.player;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.annotations.Status;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;
import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

@Status.Fixme
public class RotationUtil {

    /**
     * not implemented
     */
    public enum Mode {
        INSTANT,
        INSTANT_LERP,
        LERP,
        SMOOTHDAMP
    }
    public static float LERP_STEP = 0.05f;
    public static Mode mode = Mode.INSTANT;

    static List<Rotation> rotationQueue = new ArrayList<>();

    public static float keepPitch = 0f;
    public static float keepYaw = 0f;

    public static boolean shouldRotate() {
        return hold > 0;
    }
    public static void sendPacket() {
        PacketUtil.send(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), keepYaw, keepPitch, mc.player.isOnGround()));
    }

    public static PlayerMoveC2SPacket.Full onFull(PlayerMoveC2SPacket.Full packet) {
        if(hold <= 0) return packet;
        return new PlayerMoveC2SPacket.Full(packet.getX(0), packet.getY(0), packet.getZ(0), keepYaw, keepPitch, packet.isOnGround());
    }

    public static PlayerMoveC2SPacket.Full onPosOnGround(PlayerMoveC2SPacket.PositionAndOnGround packet) {
        if(hold <= 0) return new PlayerMoveC2SPacket.Full(packet.getX(0), packet.getY(0), packet.getZ(0), mc.player.getYaw(), mc.player.getPitch(), packet.isOnGround());
        return new PlayerMoveC2SPacket.Full(packet.getX(0), packet.getY(0), packet.getZ(0), keepYaw, keepPitch, packet.isOnGround());
    }
    public static PlayerMoveC2SPacket.LookAndOnGround onLookOnGround(PlayerMoveC2SPacket.LookAndOnGround packet) {
        if(hold <= 0) return packet;
        return new PlayerMoveC2SPacket.LookAndOnGround(keepYaw, keepPitch, packet.isOnGround());
    }

    public static PlayerMoveC2SPacket.LookAndOnGround onOnGroundOnly(PlayerMoveC2SPacket.OnGroundOnly packet) {
        if(hold <= 0) return new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), mc.player.getPitch(), packet.isOnGround());
        return new PlayerMoveC2SPacket.LookAndOnGround(keepYaw, keepPitch, packet.isOnGround());
    }

    static int rotationHoldTicks = 2;
    static int hold = 0;
    static boolean forceHold = false;
    public static void tick() {
        if((hold > 0 && rotationQueue.isEmpty()) || forceHold) {
            if(!rotationQueue.isEmpty() && rotationQueue.get(0).priority <= -999) { // change rotation if another override is queued
                Rotation next = rotationQueue.remove(0);
                keepPitch = next.pitch;
                keepYaw = next.yaw;
                next.rotate();
                hold = rotationHoldTicks;
            } else {
                hold--;
                if(hold <= 0) forceHold = false;
            }
            return;
        } else hold = 0;
        if(Util.nullCheck() || rotationQueue.isEmpty()) return;
        Rotation next = rotationQueue.remove(0);

        PulseClient.LOGGER.debug("Rotation: y{} p{}", keepYaw, keepPitch);
        next.rotate();
        hold = rotationHoldTicks;
    }

    public static float getYaw(Vec3d pos) {
        return MathHelper.wrapDegrees(mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(atan2(pos.getZ() - mc.player.getZ(), pos.getX() - mc.player.getX())) - 90f - mc.player.getYaw()));
    }

    public static float getPitch(Vec3d pos) {
        double diffX = pos.getX() - mc.player.getX();
        double diffY = pos.getY() - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = pos.getZ() - mc.player.getZ();

        double diffXZ = sqrt(diffX * diffX + diffZ * diffZ);

        return mc.player.getPitch() + MathHelper.wrapDegrees((float) -Math.toDegrees(atan2(diffY, diffXZ)) - mc.player.getPitch());
    }

    public static Pair<Float, Float> getRot(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(
                mc.player.getX(),
                mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()),
        mc.player.getZ()
            );

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.toDegrees(atan2(diffZ, diffX)) - 90f);
        float pitch = (float) -Math.toDegrees(atan2(diffY, diffXZ));
        return new Pair<>(yaw, pitch);
    }

    /**
     * Priority:
     * 0 - important (e.g. surround)
     * 10 - semi-important (e.g. auras)
     * 20+ - not important (anything else)
     */
    public static void addRotation(Vec3d pos, Runnable callback) {
        addRotation(pos, 20, callback);
    }

    public static void addRotation(Vec3d pos, int priority, Runnable callback) {
        Pair<Float, Float> rot = getRot(pos);
        float yaw = rot.getLeft(), pitch = rot.getRight();
        addRotation(yaw, pitch, priority, callback);
    }

    public static void addRotation(float yaw, float pitch, int priority, Runnable callback) {
        int i = 0;
        for (; i < rotationQueue.size(); i++) {
            if (priority < rotationQueue.get(i).priority) break;
        }

        LOGGER.debug("Added rotation: y{} p{} to index {}", yaw, pitch, i);
        rotationQueue.add(i, new Rotation(yaw, pitch, priority, callback));
    }

    public static void override(Vec3d pos, boolean forceKeep) {
        Pair<Float, Float> rot = getRot(pos);
        float yaw = rot.getLeft(), pitch = rot.getRight();
        override(yaw, pitch, forceKeep);
    }

    /**
     * top priority
     * @param yaw yaw
     * @param pitch pitch
     * @param forceKeep should forcefully keep the rotation for 10 ticks, ignoring other rotations
     */
    public static void override(float yaw, float pitch, boolean forceKeep) {
        override(yaw, pitch, forceKeep, 1);
    }

    public static void override(float yaw, float pitch, boolean forceKeep, int m) {
        rotationQueue.add(0, new Rotation(yaw, pitch, -999 * m, null));
        forceHold = forceKeep;
    }

    record Rotation(float yaw, float pitch, int priority, Runnable callback) {
        public void rotate() {
            PacketUtil.sendRotate(yaw, pitch);
            LOGGER.info("Rotated: y{} p{}", yaw, pitch);
            if(callback != null) {
                callback.run();
            }
        }
    }
}
