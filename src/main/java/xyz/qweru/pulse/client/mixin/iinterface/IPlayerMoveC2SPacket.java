package xyz.qweru.pulse.client.mixin.iinterface;

public interface IPlayerMoveC2SPacket {
    void pulse$setX(double x);
    void pulse$setY(double y);
    void pulse$setZ(double z);
    void pulse$setChangesPosition(boolean changesPosition);
    void pulse$setChangesRotation(boolean changesPosition);
    void pulse$setOnGround(boolean onGround);
    void pulse$setPitch(float pitch);
    void pulse$setYaw(float yaw);
}
