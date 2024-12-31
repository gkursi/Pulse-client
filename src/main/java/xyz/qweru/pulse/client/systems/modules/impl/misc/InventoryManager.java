package xyz.qweru.pulse.client.systems.modules.impl.misc;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

public class InventoryManager extends ClientModule {

    List<Item> layout = new ArrayList<>();

    BooleanSetting copyInv = booleanSetting()
            .name("Save layout")
            .description("Save your current inventory as a layout")
            .build();

    BooleanSetting dupe = booleanSetting()
            .name("Dupe")
            .description("On play.dupeanarchy.com, if a slot does not contain the expected item but the item is present elsewhere in the inventory, automatically dupe the item")
            .build();

//    NumberSetting delay = numberSetting()
//            .name("Action delay")
//            .description("How long to wait between inventory interactions")
//            .range(0, 2000)
//            .defaultValue(40)
//            .stepFullNumbers()
//            .build();

    public InventoryManager() {
        builder(this)
                .name("Inventory Manager")
                .description("Automatically manage inventory according to saved layout")
                .category(Category.MISC)
                .settings("Settings", copyInv, dupe);

        copyInv.addOnToggle(() -> {
            if(Util.nullCheck(mc)) return;
            layout.clear();
            for (int i = 0; i < 36; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                layout.add(i, stack.getItem());
                LOGGER.info("added item to layout: {}", stack.getName());
            }
        });

        disableOnExit();
    }


    List<InvalidItem> invalidItems = new ArrayList<>();
    @EventHandler
    void tick(WorldTickEvent.Pre e) {
        for (int i = 0; i < 36; i++) {
            if(layout.isEmpty()) return;
            ItemStack actual = mc.player.getInventory().getStack(i);
            Item expected = layout.get(i);

            if(!expected.equals(actual.getItem()) || (expected.equals(Items.AIR) && !actual.isEmpty())) {
                invalidItems.add(new InvalidItem(i, actual, expected));
            }
        }

        Iterator<InvalidItem> i = invalidItems.iterator();
        while (i.hasNext()) {
            InvalidItem item = i.next();
            if(item.actual.isEmpty()) {
                i.remove();
                if(dupe.isEnabled() && !item.expected.equals(Items.AIR) && InventoryUtils.getItemSlotAll(item.expected) != -1) {
                    String txt = item.expected.toString();
                    LOGGER.info("Item id: {} ({})", txt, item.expected.getName());
                }
                continue;
            }
            PacketUtil.send(new PickFromInventoryC2SPacket(item.slot));
            PacketUtil.send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, Direction.UP));
            mc.player.getInventory().setStack(item.slot, ItemStack.EMPTY);
            i.remove();
        }
    }

    record InvalidItem(int slot, ItemStack actual, Item expected) {}
}
