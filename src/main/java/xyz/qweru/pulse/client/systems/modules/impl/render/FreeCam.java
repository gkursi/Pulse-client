package xyz.qweru.pulse.client.systems.modules.impl.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.mixin.iinterface.ICamera;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.events.PreSendPacketEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class FreeCam extends ClientModule {
    NumberSetting speed = numberSetting()
            .name("Speed")
            .description("How fast should the camera move")
            .range(0, 10)
            .defaultValue(1)
            .build();

    public FreeCam() {
        builder(this)
                .name("FreeCam")
                .description("Leave your body and enter spectator mode")
                .category(Category.RENDER);
        disableOnExit();
    }

    ICamera c = null;

    boolean captured = false;
    Vec3d initPos = new Vec3d(0, 0, 0);
    Vec3d initPlayerPos = new Vec3d(0,0,0);

    @Override
    public void enable() {
        super.enable();
        captured = false;
        unpress();
    }

    @EventHandler
    void render3D(Render3DEvent e) {
        if(!captured) {
            c = (ICamera) e.getCamera();
            initPos = e.getCamera().getPos();
            initPlayerPos = mc.player.getPos();
            captured = true;
        }
    }

    @EventHandler
    void tick(WorldTickEvent.Pre e) {
        if(Util.nullCheck(mc) || c == null) return;
        c.pulse$setRotation(mc.player.getYaw(), mc.player.getPitch());
        if(mc.options.backKey.isPressed()) {
            double angle = Math.atan2(c.pulse$getRotationVector().z, c.pulse$getRotationVector().x) + Math.PI;
            c.pulse$moveBy(Math.cos(angle) * speed.getValue(), 0, Math.sin(angle) * speed.getValue());
        }
        if(mc.options.forwardKey.isPressed()) {
            double angle = Math.atan2(c.pulse$getRotationVector().z, c.pulse$getRotationVector().x);
            c.pulse$moveBy(Math.cos(angle) * speed.getValue(), 0, Math.sin(angle) * speed.getValue());
        }
        if(mc.options.leftKey.isPressed()) {
            double angle = Math.atan2(c.pulse$getRotationVector().z, c.pulse$getRotationVector().x) - Math.PI / 2;
            c.pulse$moveBy(Math.cos(angle) * speed.getValue(), 0, Math.sin(angle) * speed.getValue());
        }
        if(mc.options.rightKey.isPressed()) {
            double angle = Math.atan2(c.pulse$getRotationVector().z, c.pulse$getRotationVector().x) + Math.PI / 2;
            c.pulse$moveBy(Math.cos(angle) * speed.getValue(), 0, Math.sin(angle) * speed.getValue());
        }
        if(mc.options.jumpKey.isPressed()) c.pulse$moveBy(0,  speed.getValue(), 0);
        if(mc.options.sneakKey.isPressed()) c.pulse$moveBy(0, -speed.getValue(), 0);
//        unpress();
    }

    @Override
    public void disable() {
        if(Util.nullCheck(mc)) return;
        captured = false;
        mc.player.setPos(initPlayerPos.x, initPlayerPos.y, initPlayerPos.z);
        unpress();
    }

    void unpress() {
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sneakKey.setPressed(false);
    }

    @EventHandler
    void packetEvent(PreSendPacketEvent e) {
        if (this.isEnabled() && !(e.getPacket() instanceof KeepAliveC2SPacket || e.getPacket() instanceof CommonPongC2SPacket)) {
            e.cancel();
        }
    }
}

