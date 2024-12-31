package xyz.qweru.pulse.client.systems.modules.impl.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.systems.events.HudRenderEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.entity.EntityFinder;
import xyz.qweru.pulse.client.utils.render.NametagUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;
import xyz.qweru.pulse.client.utils.world.PosUtil;

import java.awt.*;
import java.util.Comparator;

import static java.awt.Color.*;
import static xyz.qweru.pulse.client.PulseClient.mc;

public class Nametags extends ClientModule {

    BooleanSetting distance = new BooleanSetting("Show distance", "Show distance in the nametag", false, false);
    BooleanSetting armor = new BooleanSetting("Show armor", "Show armor in the nametag", true, false);

    final Vector3d pos = new Vector3d();

    public Nametags() {
        builder(this)
                .name("Nametags")
                .description("Shows better nametags above players")
                .settings(distance, armor)
                .category(Category.RENDER);
    }

    // todo: implement without CopyOnWrite because it is too slow
    EntityFinder.COWEntityList entityList = new EntityFinder.COWEntityList();

    @EventHandler
    void tick(WorldTickEvent.Pre e) {
        Vec3d posC = mc.gameRenderer.getCamera().getPos();

        entityList = EntityFinder.findEntitiesInRange(512, mc.player.getPos()).getThreadSafe()
            .filter(entity -> entity instanceof PlayerEntity pe && pe.getGameProfile().getName() != mc.player.getGameProfile().getName())
            .sort(Comparator.comparing(en -> en.squaredDistanceTo(posC)));
    }
    @EventHandler
    private void onRender2D(HudRenderEvent.Hud event) {
        int count = entityList.get().size();

        for (int i = count - 1; i > -1; i--) {
            Entity entity = entityList.get().get(i);

            RenderUtil.set(pos, entity, event.getTickDelta());
            pos.add(0, entity.getHeight(), 0);

            if (NametagUtil.to2D(pos, 1)) {
                renderTag((PlayerEntity) entity, pos, event.getContext());
            }
        }
    }

    void renderTag(PlayerEntity e, Vector3d pos, DrawContext context) {

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        NametagUtil.renderNametag(context.getMatrices(), e, mc.getRenderTickCounter().getTickDelta(true), dispatcher, (matrices -> {

            FontRenderer text = RenderUtil.textRenderer;
            float offY = RenderUtil.fontOffsetY;

            String name = e.getName().getString();

            float absorption = e.getAbsorptionAmount();
            int health = Math.round(e.getHealth() + absorption);
            double healthPercentage = health / (e.getMaxHealth() + absorption);

            String healthText = " " + health;
            Color healthColor;

            if (healthPercentage <= 0.333) healthColor = RED;
            else if (healthPercentage <= 0.666) healthColor = ORANGE;
            else healthColor = GREEN;
            double dist = Math.round(PosUtil.distanceBetween(mc.player.getPos(), e.getPos()) * 10.0) / 10.0;
            String distText = " " + dist + "m";

            double width = 4 + text.getWidth(name + healthText);

            double widthHalf = width / 2;
            double heightDown = text.getStringHeight("AA", false);

            bg(-widthHalf, -heightDown, width, heightDown, matrices);
        }));



    }

    void bg(double x, double y, double w, double h, MatrixStack m) {
        Pulse2D.drawHudBase(m, (float) (x - 1), (float) (y - 1), (float) (w + 2), (float) (h + 2), Pulse2D.cornerRad, 0.85f);
    }

}
