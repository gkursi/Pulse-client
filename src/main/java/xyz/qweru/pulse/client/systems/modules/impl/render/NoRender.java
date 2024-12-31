package xyz.qweru.pulse.client.systems.modules.impl.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket;
import net.minecraft.particle.ParticleTypes;
import xyz.qweru.pulse.client.systems.events.HandlePacketEvent;
import xyz.qweru.pulse.client.systems.events.HudRenderEvent;
import xyz.qweru.pulse.client.systems.events.ParticleSpawnEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;

public class NoRender extends ClientModule {

    BooleanSetting vignette = booleanSetting()
            .name("Vignette")
            .description("Remove the slight darkness in the corner of your screen")
            .build();

    BooleanSetting potionIcons = booleanSetting()
            .name("Pot icons")
            .description("Remove potion icons")
            .build();

    BooleanSetting explosions = booleanSetting()
            .name("Explosions")
            .description("Remove explosion particles")
            .build();

    public BooleanSetting overlays = booleanSetting()
            .name("Overlays")
            .description("Remove all overlays (underwater, fire, wall)")
            .build();

    BooleanSetting damageTilt = booleanSetting()
            .name("Damage tilt")
            .description("Remove damage tilt")
            .build();

    BooleanSetting expParticles = booleanSetting()
            .name("Exp bottle particles")
            .description("Remove damage tilt")
            .build();

    public BooleanSetting swing = booleanSetting()
            .name("Swing")
            .description("Remove swing animation")
            .build();

    public BooleanSetting eat = booleanSetting()
            .name("Static eating")
            .description("Remove eating animation jitter")
            .build();

    public BooleanSetting blockBreakParticle = booleanSetting()
            .name("Block break particles")
            .description("Remove damage tilt")
            .build();

    public NoRender() {
        builder(this)
                .name("NoRender")
                .description("Disable rendering of some stuff")
                .settings(vignette, explosions, potionIcons, overlays, damageTilt, blockBreakParticle, expParticles, swing, eat)
                .category(Category.RENDER);
    }

    @EventHandler
    void particle(ParticleSpawnEvent e) {
        if (e.getParameters().getType() == ParticleTypes.EXPLOSION && explosions.isEnabled()) e.setCancelled(true);
        if(e.getParameters().getType() == ParticleTypes.EFFECT && expParticles.isEnabled()) e.setCancelled(true);
        if(e.getParameters().getType() == ParticleTypes.BLOCK && blockBreakParticle.isEnabled()) e.setCancelled(true);
    }

    @EventHandler
    void vignette(HudRenderEvent.Vignette e) {
        if (vignette.isEnabled()) e.setCancelled(true);
    }

    @EventHandler
    void potIcons(HudRenderEvent.Potion e) {
        if(potionIcons.isEnabled()) e.setCancelled(true);
    }

    @EventHandler
    void overlays(HudRenderEvent.WallOverlay e) {
        if(overlays.isEnabled()) e.setCancelled(true);
    }

    @EventHandler
    void getPacket(HandlePacketEvent e) {
        if(e.getPacket() instanceof DamageTiltS2CPacket dtp && damageTilt.isEnabled()) {
            e.setCancelled(true);
        }
    }

}