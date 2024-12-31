package xyz.qweru.pulse.client.utils.player;

import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class SlotManager {

    private int clientSlot = -1;
    private int serverSlot = -1;
    private boolean cancelPackets = false;

    /**
     * Cancel any incoming UpdateSelectedSlot packets by immediately sending a new UpdateSelectedSlotP packet
     * @param cancelPackets boolean
     */
    public void cancelPackets(boolean cancelPackets) {
        this.cancelPackets = cancelPackets;
    }

    public void syncNow() {
        if(Util.nullCheck() || clientSlot == -1) return;
        PacketUtil.sendImmediately(new UpdateSelectedSlotC2SPacket(clientSlot));
    }

    public void sync() {
        if(Util.nullCheck() || clientSlot == -1) return;
        PacketUtil.send(new UpdateSelectedSlotC2SPacket(clientSlot));
    }

    /**
     * @return true if event should be cancelled
     */
    public boolean update(UpdateSelectedSlotS2CPacket packet) {
        serverSlot = packet.getSlot();
        clientSlot = packet.getSlot();
        if(cancelPackets) {
            syncNow();
            return true;
        }
        return false;
    }

    public void update(UpdateSelectedSlotC2SPacket packet) {
        serverSlot = packet.getSelectedSlot();
    }

    public void tick() {
        if(Util.nullCheck()) return;
        if(mc.player.getInventory().selectedSlot != clientSlot) clientSlot = mc.player.getInventory().selectedSlot;
    }

    public int getRealSlot() {
        return serverSlot;
    }

    public void selectOnServer(int slot) {
        PacketUtil.send(new UpdateSelectedSlotC2SPacket(slot));
    }

    public void selectOnClient(int slot) {
        mc.player.getInventory().selectedSlot = slot;
        clientSlot = slot;
    }

    public void selectSync(int slot) {
        clientSlot = slot;
        serverSlot = slot;
        mc.player.getInventory().selectedSlot = slot;
        sync();
    }

}
