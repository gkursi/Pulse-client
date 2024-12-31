package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.systems.events.PlayerMoveEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.world.BlockUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Speed extends ClientModule {

    BooleanSetting stopInWall = new BooleanSetting("Stop in wall", "Stop using speed while inside of a wall", false, true);
    NumberSetting speed = numberSetting()
            .name("Speed")
            .description("How fast")
            .setValueModifier(value -> (float) Util.round(value, 2))
            .range(0, 5)
            .defaultValue(1)
            .build();

    NumberSetting fall = numberSetting()
            .name("Fall speed")
            .description("How fast to fall")
            .setValueModifier(value -> (float) Util.round(value, 2))
            .range(0, 5)
            .defaultValue(1)
            .build();

    BooleanSetting ccStrafe = new BooleanSetting("CCStrafe", "ccstrafe", false, true);

    public Speed() {
        builder(this)
                .name("Speed")
                .description("Makes you faster")
                .settings(stopInWall, speed, fall, ccStrafe)
                .category(Category.MOVEMENT);
    }

    @Override
    public void enable() {
        super.enable();
        jumpPhase = 1;
    }

    @EventHandler
    void move(PlayerMoveEvent e) {
        if(Managers.MODULE.getItemByClass(Flight.class).isEnabled()) return;
        if(stopInWall.isEnabled()
                && !Util.equalsAny(BlockUtil.getBlockAt(BlockPos.ofFloored(mc.player.getPos())),
                    Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.FIRE, Blocks.SOUL_FIRE)
                && mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos()))
                    .isSolidBlock(mc.world, BlockPos.ofFloored(mc.player.getPos())))
            return;

        if(ccStrafe.isEnabled()) {
            ccStrafe(e);
            return;
        }

        Vec3d vel = getHorizontalVelocity(speed.getValue() * 6.5);

        double velX = vel.getX();
        double velZ = vel.getZ();

        e.set(velX, getVerticalVelocity(e.getMovement().y), velZ);
    }

    private Vec3d horizontalVelocity = new Vec3d(0, 0, 0);

    public double velocity;
    int jumpPhase = 1;
    void ccStrafe(PlayerMoveEvent e) {
        if(ccStrafe.isEnabled()) {
            double forward = mc.player.input.movementForward;
            double sideways = mc.player.input.movementSideways;


            double yaw = getYaw(forward, sideways);

            if (jumpPhase == 4) {
                velocity *= 0.9888888889;

                if (mc.player.isOnGround()) {
                    jumpPhase = 1;
                }
            }
            if (jumpPhase == 3) {
                velocity = velocity + (0.2873 - velocity) * 0.6;
                jumpPhase = 4;
            }
            if (jumpPhase == 2) {
                e.set(e.getMovement().x, 0.4, e.getMovement().z);
                velocity *= 1.85;
                jumpPhase = 3;
            }
            if (jumpPhase == 1) {
                if (mc.player.isOnGround()) {
                    velocity = 0.2873;
                    jumpPhase = 2;
                }
            }

            velocity = Math.max(velocity, 0.2873);

            double motion = velocity;
            if (velocity < 0.01) {
                motion = 0;
            }
            if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
                motion *= 1.2 + mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() * 0.2;
            }
            if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                motion /= 1.2 + mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() * 0.2;
            }

            double x = Math.cos(Math.toRadians(yaw + 90.0f));
            double y = mc.player.getVelocity().getY();
            double z = Math.sin(Math.toRadians(yaw + 90.0f));

            if(move) {
                e.set(motion * x, y, motion * z);
            } else {
                e.set(0, y, 0);
            }
        }
    }

    boolean move = false;
    private double getYaw(double f, double s) {
        double yaw = mc.player.getYaw();
        if (f > 0) {
            move = true;
            yaw += s > 0 ? -45 : s < 0 ? 45 : 0;
        } else if (f < 0) {
            move = true;
            yaw += s > 0 ? -135 : s < 0 ? 135 : 180;
        } else {
            move = s != 0;
            yaw += s > 0 ? -90 : s < 0 ? 90 : 0;
        }
        return yaw;
    }

    public Vec3d getHorizontalVelocity(double bps) {
        float yaw = mc.player.getYaw();

        double diagonal = 1 / Math.sqrt(2);

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);
        double velX = 0;
        double velZ = 0;

        boolean a = false;
        if (mc.player.input.pressingForward) {
            velX += forward.x / 20 * bps;
            velZ += forward.z / 20 * bps;
            a = true;
        }
        if (mc.player.input.pressingBack) {
            velX -= forward.x / 20 * bps;
            velZ -= forward.z / 20 * bps;
            a = true;
        }

        boolean b = false;
        if (mc.player.input.pressingRight) {
            velX += right.x / 20 * bps;
            velZ += right.z / 20 * bps;
            b = true;
        }
        if (mc.player.input.pressingLeft) {
            velX -= right.x / 20 * bps;
            velZ -= right.z / 20 * bps;
            b = true;
        }

        if (a && b) {
            velX *= diagonal;
            velZ *= diagonal;
        }

        horizontalVelocity = new Vec3d(velX, horizontalVelocity.y, velZ);

        return horizontalVelocity;
    }

    double getVerticalVelocity(double initialVelocity) {
        return initialVelocity < 0 ? initialVelocity * fall.getValue() : initialVelocity;
    }

}
