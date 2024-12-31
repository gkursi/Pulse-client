package xyz.qweru.pulse.client.systems.modules.impl.misc;

import net.minecraft.item.Items;
import net.minecraft.world.WorldEvents;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.player.SlotUtil;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class TotemDupe extends ClientModule {

    private final NumberSetting dupeItemCount = new NumberSetting("Dupe item count", "How many times should dupe", 1f, 64f, 16f, true);
    private final NumberSetting delay = new NumberSetting("Delay", "time in ms between actions", 0f, 1000f, 50f, true);

    // todo
//    BooleanSetting auto = booleanSetting()
//            .name("Auto")
//            .description("Automatically dupe once totem count is below the set number")
//            .build();
//
//    private final NumberSetting autoC = new NumberSetting("Trigger Count",
//            "if auto is enabled, totems will be automatically duped if the total count is smaller or equal to this number",
//            1f, 36f, 10f, true);


    public TotemDupe() {
        builder()
                .name("Totem Dupe")
                .description("On play.dupeanarchy.com, bypass for quickly duping totems")
                .category(Category.MISC)
                .settings(dupeItemCount, delay);
        dupeItemCount.setValueModifier((value -> (int) value));
        delay.setValueModifier((value -> (int) value));
    }

    @Override
    public void enable() {
        super.enable();
        toggle();
        ThreadManager.cachedPool.submit(() -> {
            if(Util.nullCheck()) return;
            int c = dupeItemCount.getValueInt();
            int slot = InventoryUtils.getItemSlotAll(Items.TOTEM_OF_UNDYING);
            if(slot == -1) return;
            int to = mc.player.getInventory().selectedSlot;
            SlotUtil.swapInv(slot, to);
            Util.sleep(delay.getValueLong());
            ChatUtil.sendServerMsg("/dupe " + c);
            Util.sleep(delay.getValueLong());
            SlotUtil.swapBack();
        });
    }
}
