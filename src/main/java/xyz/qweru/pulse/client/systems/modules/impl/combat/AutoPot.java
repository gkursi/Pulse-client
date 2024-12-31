package xyz.qweru.pulse.client.systems.modules.impl.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.util.Hand;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AutoPot extends ClientModule {

    BooleanSetting redupe = booleanSetting()
            .name("Redupe")
            .description("Dupe the pots")
            .build();

    NumberSetting health = numberSetting()
            .name("Health")
            .description("At what HP to start throwing")
            .range(0, 36)
            .defaultValue(10)
            .stepFullNumbers()
            .build();

    TextSetting dupeText = textSetting()
            .name("Dupe id")
            .description("item id to use when duping")
            .defaultValue("splash_potion")
            .build();

    public AutoPot() {
        builder()
                .name("Spam Pot")
                .description("Automatically spams splash pots")
                .settings(redupe, dupeText, health)
                .category(Category.COMBAT);
    }

    boolean skip = false;
    @EventHandler
    void tick(WorldTickEvent.Post ignored) {
        if(skip) {
            skip = false;
            return;
        } else skip = true;
        if(mc.player.getHealth() <= health.getValue()) {
            int count = InventoryUtils.totalItemCount(Items.SPLASH_POTION);
            if(count <= 2 && redupe.isEnabled()) dupe();
            else throwPot();
        }
    }

    void dupe() {
        ChatUtil.sendServerMsg("/dupe 4 " + dupeText.getValue());
    }

    void throwPot() {
        int slot = InventoryUtils.getItemSlotAll(Items.SPLASH_POTION);
        if(slot == -1) return;
        pickSwitch(slot);
        PlayerUtil.interact(mc.player, Hand.MAIN_HAND, mc.player.getInventory().selectedSlot, 90, mc.player.getYaw());
        pickSwapBack();
    }

    int pickSlot = -1;

    boolean pickSwitch(int slot) {
        if (slot >= 0) {
            pickSlot = slot;
            mc.getNetworkHandler().sendPacket(new PickFromInventoryC2SPacket(slot));

            return true;
        }
        return false;
    }
    void pickSwapBack() {
        if (pickSlot >= 0) {
            mc.getNetworkHandler().sendPacket(new PickFromInventoryC2SPacket(pickSlot));
            pickSlot = -1;
        }
    }

}
