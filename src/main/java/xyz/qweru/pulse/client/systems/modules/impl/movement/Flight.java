package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.systems.events.KeyEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.NumberSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;
import xyz.qweru.pulse.client.utils.Util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Flight extends ClientModule {

    private final NumberSetting speed = new NumberSetting("Speed", "The speed to fly at", 0, 10, 0.1f, true);
    private final NumberSetting verticalSpeed = new NumberSetting("Velocity vertical speed", "The speed to fly at", 0, 10, 0.1f, true);
    private float prevSpeed = 0.1f;
    private final ModeSetting mode = new ModeSetting("Bypass", "what to use for flying", true, "Vanilla", "Vulcan", "Velocity", "Vanilla");
    private final NumberSetting antiKickDelay = new NumberSettingBuilder()
            .name("Anti Kick delay")
            .description("How many ticks should be between each anti-kick move")
            .defaultValue(40f)
            .shouldShow(true)
            .max(120f)
            .min(0f)
            .build();

    private final NumberSetting spoofRemoveTicks = numberSetting()
            .info("Spoof remove ticks", "(vulcan) how many ticks to wait before removing spoofed blocks")
            .range(0, 100)
            .defaultValue(50)
            .build();

    private int tickCounter = 0;
    private final BooleanSetting velMatch = new BooleanSetting("Match vertical speed", "(velocity) match vertical speed", true, true);
    private final BooleanSetting extend = new BooleanSetting("Extend", "Extend block spoof", false, true);

    public Flight() {
        super("Flight", "Lets you fly", -1, Category.MOVEMENT);
        builder(this).settings(speed, mode, velMatch, antiKickDelay, extend);

        mode.addOnToggle(() -> {
            if(mode.is("Velocity")) {
                speed.setMax(100);
            } else speed.setMax(10);
        });
    }

    @Override
    public void enable() {
        super.enable();
        if(Util.nullCheck(mc)) return;
        prevSpeed = mc.player.getAbilities().getFlySpeed();
        tickCounter = 0;
        lastPos = null;
        lastState = null;
    }

    BlockPos lastPos = null;
    List<Pair<BlockPos, BlockState>> extendedList = new CopyOnWriteArrayList<>();
    BlockState lastState = null;
    boolean prevAntiKick = false;
    @EventHandler
    private void onTick(WorldTickEvent.Post ignored) {
        if(Util.nullCheck()) return;
//        if(groundOnly.isEnabled() && !mc.player.isOnGround()) return;
        if(mode.is("Vanilla")) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed(speed.getValue());
            if(prevAntiKick) {
                PacketUtil.sendMove(mc.player.getPos());
                prevAntiKick = false;
            }
            if(tickCounter >= Math.floor(antiKickDelay.getValue())) {
                PacketUtil.sendMove(mc.player.getPos().subtract(0, 0.05, 0));
                tickCounter = 0;
                prevAntiKick = true;
            }
        } else if(mode.is("Vulcan")){
            if(lastPos != null && lastState != null) {
                mc.player.getWorld().setBlockState(lastPos, lastState);
                extendedList.forEach(blockPosBlockStatePair -> {
                    mc.player.getWorld().setBlockState(blockPosBlockStatePair.getLeft(), blockPosBlockStatePair.getRight());
                });
            }
            lastState = mc.player.getWorld().getBlockState(mc.player.getBlockPos().add(0, -1, 0));
            mc.player.getWorld().setBlockState(mc.player.getBlockPos().add(0, -1, 0), Blocks.BEDROCK.getDefaultState());
            lastPos = mc.player.getBlockPos().add(0, -1, 0);
            if(extend.isEnabled()) {
                extendedList.clear();
                extendedList.add(extendTo(1, -1, 0));
                extendedList.add(extendTo(-1, -1, 0));
                extendedList.add(extendTo(0, -1, 1));
                extendedList.add(extendTo(0, -1, -1));

                extendedList.forEach(blockPosBlockStatePair ->
                        mc.player.getWorld().setBlockState(blockPosBlockStatePair.getLeft(), Blocks.BEDROCK.getDefaultState()));
            }
        } else if (mode.is("Velocity")) {
            Vec3d velocity = getVelocity();
            mc.player.setVelocity(velocity);
        }
        tickCounter++;
    }

    Pair<BlockPos, BlockState> extendTo(int x, int y, int z) {
        BlockPos pos = mc.player.getBlockPos().add(x, y, z);
        BlockState state = mc.player.getWorld().getBlockState(pos);
        return new Pair<>(pos, state);
    }

    private @NotNull Vec3d getVelocity() {
        boolean up = mc.options.jumpKey.isPressed();
        boolean down = mc.options.sneakKey.isPressed();

        Vec3d velocity = getHorizontalVelocity(speed.getValue() * 2);

        double speed = this.speed.getValue() * (velMatch.isEnabled() ? 10 : 1);

        if(up) velocity = new Vec3d(velocity.x, speed, velocity.z);
        else if(down) velocity = new Vec3d(velocity.x, -speed, velocity.z);
        else velocity = new Vec3d(velocity.x, 0, velocity.z);
        return velocity;
    }

    public Vec3d getHorizontalVelocity(double bps) {
        float yaw = mc.player.getYaw();
        Vec3d horizontalVelocity;
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

        horizontalVelocity = new Vec3d(velX, 0, velZ);

        return horizontalVelocity;
    }

    @Override
    public void disable() {
        super.disable();
        if(Util.nullCheck(mc)) return;
        if(mode.is("Vanilla")) {
            mc.player.getAbilities().allowFlying = mc.interactionManager.getCurrentGameMode() == GameMode.CREATIVE;
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().setFlySpeed(prevSpeed);
        }
        if(lastPos != null && lastState != null) {
            mc.player.getWorld().setBlockState(lastPos, lastState);
        }
        extendedList.forEach(blockPosBlockStatePair -> {
            mc.player.getWorld().setBlockState(blockPosBlockStatePair.getLeft(), blockPosBlockStatePair.getRight());
        });
    }

    @EventHandler
    void onKey(KeyEvent e) {
        if(Util.nullCheck(mc)) return;
        if(e.getAction() == GLFW.GLFW_RELEASE) {
            if(e.getKey() == InputUtil.KEY_W || e.getKey() == InputUtil.KEY_S || e.getKey() == InputUtil.KEY_A || e.getKey() == InputUtil.KEY_D ) mc.player.setVelocity(0, 0 ,0);
        }
    }

}
