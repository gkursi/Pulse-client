package xyz.qweru.pulse.client.systems.modules.impl.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.player.SlotUtil;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AutoTotem extends ClientModule {
    public AutoTotem() {
        builder(this)
                .name("AutoTotem")
                .description("Automatically puts totems in your offhand")
                .settings(safeHealth, item)
                .settings("Swap", switchDelay, onlyHotbar, onlyInInv, swapBack, strict)
                .settings("Right click", rcEnabled, rcItem)
                .category(Category.COMBAT);
    }

    NumberSetting switchDelay = numberSetting()
            .name("Delay")
            .description("wait ms")
            .range(0, 5000)
            .defaultValue(20)
            .stepFullNumbers()
            .build();

    NumberSetting safeHealth = numberSetting()
            .name("Totem HP")
            .description("will always hold a totem when below this amount of health")
            .range(0, 5000)
            .defaultValue(20)
            .stepFullNumbers()
            .build();

    BooleanSetting rcEnabled = booleanSetting()
            .name("Right click switch")
            .description("Switch to specified item while right clicking")
            .build();

    BooleanSetting onlyInInv = booleanSetting()
            .name("Only in inventory")
            .description("Only swap while in inventory")
            .build();

    BooleanSetting onlyHotbar = booleanSetting()
            .name("Only hotbar")
            .description("Only swap items in hotbar")
            .build();

    BooleanSetting swapBack = booleanSetting()
            .name("Swap back")
            .description("After swapping item in hotbar, swap back to previously selected slot")
            .build();

    ModeSetting item = modeSetting()
            .name("Offhand Item")
            .description("item to hold in offhand")
            .defaultMode("Totem")
            .mode("Gap")
            .mode("Crystal")
            .mode("Totem")
            .build();

    ModeSetting rcItem = modeSetting()
            .name("RClick Item")
            .description("item to hold in offhand while right clicking")
            .defaultMode("Totem")
            .mode("Gap")
            .mode("Crystal")
            .mode("Totem")
            .build();

    BooleanSetting strict = booleanSetting()
            .name("Strict")
            .description("Bypass some anticheats")
            .build();

    boolean running = false;
    @EventHandler
    private void preWorldTick(WorldTickEvent.Pre e) {
        if(running) return;
        if(onlyInInv.isEnabled() && !(mc.currentScreen instanceof InventoryScreen)) return;

        Item target = Items.TOTEM_OF_UNDYING;
        if(mc.player.getHealth() > safeHealth.getValue()) {
            if(mc.options.useKey.isPressed() && rcEnabled.isEnabled()) target = getItemFromString(rcItem.getCurrent());
            else target = getItemFromString(item.getCurrent());
        }

        if(mc.player.getOffHandStack().getItem().equals(target)) return;

        int slot = onlyHotbar.isEnabled() ? InventoryUtils.getItemSlotHotbar(target) : InventoryUtils.getItemSlotAll(target);
        if(slot == -1) {
            // todo setting
            slot = onlyHotbar.isEnabled() ? InventoryUtils.getItemSlotHotbar(Items.TOTEM_OF_UNDYING) : InventoryUtils.getItemSlotAll(Items.TOTEM_OF_UNDYING);
            if(slot == -1) return;
            return;
        }

        int finalSlot = slot;
        ThreadManager.cachedPool.submit(() -> {
            running = true;

            if(finalSlot < 9 && !strict.isEnabled()) {
                Util.sleep(((long) switchDelay.getValue()));
                PacketUtil.sendImmediately(new UpdateSelectedSlotC2SPacket(finalSlot));
                Util.sleep(((long) switchDelay.getValue()));
                PacketUtil.sendImmediately(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.UP));
                if(swapBack.isEnabled()) {
                    Util.sleep(((long) switchDelay.getValue()));
                    PacketUtil.sendImmediately(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                } else {
                    Managers.SLOT.selectOnClient(finalSlot);
                }
            } else {
                PacketUtil.sendImmediately(new PickFromInventoryC2SPacket(finalSlot));
                Util.sleep(((long) switchDelay.getValue()));
                PacketUtil.sendImmediately(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.UP));
                Util.sleep(((long) switchDelay.getValue()));
                PacketUtil.sendImmediately(new PickFromInventoryC2SPacket(finalSlot));
            }
            running = false;
        });

    }

    Item getItemFromString(String s) {
        return switch (s) {
            case "Gap" -> Items.ENCHANTED_GOLDEN_APPLE;
            case "Crystal" -> Items.END_CRYSTAL;

            default -> Items.TOTEM_OF_UNDYING;
        };
    }
}
