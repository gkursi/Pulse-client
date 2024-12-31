package xyz.qweru.pulse.client.mixin.iinterface;

import net.minecraft.util.math.Vec3d;

public interface ICamera {
    void pulse$moveBy(double x, double y, double z);
    float pulse$getX();
    float pulse$getY();
    float pulse$getZ();
    float pulse$getPitch();
    float pulse$getYaw();
    Vec3d pulse$getRotationVector();
    void pulse$setRotation(float yaw, float pitch);
    void pulse$setYaw(float yaw);
    void pulse$setPitch(float pitch);
    void pulse$setPos(Vec3d pos);
}
