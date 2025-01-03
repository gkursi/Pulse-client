package xyz.qweru.pulse.client.systems.modules.impl.combat;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import xyz.qweru.pulse.client.mixin.iinterface.IWorldRenderer;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.render.world.PulseBlock;
import xyz.qweru.pulse.client.render.world.blocks.FadeOutBlock;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.events.PreSendPacketEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;
import xyz.qweru.pulse.client.utils.player.RotationUtil;
import xyz.qweru.pulse.client.utils.player.Rotations;
import xyz.qweru.pulse.client.utils.player.SlotUtil;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;
import xyz.qweru.pulse.client.utils.world.BlockUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Surround extends ClientModule {

    BooleanSetting protect = booleanSetting()
            .name("Keep placing")
            .description("Continue replacing broken blocks after the first place")
            .defaultValue(true)
            .build();

    BooleanSetting aboveHead = booleanSetting()
            .name("Above head")
            .description("Also place a block above your head")
            .defaultValue(true)
            .build();

    BooleanSetting aroundHead = booleanSetting()
            .name("Around head")
            .description("Also place blocks around your head")
            .build();

    BooleanSetting render = booleanSetting()
            .name("Render")
            .description("Adds visuals")
            .build();

    BooleanSetting prePlace = booleanSetting()
            .name("Pre-place")
            .description("(BROKEN) Attempts to bypass high ping and replace blocks just before they break, might have issues on low ping")
            .build();

    BooleanSetting anchorPlace = booleanSetting()
            .name("Place on anchors")
            .description("Will treat anchors as air, good against anchor aura")
            .build();

    BooleanSetting spamPlace = booleanSetting()
            .name("Spam place")
            .description("Constantly send block place packets, value of 'render' is ignored if this is enabled")
            .build();

    NumberSetting delay = numberSetting()
            .name("Delay")
            .description("How long to wait after each place")
            .stepFullNumbers()
            .range(0, 1000)
            .defaultValue(20)
            .build();

    NumberSetting bpt = numberSetting()
            .name("Blocks per tick")
            .description("How many blocks can be placed each tick")
            .stepFullNumbers()
            .range(0, 14)
            .defaultValue(6)
            .build();

    NumberSetting breakPercent = numberSetting()
            .name("Break %")
            .description("At what break % should the block be considered as air")
            .setValueModifier(value -> (float) Util.round(value, 2))
            .range(0, 1)
            .defaultValue(0.95f)
            .build();

    BooleanSetting center = booleanSetting()
            .name("Center")
            .description("Center the player before placing")
            .build();

    BooleanSetting silent = booleanSetting()
            .name("Silent switch")
            .description("Switch using packets")
            .build();

    // fixme
    BooleanSetting toggleOnMove = booleanSetting()
            .name("Toggle on move")
            .description("Disable module on move")
            .build();

    BooleanSetting placeOnExit = booleanSetting()
            .name("Place on disable")
            .description("Place again on disable")
            .build();

    BooleanSetting rotate = booleanSetting()
            .name("Rotate")
            .description("Rotate when placing")
            .build();

    BooleanSetting extend = booleanSetting()
            .name("Extend")
            .description("Extend your surround")
            .build();

    BooleanSetting swing = booleanSetting()
            .name("Swing")
            .description("Render a swing animation")
            .build();

    public Surround() {
        builder(this)
                .name("Surround")
                .description("Surrounds you with obsidian")
                .settings("Place", protect, aboveHead, aroundHead, placeOnExit, extend)
                .settings(render, swing, delay, center, silent, toggleOnMove, rotate, bpt)
                .settings("Pre-place", prePlace, breakPercent, anchorPlace, spamPlace)
                .category(Category.COMBAT);
    }

    @Override
    public void enable() {
        super.enable();
        if(Util.nullCheck()) return;
        if(center.isEnabled()) {
            Vec3d center = BlockPos.ofFloored(mc.player.getPos()).toBottomCenterPos();
            mc.player.setPos(center.x, center.y, center.z);
        }
        if(protect.isEnabled()) return;
        if(lock) {
//            ChatUtil.warn("Thread is locked, stopping");
            return;
        }
        ThreadManager.cachedPool.submit(this::place);
        this.setEnabled(false);
    }

    @EventHandler
    void tick(WorldTickEvent.Pre ignored) {
        if(!lock) ThreadManager.cachedPool.submit(this::place);
    }

    boolean lock = false;
    public static boolean placing = false;
    Vec3d curPos = new Vec3d(0, 0, 0);
    List<PulseBlock> places = new CopyOnWriteArrayList<>();
    Int2ObjectMap<BlockBreakingInfo> map = null;
    void place() {
        if(lock) return;
        lock = true;
        map = ((IWorldRenderer) mc.worldRenderer).pulse$getBlockBreakingInfos();

        Vec3d centerPos = mc.player.getPos();
        ArrayList<BlockPos> places = new ArrayList<>();
        places.add(BlockPos.ofFloored(centerPos.add(1, 0, 0)));
        places.add(BlockPos.ofFloored(centerPos.add(0, 0, 1)));
        places.add(BlockPos.ofFloored(centerPos.add(-1, 0, 0)));
        places.add(BlockPos.ofFloored(centerPos.add(0, 0, -1)));

        if(extend.isEnabled()) {
            ArrayList<BlockPos> e = new ArrayList<>();
            for (BlockPos place : places) {
                e.add(place.add(1, 0, 0));
                e.add(place.add(0, 0, 1));
                e.add(place.add(-1, 0, 0));
                e.add(place.add(0, 0, -1));
            }
            places.addAll(e);
        }

        if(aboveHead.isEnabled()) places.add(BlockPos.ofFloored(centerPos.add(0, 2, 0)));
        if(aroundHead.isEnabled()) {
            places.add(BlockPos.ofFloored(centerPos.add(1, 1, 0)));
            places.add(BlockPos.ofFloored(centerPos.add(0, 1, 1)));
            places.add(BlockPos.ofFloored(centerPos.add(-1, 1, 0)));
            places.add(BlockPos.ofFloored(centerPos.add(0, 1, -1)));
        }

        places.add(BlockPos.ofFloored(centerPos.add(0, -1, 0)));

        //fixme error in blockmanager
//        Managers.BLOCK.registerOnce$onBlockAir((pos, state) -> {
//            if(Util.nullCheck() || !isEnabled()) return;
//            if(places.contains(pos)) SlotUtil.runWithItem((slot, inventory) -> {
//                    this.places.add(getBlockFrom(pos));
//                    PlayerUtil.placeBlock(new BlockHitResult(curPos, Direction.DOWN, pos, false));
//                }, Items.OBSIDIAN, silent.isEnabled());
//        });

        places.removeIf(this::isNotValid);

        if(!places.isEmpty()) {
            this.places.clear();

                SlotUtil.runWithItem((slot, inventory) -> {
                    int i = 0;
                    placing = true;
                    for (BlockPos place : places) {
                        if(i > bpt.getValue()) {
                            placing = false;
                            break;
                        }
                        if(render.isEnabled() && !spamPlace.isEnabled()) this.places.add(getBlockFrom(place));
                        if(!BlockUtil.isPosReplaceable(place)) continue;
                        curPos = new Vec3d(place.getX(), place.getY(), place.getZ());
                        if(rotate.isEnabled()) {
                            Rotations.rotate(Rotations.getYaw(place.toCenterPos()), Rotations.getPitch(place.toCenterPos()), 10000, () -> {
                                PlayerUtil.placeBlock(new BlockHitResult(curPos, Direction.DOWN, place, false));
                            });
                        } else {
                            Util.sleep(((long) delay.getValue()));
                            PlayerUtil.placeBlock(new BlockHitResult(curPos, Direction.DOWN, place, false));
                        }
                        i++;
                    }
                    placing = false;
                }, Items.OBSIDIAN, silent.isEnabled());
        }

        curPos = null;
        lock = false;
    }

    // todo: optimize
    // only map progress to blockpos once every tick instead of doing it each pos check
    boolean isNotValid(BlockPos blockPos) {
        boolean replacable = BlockUtil.isPosReplaceable(blockPos)
                || (anchorPlace.isEnabled() && BlockUtil.getBlockAt(blockPos).equals(Blocks.RESPAWN_ANCHOR));

        boolean almostBroken = false;

        if(map != null) {
            for (BlockBreakingInfo info : map.values()) {
                BlockPos pos = info.getPos();
                if(pos.equals(blockPos)) {
                    int stage = info.getStage();

                    double shrinkFactor = (9 - (stage + 1)) / 9d;
                    double progress = 1d - shrinkFactor;
                    if(progress >= breakPercent.getValue()) almostBroken = true;
                    break;
                }
            }
        }

        return !(
                (
                        ((prePlace.isEnabled() && almostBroken) || replacable)
                        && !blockPos.equals(mc.player.getBlockPos()))
                || spamPlace.isEnabled());
    }

    PulseBlock getBlockFrom(BlockPos pos) {
        return new FadeOutBlock(pos, Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.ACCENT(), 100), Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.ACCENT(), 255), 350);
    }

    @EventHandler
    void render(Render3DEvent e) {
        Iterator<PulseBlock> i = places.iterator();
        while (i.hasNext()) {
            PulseBlock pos = i.next();
            if(pos == null) return;
            pos.render(e.getMatrixStack());
        }
    }

    @Override
    public void disable() {
        super.disable();
        places.clear();
        if(placeOnExit.isEnabled() && protect.isEnabled()) {
            if(Util.nullCheck()) return;
            if(center.isEnabled()) {
                Vec3d center = BlockPos.ofFloored(mc.player.getPos()).toBottomCenterPos();
                mc.player.setPos(center.x, center.y, center.z);
            }
            if(lock) {
//                ChatUtil.warn("Thread is locked, stopping");
                return;
            }
            ThreadManager.cachedPool.submit(this::place);
        }
        placing = false;
        lock = false;
    }

    @EventHandler
    void move(PreSendPacketEvent e) {
        if(!(e.getPacket() instanceof PlayerMoveC2SPacket pm) || !pm.changesPosition()) return;
        Vec3d newPos = new Vec3d(pm.getX(mc.player.getX()), pm.getY(mc.player.getY()), pm.getZ(mc.player.getZ()));
        if(mc.player.getPos().distanceTo(newPos) > 1 && toggleOnMove.isEnabled()) {
            setEnabled(false);
        }
    }
}
