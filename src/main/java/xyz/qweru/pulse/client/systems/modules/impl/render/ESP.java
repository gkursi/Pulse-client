package xyz.qweru.pulse.client.systems.modules.impl.render;

import me.x150.renderer.render.OutlineFramebuffer;
import me.x150.renderer.render.Renderer3d;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.events.HudRenderEvent;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class ESP extends ClientModule {

    public ModeSetting mode = modeSetting()
            .name("Mode")
            .description("How should the esp be renderer")
            .defaultMode("Box")
            .mode("Glow")
            .mode("2D")
            .mode("Box")
            .build();

    BooleanSetting expand = booleanSetting()
            .name("Expand box")
            .description("Slightly expand bounding box for cleaner visuals")
            .build();

    ModeSetting color = modeSetting()
            .name("Color")
            .description("Color setting")
            .defaultMode("Type-based")
            .mode("Single")
            .mode("Type-based")
            .build();

    BooleanSetting onlyLiving = booleanSetting()
            .name("Only living")
            .description("Only show living entities")
            .build();

    BooleanSetting passive = booleanSetting()
            .name("Passive")
            .description("Show passive entities")
            .build();

    BooleanSetting mobs = booleanSetting()
            .name("Mobs")
            .description("Show mobs")
            .build();

    BooleanSetting players = booleanSetting()
            .name("Players")
            .description("Show players")
            .build();

    BooleanSetting misc = booleanSetting()
            .name("Misc")
            .description("Show other entities")
            .build();

    BooleanSetting ignoreDead = booleanSetting()
            .name("Hide dead")
            .description("Hide dead entities")
            .defaultValue(true)
            .build();

    public ESP() {
        builder(this)
                .name("ESP")
                .description("Show entities trough walls")
                .settings(mode)
                .settings("Render", expand, color)
                .settings("Target", onlyLiving, ignoreDead, players, passive, mobs)
                .category(Category.RENDER);
    }

    Matrix4f lastProjMatrix = null;
    Frustum lastFrustum = null;
    @EventHandler
    void render3D(Render3DEvent e) {
        switch (mode.getCurrent()) {
            case "Glow" -> {}
            case "Box" -> {
                HashMap<Color, ArrayList<Box>> renders = new HashMap<>();
                ArrayList<Box> misc = new ArrayList<>();
                ArrayList<Box> players = new ArrayList<>();
                ArrayList<Box> mobs = new ArrayList<>();
                ArrayList<Box> passive = new ArrayList<>();
                for (Entity entity : mc.world.getEntities()) {
                    if(!isValid(entity)) continue;
                    if(entity == mc.player) continue;
                    Box box = entity.getBoundingBox().expand(expand.isEnabled() ? 0.1 : 0);
                    if(entity instanceof PlayerEntity) players.add(box);
                    else if(entity instanceof PassiveEntity) passive.add(box);
                    else if(entity instanceof MobEntity a) mobs.add(a.getBoundingBox());
                    else misc.add(box);
                }

                renders.put(new Color(10, 250, 10, 100), passive);
                renders.put(new Color(250, 10, 10, 100), mobs);
                renders.put(new Color(0, 255, 255, 100), players);
                renders.put(new Color(20, 20, 20, 100), misc);

                for (Color color : renders.keySet()) {
                    if(renders.get(color).isEmpty()) continue;
//                    OutlineFramebuffer.use(() -> {
                    Renderer3d.renderThroughWalls();
                    for (Box box : renders.get(color)) {
                        Renderer3d.renderEdged(e.getMatrixStack(), color, Pulse2D.injectAlpha(color.darker(), 180), box.getMinPos(), box.getMaxPos().subtract(box.getMinPos()));
                    }
//                    });
//                    OutlineFramebuffer.draw(Pulse2D.cornerRad, ThemeInfo.COLORSCHEME.ACCENT(), this.color.is("type-based") ? color : Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.SECONDARY(), 180));
                }
            }
            case "2D" -> {
                lastProjMatrix = e.getProjectionMatrix();
                lastFrustum = e.getFrustum();
            }
        }
    }

    @EventHandler
    void render2D(HudRenderEvent.Hud e) {
        if(mode.is("2D")) {

        }
    }

    public boolean isValid(Entity entity) {
        if(!entity.isAlive() && ignoreDead.isEnabled()) return false;
        if(!(entity instanceof LivingEntity) && onlyLiving.isEnabled()) return false;
        if(entity instanceof PassiveEntity && passive.isEnabled()) return true;
        if(entity instanceof MobEntity && mobs.isEnabled()) return true;
        if(entity instanceof PlayerEntity && players.isEnabled()) return true;
        else return misc.isEnabled();
    }

}
