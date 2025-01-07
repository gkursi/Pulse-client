package xyz.qweru.pulse.client.systems.modules.impl.world;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.mixin.iinterface.IWorld;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.render.world.PulseBlock;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ColorSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;
import xyz.qweru.pulse.client.utils.render.AnimationUtil;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;
import xyz.qweru.pulse.client.utils.world.PosUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

public class BoxBreaker extends ClientModule {

    public BoxBreaker() {
        super("Box Breaker", "Automatically break enemy player boxes", InputUtil.KEY_UNKNOWN, Category.WORLD);
        builder(this).settings(range, render, swap, onlyVisible, instaMine, imIgnoreBurrow, ignoreWebs)
                .settings("Crystal check", doCrystalCheck, crystalCheck, ccIgnoreBurrow)
                .settings("Color", miningFill, miningOutline, finishedFill, finishedOutline);
    }

    NumberSetting range = new NumberSetting("Range", "range", 0, 6, 4, true);
    BooleanSetting render = new BooleanSetting("Render", "adds visuals", true, true);
    BooleanSetting swap = new BooleanSetting("Swap", "swap to pickaxe", true, true);
    BooleanSetting onlyVisible = new BooleanSetting("Only visible", "Only mine visible blocks", false, true);
    BooleanSetting instaMine = new BooleanSetting("Rebreak", "Start instamining the mined block if insta break is enabled", false, true);
    BooleanSetting imIgnoreBurrow = new BooleanSetting("Don't rebreak burrow", "Don't start instamining if the mined block is the burrow block", true, true);

    BooleanSetting doCrystalCheck = new BooleanSetting("Do crystal check", "Do crystal check (below setting)", false, true);
    ModeSetting crystalCheck = modeSetting()
            .name("Crystal check")
            .description("inside: only mine blocks that have obby / bedrock below, above: only mine blocks that are obby and have air above them, either: inside or above")
            .defaultMode("either")
            .mode("inside")
            .mode("above")
            .mode("either")
            .build();
    BooleanSetting ccIgnoreBurrow = new BooleanSetting("Ignore burrow", "Ignores burrow block for crystal check", true, true);

    ColorSetting miningFill = new ColorSettingBuilder()
            .setName("Mining Fill")
            .setDescription("Fill color while mining")
            .setColor(new Color(235, 48, 53, 120))
            .build();
    ColorSetting miningOutline = new ColorSettingBuilder()
            .setName("Mining Outline")
            .setDescription("Outline color while mining")
            .setColor(new Color(235, 48, 53, 120).darker())
            .build();

    ColorSetting finishedFill = new ColorSettingBuilder()
            .setName("Finished Fill")
            .setDescription("Fill color when finished")
            .setColor(new Color(110, 193, 117, 120))
            .build();
    ColorSetting finishedOutline = new ColorSettingBuilder()
            .setName("Finished Outline")
            .setDescription("Outline color when finished")
            .setColor(new Color(110, 193, 117, 120).darker())
            .build();

    BooleanSetting ignoreWebs = new BooleanSetting("Ignore webs", "Don't mine a block if it is cobwebs", false, true);


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

