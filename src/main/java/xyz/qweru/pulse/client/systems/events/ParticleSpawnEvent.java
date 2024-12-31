package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.particle.ParticleEffect;

public class ParticleSpawnEvent implements ICancellable {
    private final ParticleEffect parameters;
    private final boolean alwaysSpawn;
    private final boolean canSpawnOnMinimal;
    private final double x;
    private final double y;
    private final double z;
    private final double velocityX;
    private final double velocityY;
    private final double velocityZ;
    boolean cancelled = false;

    public ParticleEffect getParameters() {
        return parameters;
    }

    public boolean isAlwaysSpawn() {
        return alwaysSpawn;
    }

    public boolean isCanSpawnOnMinimal() {
        return canSpawnOnMinimal;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getVelocityZ() {
        return velocityZ;
    }

    public ParticleSpawnEvent(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {

        this.parameters = parameters;
        this.alwaysSpawn = alwaysSpawn;
        this.canSpawnOnMinimal = canSpawnOnMinimal;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
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
