package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.mixin.iinterface.IVec3D;

public class PlayerMoveEvent implements ICancellable {
    private final MovementType movementType;

    public void set(double x, double y, double z) {
        ((IVec3D) movement).pulse$setX(x);
        ((IVec3D) movement).pulse$setY(y);
        ((IVec3D) movement).pulse$setZ(z);
    }

    private final Vec3d movement;
    boolean cancelled = false;

    public MovementType getMovementType() {
        return movementType;
    }

    public Vec3d getMovement() {
        return movement;
    }

    public PlayerMoveEvent(MovementType movementType, Vec3d movement) {
        this.movementType = movementType;
        this.movement = movement;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