        instamining = false;
    }

    List<MyBlock> blocks = new ArrayList<>();

    BlockPos lastTargetPos = null;
    PlayerEntity lastTarget = null;

    boolean swapped = false;
    boolean shouldUpdateSlot = false;
    boolean instamining = false;
    PulseBlock current = null;
    MyBlock currentBlock = null;
    @EventHandler
    void tick(WorldTickEvent.Pre ignored) {
        if(instamining) {
            if(currentBlock == null || Vec3d.of(currentBlock.blockPos).distanceTo(mc.player.getPos()) > 6 || (lastTarget != null && lastTargetPos != null && (lastTarget.isDead() || lastTarget.getWorld() == null || !lastTargetPos.equals(lastTarget.getBlockPos()) || lastTarget.distanceTo(mc.player) > 6))) {
                instamining = false;
                return;
            }
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, currentBlock.blockPos, Direction.UP));

            return;
        }
        blocks.removeIf(MyBlock::shouldRemove);

        if(!blocks.isEmpty()) {
            currentBlock = blocks.get(0);
            currentBlock.mine();
            current = new PulseBlock(currentBlock.blockPos, new Color(255, 10, 120, 180), new Color(215, 10, 80, 215));
        }

        if (shouldUpdateSlot && currentBlock == null) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            shouldUpdateSlot = false;
        }

        if (!swapped && swap.isEnabled()) {
            for (MyBlock block : blocks) {
                if (block.isReady()) {
                    if(instaMine.isEnabled() && !(imIgnoreBurrow.isEnabled() && block.burrowBlock)) {
                        instamining = true;
//                        LOGGER.debug("Started instamining");
                    }
                    int slot = InventoryUtils.findFastestTool(block.blockState);
                    if (slot == -1 || mc.player.getInventory().selectedSlot == slot) continue;
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                    swapped = true;
//                    shouldUpdateSlot = true;
//                    prevSwap = true;
                    break;
                }
            }
        }

        PlayerEntity player = null;
        double distance = Double.MAX_VALUE;
        for (Entity entity : mc.world.getEntities()) {
            if(entity instanceof PlayerEntity player1 && player1 != mc.player
                    && !PulseClient.friendSystem.isPlayerInSystem(player1) && mc.player.distanceTo(player1) < distance) {
                player = player1;
            }
        }
        if(player == null) return;
        List<BlockPos> blocks = getBlocks(player);
        if(blocks.isEmpty()) return;
        BlockPos blockPos = blocks.get(0);
        if (!isMiningBlock(blockPos)) {
            MyBlock b = new MyBlock().set(blockPos, Direction.UP, blockPos.equals(player.getBlockPos()));
            this.blocks.add(b);
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
        if(!render.isEnabled()) return;
        Pulse3D.renderThroughWalls();
        blocks.forEach(block -> block.render(event.getMatrixStack()));
        Pulse3D.stopRenderThroughWalls();
    }

    // skidded from meteor
    public class MyBlock {
        public BlockPos blockPos;
        public BlockState blockState;
        public Block block;
        public Direction direction;
        public boolean burrowBlock;

        public int timer;
        public boolean mining;
        public double progress;

        public MyBlock set(BlockPos pos, Direction direction, boolean burrowBlock) {
            this.blockPos = pos;
            this.direction = direction;
            this.burrowBlock = burrowBlock;
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

                BoxBreaker b = ((BoxBreaker) Managers.MODULE.getItemByClass(BoxBreaker.class));
                if(b.currentBlock != null && blockPos.equals(b.currentBlock.blockPos) && !b.instamining) {
                    b.currentBlock = null;
                    b.current = null;
                }
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

        void render(MatrixStack matrices) {
            if(progress >= 1) {
                Pulse3D.renderEdged(matrices, finishedFill.getJavaColor(), finishedOutline.getJavaColor(), Vec3d.of(blockPos), new Vec3d(1, 1, 1));
            } else {
                double renderSize = AnimationUtil.easeOutCirc(MathHelper.clamp(progress, 0, 1));
                double renderProg = 0.5 - (renderSize) / 2;

                Vec3d init = Vec3d.of(blockPos).add(renderProg, renderProg, renderProg);
                Vec3d size = new Vec3d(renderSize, renderSize, renderSize);
                Pulse3D.renderEdged(matrices, miningFill.getJavaColor(), miningOutline.getJavaColor(), init, size);
            }
        }
    }

    // for placing crystals near blocks
    BlockHitResult getInteractPos(BlockPos pos) {
        List<BlockPos> pa = new ArrayList<>();

        pa.add(pos.add(1, 0, 0));
        pa.add(pos.add(0, 0, 1));
        pa.add(pos.add(-1, 0,0));
        pa.add(pos.add(0, 0, -1));

        pa.add(pos.add(1, 0, -1));
        pa.add(pos.add(1, 0, 1));
        pa.add(pos.add(-1, 0, -1));
        pa.add(pos.add(-1, 0, 1));

        pa.add(pos.add(1, -1, 0));
        pa.add(pos.add(0, -1, 1));
        pa.add(pos.add(-1, -1,0));
        pa.add(pos.add(0, -1, -1));

        pa.add(pos.add(1, -1, -1));
        pa.add(pos.add(1, -1, 1));
        pa.add(pos.add(-1, -1, -1));
        pa.add(pos.add(-1, -1, 1));

        pa.add(pos.add(0, -1, 0));

        for (BlockPos blockPos : pa) {
            if(check(blockPos)) return create(blockPos.add(0, -1, 0));
        }

        return null;
    }

    BlockHitResult create(BlockPos pos) {
        return new BlockHitResult(Vec3d.of(pos), Direction.UP, pos, true);
    }

    boolean check(BlockPos pos) {
        return BlockUtil.getBlockAt(pos).equals(Blocks.AIR) && Util.equalsAny(BlockUtil.getBlockAt(pos.add(0, -1, 0)), Blocks.OBSIDIAN, Blocks.BEDROCK);
    }

    List<BlockPos> getBlocks(PlayerEntity player) {
        Vec3d pos = player.getPos();
        List<BlockPos> list = new ArrayList<>();

        list.add(BlockPos.ofFloored(pos)); // first mine burrow block

        // then city blocks
        list.add(BlockPos.ofFloored(pos.add(1, 0, 0)));
        list.add(BlockPos.ofFloored(pos.add(-1, 0, 0)));
        list.add(BlockPos.ofFloored(pos.add(0, 0, 1)));
        list.add(BlockPos.ofFloored(pos.add(0, 0, -1)));

        // then block above head todo setting
        list.add(BlockPos.ofFloored(pos.add(0, 2, 0)));

        // then surrounding face blocks todo setting
        list.add(BlockPos.ofFloored(pos.add(1, 1, 0)));
        list.add(BlockPos.ofFloored(pos.add(-1, 1, 0)));
        list.add(BlockPos.ofFloored(pos.add(0, 1, 1)));
        list.add(BlockPos.ofFloored(pos.add(0, 1, -1)));

        list.removeIf(pos1 -> !isValid(pos1));
        return list;
    }

    boolean isValid(BlockPos pos) {
        if(doCrystalCheck.isEnabled()) {
            boolean inside = Util.equalsAny(BlockUtil.getBlockAt(pos.add(0, -1, 0)), Blocks.OBSIDIAN, Blocks.BEDROCK);
            boolean above = Util.equalsAny(BlockUtil.getBlockAt(pos), Blocks.OBSIDIAN, Blocks.BEDROCK)
                    && BlockUtil.getBlockAt(pos.add(0, 1, 0)).equals(Blocks.AIR);

            if(crystalCheck.is("inside") && !inside) return false;
            else if(crystalCheck.is("above") && !above) return false;
            else if(crystalCheck.is("either") && !(above || inside)) return false;
        }
        if(onlyVisible.isEnabled() && !PlayerUtil.canSeePos(Vec3d.of(pos), range.getValue())) return false;
        if(ignoreWebs.isEnabled() && BlockUtil.getBlockAt(pos).equals(Blocks.COBWEB)) return false;
        return !(BlockUtil.getBlockAt(pos).equals(Blocks.AIR) || BlockUtil.getBlockAt(pos).equals(Blocks.WATER) || BlockUtil.getBlockAt(pos).equals(Blocks.LAVA) || BlockUtil.getBlockAt(pos).equals(Blocks.BEDROCK) || BlockUtil.getBlockAt(pos).equals(Blocks.END_PORTAL_FRAME))
                && PosUtil.distanceBetween(pos.toCenterPos(), mc.player.getPos()) <= range.getValue() && !InstantBreak.isBreaking(pos);
    }
}
