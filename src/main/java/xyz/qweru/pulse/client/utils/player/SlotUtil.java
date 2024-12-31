package xyz.qweru.pulse.client.utils.player;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import java.util.ArrayList;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class SlotUtil {

    public static void runWithItem(SlotRunnable runnable, Item item, boolean silent) {
        if(mc.player.getOffHandStack().getItem().equals(item)) {
            runnable.run(-1, mc.player.getInventory());
        }

        int slot = InventoryUtils.getItemSlotHotbar(item);
        if(slot != -1) {
            PlayerInventory inventory = mc.player.getInventory();
            int prevSlot = inventory.selectedSlot;
            if(silent) {
                PacketUtil.send(new UpdateSelectedSlotC2SPacket(slot));
                runnable.run(slot, inventory);
                PacketUtil.send(new UpdateSelectedSlotC2SPacket(prevSlot));
            } else {
                inventory.selectedSlot = slot;
                runnable.run(slot, inventory);
                inventory.selectedSlot = prevSlot;
            }
        }
    }

    public static void runWithItemFilter(SlotRunnable runnable, ItemComparable comparable, boolean silent) {
        int slot = -1;
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (comparable.compare(stack)) {
                slot = i;
            }
        }

        if(slot != -1) {
            PlayerInventory inventory = mc.player.getInventory();
            int prevSlot = inventory.selectedSlot;
            if(silent) {
                PacketUtil.send(new UpdateSelectedSlotC2SPacket(slot));
                runnable.run(slot, inventory);
                PacketUtil.send(new UpdateSelectedSlotC2SPacket(prevSlot));
            } else {
                inventory.selectedSlot = slot;
                runnable.run(slot, inventory);
                inventory.selectedSlot = prevSlot;
            }
        }
    }

    @FunctionalInterface
    public interface ItemComparable {
        boolean compare(ItemStack item);
    }

    @FunctionalInterface
    public interface SlotRunnable {
        void run(int slot, PlayerInventory inventory);
    }

    public static void runWithItems(SlotRunnable runnable, boolean silent, Item... items) {
        int slot = InventoryUtils.getHotbarItem(items);
        if(slot != -1) {
            PlayerInventory inventory = mc.player.getInventory();
            int prevSlot = mc.player.getInventory().selectedSlot;
            if(silent) {
                PacketUtil.send(new UpdateSelectedSlotC2SPacket(slot));
                runnable.run(slot, inventory);
                PacketUtil.send(new UpdateSelectedSlotC2SPacket(prevSlot));
            } else {
                inventory.selectedSlot = slot;
                runnable.run(slot, inventory);
                inventory.selectedSlot = prevSlot;
            }
        }
    }

    static int lastF = -1;
    static int lastT = -1;

    /**
     * @param from slot
     * @param to slot
     */
    public static void swapInv(int from, int to) {
        PacketUtil.send(new PickFromInventoryC2SPacket(from));
        PacketUtil.send(new PickFromInventoryC2SPacket(to));
        lastF = from;
        lastT = to;
    }

    public static void swapBack() {
        if(lastT == -1 || lastF == -1) return;
        PacketUtil.send(new PickFromInventoryC2SPacket(lastT));
        PacketUtil.send(new PickFromInventoryC2SPacket(lastF));
    }


}
