package xyz.qweru.pulse.client.systems.modules.impl.world;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

public class LitematicaPrinter extends ClientModule {

    int placeAttempts = 2;
    int actionsPerTick = 1;
    boolean onlyHotbar = false;
    double moveDistancePerTick = 5;
    int placeRange = 256;

    double reach = 4;

    public LitematicaPrinter() {
        builder()
                .name("Litematica Printer")
                .description("Automatically build schematics")
                .category(Category.WORLD);
    }


    List<Action> actions = new ArrayList<>();
    @EventHandler
    void tick(WorldTickEvent.Post e) {
        WorldSchematic schematic = SchematicWorldHandler.getSchematicWorld();
        if(schematic == null) return;
        if(actions.isEmpty()) fillActionList(schematic);
        int actionCount = 0;
        for (Action action : actions) {
            if(actionCount >= actionsPerTick) break;
            action.execute();
            actionCount++;
        }
    }

    void fillActionList(WorldSchematic schematic) {
        BlockPos initial = mc.player.getBlockPos();
        int prevSlot = -1;
        HashMap<Item, Integer> slotMap = new HashMap<>();
        for (double x = -reach; x < reach; x++) {
            for (double y = -reach; x < reach; x++) {
                for (double z = -reach; x < reach; x++) {
                    if(x == 0 && (y == 0 || y == 1) && z == 0) continue; // dont place inside player
                    BlockPos pos = initial.add((int) x, (int) y, (int) z);
                    BlockState target = schematic.getBlockState(pos);
                    BlockState current = mc.world.getBlockState(pos);

                    if(target.equals(current) || target.isAir() || !current.isReplaceable()) continue; // will stop if block cannot be placed inside
                    Item targetItem = target.getBlock().asItem();
                    int slot;
                    if(!slotMap.containsKey(targetItem)) slot = slotMap.get(targetItem);
                    else {
                        slot = onlyHotbar ? InventoryUtils.getItemSlotHotbar(targetItem) : InventoryUtils.getItemSlotAll(targetItem);
                        if(slot == -1) {
                            this.setEnabled(false);
                            ChatUtil.err("Could not find item: " + targetItem.toString());
                            return;
                        }
                        slotMap.put(targetItem, slot);
                    }
                    Swap swap = slot == prevSlot ? null : new Swap(slot, slot < 9);
                    actions.add(new Action(null, swap, pos));
                }
            }
        }
        if(actions.isEmpty()) LOGGER.debug("Placed all blocks withing reach!");
        else LOGGER.debug("Added {} actions!", actions.size());
    }

    class Action {
        @Nullable
        private final Move move;
        @Nullable
        private final Swap swap;
        private final BlockPos place;

        public Action(@Nullable Move move, @Nullable Swap swap, BlockPos place) {
            this.move = move;
            this.swap = swap;
            this.place = place;
        }

        public void execute() {
            // moving
            if(move != null) {
                if(move.multiStep) {
                    lerpTP();
                } else {
                    mc.player.setPosition(move.target);
                    PacketUtil.sendMove(move.target);
                }
            }

            // swapping
            if(swap != null) {
                if(swap.invSwap) {
                    pickSwitch(swap.slot);
                } else {
                    mc.player.getInventory().selectedSlot = swap.slot;
                    PacketUtil.send(new UpdateSelectedSlotC2SPacket(swap.slot));
                }
            }

            for (int i = 0; i < placeAttempts; i++) {
                PlayerUtil.placeBlock(new BlockHitResult(Vec3d.of(place), Direction.UP, place, false));
            }
        }

        void lerpTP() {
            Vec3d from = mc.player.getPos();
            Vec3d to = move.target;

            double td = Math.ceil(from.distanceTo(to) / 8.5);
            for (int i = 1; i<=td; i++) {
                Vec3d curPos = from.lerp(to, i / td);
                PacketUtil.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(curPos.getX(), curPos.getY(), curPos.getZ(), mc.player.isOnGround()));
                mc.player.setPosition(curPos);
                LOGGER.debug("Set cur pos to {}", curPos);
            }
        }

        // blackout
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

    @Override
    public void disable() {
        super.disable();
        actions.clear();
    }

    record Swap(int slot, boolean invSwap) {}
    record Move(Vec3d target, boolean multiStep) {}
}
