package xyz.qweru.pulse.client.utils.world;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import static java.lang.Math.sqrt;

public class PosUtil {

    public static double distanceBetween(Vec3d from, Vec3d to) {
        double d = from.x - to.x;
        double e = from.y - to.y;
        double f = from.z - to.z;
        return sqrt(d * d + e * e + f * f);
    }

    public static Vec3d predictPos(LivingEntity entity, int ticks) {
        Vec3d ePos = entity.getPos();
        for(int i = 0; i < ticks; i++) {
            ePos.add(entity.getVelocity());
        }
        return ePos;
    }

    public static boolean isPositionInside(Vec3d pos, LivingEntity entity) {
        return entity.getBoundingBox().contains(pos);
    }

    public static boolean boxCollision(Box b1, Box b2) {
        return b1.intersects(b2);
    }

}
