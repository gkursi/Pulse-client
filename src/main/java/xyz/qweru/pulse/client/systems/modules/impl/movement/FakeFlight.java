package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerMoveC2SPacket;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.events.PreSendPacketEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.annotations.Status;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

@ExcludeModule
@Status.MarkedForIntegration(Flight.class)
public class FakeFlight extends Flight {

    public FakeFlight() {
        super();
        builder(this)
                .name("Fake flight")
                .description("Client side flight that snaps you to the ground for others")
                .category(Category.MOVEMENT);
    }

    BlockPos lastPos = null;

    @EventHandler
    void packetSentEvent(PreSendPacketEvent e) {
        if(e.getPacket() instanceof PlayerMoveC2SPacket moveC2SPacket ) {
            Vec3d pos = new Vec3d(moveC2SPacket.getX(mc.player.getX()), moveC2SPacket.getY(mc.player.getY()), moveC2SPacket.getZ(mc.player.getZ()));
            Vec3d newPos = pos;

            for (int i = 0; i < 10; i++) {
                BlockPos bp = BlockPos.ofFloored(pos.subtract(0, i, 0));
                if(!BlockUtil.isPosReplaceable(bp)) {
                    newPos = bp.toBottomCenterPos().add(0, 1, 0);
                    break;
                }
            }

            ((IPlayerMoveC2SPacket) moveC2SPacket).pulse$setX(newPos.x);
            ((IPlayerMoveC2SPacket) moveC2SPacket).pulse$setY(newPos.y);
            ((IPlayerMoveC2SPacket) moveC2SPacket).pulse$setZ(newPos.z);
            ((IPlayerMoveC2SPacket) moveC2SPacket).pulse$setOnGround(true);
            lastPos = BlockPos.ofFloored(newPos);
        }
    }

    @EventHandler
    void render(Render3DEvent e) {
        if(lastPos != null) {
            Pulse3D.renderEdged(e.getMatrixStack(), Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.SECONDARY(), 180),
                    ThemeInfo.COLORSCHEME.ACCENT(), Vec3d.of(lastPos),
                    new Vec3d(1, 1, 1));
        }
    }

    @Override
    public void enable() {
        super.enable();
        lastPos = null;
    }

    @Override
    public void disable() {
        super.disable();
        if(Util.nullCheck() || lastPos == null) return;
        mc.player.setPosition(lastPos.toBottomCenterPos());
        PacketUtil.sendMove(lastPos.toBottomCenterPos());
    }
}
