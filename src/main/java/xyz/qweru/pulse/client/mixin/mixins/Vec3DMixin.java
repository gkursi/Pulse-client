package xyz.qweru.pulse.client.mixin.mixins;

import net.fabricmc.loader.impl.lib.sat4j.specs.IVec;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xyz.qweru.pulse.client.mixin.iinterface.IVec3D;

@Mixin(Vec3d.class)
public class Vec3DMixin implements IVec3D {
    @Mutable
    @Shadow @Final public double x;

    @Mutable
    @Shadow @Final public double y;

    @Mutable
    @Shadow @Final public double z;

    @Override
    public void pulse$setX(double x) {
        this.x = x;
    }

    @Override
    public void pulse$setZ(double z) {
        this.z = z;
    }

    @Override
    public void pulse$setY(double y) {
        this.y = y;
    }
}
