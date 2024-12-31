package xyz.qweru.pulse.client.systems.modules.impl.render;

import me.x150.renderer.render.Renderer3d;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.render.ui.color.Colors;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.world.PosUtil;

import java.awt.*;

import static java.lang.Math.abs;
import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

public class Tracers extends ClientModule {
    public Tracers() {
        builder(this)
                .name("Tracers")
                .description("Draws lines to nearby players")
                .category(Category.RENDER);
    }

    @EventHandler
    private void render3D(Render3DEvent e) {
        Renderer3d.renderThroughWalls();
        float delta = e.getTickCounter().getTickDelta(true);
        for (Entity entity : mc.world.getEntities()) {
            if(!(entity instanceof PlayerEntity player)) continue;
            if(entity == mc.player) continue;

            Vector3f pos = new Vector3f(0, 0, 1);
            Vec3d center = new Vec3d(pos.x, -pos.y, pos.z)
                    .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                    .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                    .add(mc.gameRenderer.getCamera().getPos());

            Vector3d vec = new Vector3d();
            RenderUtil.set(vec, entity, delta);
            Vec3d lineEnd = new Vec3d(vec.x, vec.y, vec.z).add(0, entity.getHeight() / 2, 0);

            double distance = PosUtil.distanceBetween(mc.player.getPos(), lineEnd);
            Color color = getColor(distance);

            if(PulseClient.friendSystem.isPlayerInSystem(player)) color = Colors.FRIEND;
            else if(PulseClient.rageSystem.isPlayerInSystem(player)) color = Colors.RAGE;

            Renderer3d.renderLine(e.getMatrixStack(), Pulse2D.injectAlpha(color, 150),
                    center, lineEnd);
        }
    }

    Color getColor(double distance) {
        if(distance > 30) return new Color(255, 255, 255);
        else if(distance > 15) return new Color(2, 142, 2);
        else return new Color(154, 33, 33);
    }
}
