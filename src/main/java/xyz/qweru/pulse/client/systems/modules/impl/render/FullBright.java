package xyz.qweru.pulse.client.systems.modules.impl.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class FullBright extends ClientModule {
    public FullBright() {
        builder(this)
                .name("FullBright")
                .description("No more darkness")
                .category(Category.RENDER);
    }

    @EventHandler
    private void tickEvent(WorldTickEvent.Post e) {
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999, 1, false, false, false));
    }

    @Override
    public void disable() {
        if(Util.nullCheck(mc)) return;
        mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
