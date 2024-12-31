package xyz.qweru.pulse.client.systems.modules.impl.world;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.events.StartBreakingBlockEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.world.BlockUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class PacketMine extends ClientModule {
    public PacketMine() {
        builder()
                .name("Packet mine")
                .description("Mine blocks with packets")
                .category(Category.WORLD);
    }

    @Override
    public void enable() {
        super.enable();
        swapped = false;
    }

    @Override
    public void disable() {
        super.disable();
        blocks.clear();
        if (shouldUpdateSlot) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            shouldUpdateSlot = false;
        }
    }

    List<MyBlock> blocks = new ArrayList<>();
    boolean autoSwitch = true;

    boolean swapped = false;
    boolean shouldUpdateSlot = false;
    @EventHandler
    void tick(WorldTickEvent.Pre ignored) {
        blocks.removeIf(MyBlock::shouldRemove);

        if(!blocks.isEmpty()) {
            blocks.get(0).mine();
        }

        if (shouldUpdateSlot) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            shouldUpdateSlot = false;
        }

        if (!swapped && autoSwitch) {
            for (MyBlock block : blocks) {
                if (block.isReady()) {
                    int slot = InventoryUtils.findFastestTool(block.blockState);
                    if (slot == -1 || mc.player.getInventory().selectedSlot == slot) continue;
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                    swapped = true;
                    shouldUpdateSlot = true;
                    break;
                }
            }
        }

    }

    public boolean isMiningBlock(BlockPos pos) {
        for (MyBlock block : blocks) {
            if (block.blockPos.equals(pos)) return true;
        }

        return false;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        Pulse3D.renderThroughWalls();
        blocks.forEach(myBlock -> myBlock.render(event));
        Pulse3D.stopRenderThroughWalls();
    }

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        if (!BlockUtil.isPosBreakable(event.blockPos)) return;

        event.cancel();

        swapped = false;

        if (!isMiningBlock(event.blockPos)) {
            MyBlock b = new MyBlock().set(event);
            blocks.add(b);
//            b.mine();
        }
    }

    public static class MyBlock {
        public BlockPos blockPos;
        public BlockState blockState;
        public Block block;

        public Direction direction;

        public int timer;
        public boolean mining;
        public double progress;

        public MyBlock set(StartBreakingBlockEvent event) {
            this.blockPos = event.blockPos;
            this.direction = event.direction;
            this.blockState = mc.world.getBlockState(blockPos);
            this.block = blockState.getBlock();
//            this.timer = delay.get();
            this.timer = 0;
            this.mining = false;
            this.progress = 0;

            return this;
        }

        public boolean shouldRemove() {
            boolean remove = mc.world.getBlockState(blockPos).getBlock() != block
                    || new Vec3d(mc.player.getX() - 0.5, mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ() - 0.5).distanceTo(
                            new Vec3d(blockPos.getX() + direction.getOffsetX(), blockPos.getY() + direction.getOffsetY(), blockPos.getZ() + direction.getOffsetZ())) > mc.player.getBlockInteractionRange();

            if (remove) {
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, blockPos, direction));
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }

            return remove;
        }

        public boolean isReady() {
            return progress >= 1;
        }

        public void mine() {
            sendMinePackets();

            double bestScore = -1;
            int bestSlot = -1;

            for (int i = 0; i < 9; i++) {
                double score = mc.player.getInventory().getStack(i).getMiningSpeedMultiplier(blockState);

                if (score > bestScore) {
                    bestScore = score;
                    bestSlot = i;
                }
            }

            progress += BlockUtil.getBreakDelta(bestSlot != -1 ? bestSlot : mc.player.getInventory().selectedSlot, blockState);
        }

        private void sendMinePackets() {
            if (timer <= 0) {
                if (!mining) {
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction));
                    mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));

                    mining = true;
                }
            }
            else {
                timer--;
            }
        }

        public void render(Render3DEvent event) {
            VoxelShape shape = mc.world.getBlockState(blockPos).getOutlineShape(mc.world, blockPos);

            double x1 = blockPos.getX();
            double y1 = blockPos.getY();
            double z1 = blockPos.getZ();
            double x2 = blockPos.getX() + 1;
            double y2 = blockPos.getY() + 1;
            double z2 = blockPos.getZ() + 1;

            if (!shape.isEmpty()) {
                x1 = blockPos.getX() + shape.getMin(Direction.Axis.X);
                y1 = blockPos.getY() + shape.getMin(Direction.Axis.Y);
                z1 = blockPos.getZ() + shape.getMin(Direction.Axis.Z);
                x2 = blockPos.getX() + shape.getMax(Direction.Axis.X);
                y2 = blockPos.getY() + shape.getMax(Direction.Axis.Y);
                z2 = blockPos.getZ() + shape.getMax(Direction.Axis.Z);
            }

            if (isReady()) {
//                event.renderer.box(x1, y1, z1, x2, y2, z2, readySideColor.get(), readyLineColor.get(), shapeMode.get(), 0);
                Pulse3D.renderEdged(event.getMatrixStack(), new Color(0, 200, 0, 70),
                        new Color(0, 225, 0, 90), new Vec3d(x1, y1, z1), new Vec3d(x2 - x1, y2 - y1, z2 - z1));
            } else {
//                event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
                Pulse3D.renderEdged(event.getMatrixStack(), new Color(200, 0, 0, 70),
                        new Color(225, 0, 0, 90), new Vec3d(x1, y1, z1), new Vec3d(x2 - x1, y2 - y1, z2 - z1));
            }
        }
    }
}
