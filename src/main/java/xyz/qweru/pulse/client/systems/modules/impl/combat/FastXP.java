package xyz.qweru.pulse.client.systems.modules.impl.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class FastXP extends ClientModule {

    public FastXP() {
        builder(this)
                .name("FastXP")
                .description("Throw xp bottles faster")
                .settings(delay)
                .category(Category.COMBAT);
    }

    NumberSetting delay = numberSetting()
            .name("Delay")
            .description("Throw delay")
            .defaultValue(25)
            .range(0, 500)
            .stepFullNumbers()
            .build();

    TimerUtil timer = new TimerUtil();
    @EventHandler
    void preWorldTick(WorldTickEvent.Pre e) {
        if(Util.nullCheck()) return;
        if(mc.options.useKey.isPressed() &&
                 mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot).getItem().equals(Items.EXPERIENCE_BOTTLE)){
            if(timer.hasReached(delay.getValue())) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                timer.reset();
            }
        }
    }

}
