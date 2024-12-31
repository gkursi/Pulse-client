package xyz.qweru.pulse.client.systems.modules.impl.combat;

import me.x150.renderer.render.Renderer3d;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.render.world.blocks.FadeOutBlock;
import xyz.qweru.pulse.client.systems.events.InstamineEvent;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.mixin.iinterface.IWorld;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.impl.world.InstantBreak;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ColorSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.entity.DamageUtils;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.player.SlotUtil;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;
import xyz.qweru.pulse.client.utils.world.PosUtil;

import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AutoAnchor extends ClientModule {

    NumberSetting minSelfHealth = numberSetting()
            .name("Min health")
            .description("Minimum amount of health required for the module to run")
            .range(0, 40)
            .defaultValue(3)
            .build();

    NumberSetting performanceModeTicks = numberSetting()
            .name("Skip Ticks")
            .description("How many ticks should skip place calculations")
            .range(0, 20)
            .defaultValue(0)
            .stepFullNumbers()
            .build();

    BooleanSetting performanceMode = booleanSetting()
            .name("Performance mode")
            .description("Calculates placements less")
            .build();

    BooleanSetting extrapolate = booleanSetting()
            .name("Extrapolate target pos")
            .description("Predicts future positions of targets based on velocity")
            .defaultValue(true)
            .build();

    NumberSetting extrapolationTicks = numberSetting()
            .name("Extrapolation ticks")
            .description("How many ticks of movement should be predicted")
            .range(0, 40)
            .defaultValue(1)
            .stepFullNumbers()
            .build();

    NumberSetting crystalScanRange = numberSetting()
            .name("Scan range")
            .description("How far around the target should anchor placements be scanned for")
            .range(0, 10)
            .defaultValue(3)
            .stepFullNumbers()
            .build();

    NumberSetting interactionRange = numberSetting()
            .name("Interaction range")
            .description("How far from the player can crystals be placed / broken")
            .range(0, 10)
            .defaultValue(5)
            .stepFullNumbers()
            .build();

    NumberSetting targetRange = numberSetting()
            .name("Target range")
            .description("How far from the player can entities be targeted")
            .range(0, 15)
            .defaultValue(7)
            .stepFullNumbers()
            .build();

    ModeSetting placeSort = modeSetting()
            .name("Place priority")
            .description("What value should be prioritized before placing")
            .defaultMode("damage")
            .mode("distance")
            .mode("damage")
            .build();

    NumberSetting maxSelfDamage = numberSetting()
            .name("Max self damage")
            .description("Max amount of damage that can be dealt to self")
            .range(0, 40)
            .defaultValue(8.9f)
            .build();

    NumberSetting minTargetDamage = numberSetting()
            .name("Min target damage")
            .description("Min amount of damage that can be dealt to target")
            .range(0, 40)
            .defaultValue(5)
            .build();

    BooleanSetting silentSwitch = booleanSetting()
            .name("Silent switch")
            .description("Switches to anchors using packets")
            .build();

    NumberSetting anchorDelay = numberSetting()
            .name("Anchor delay")
            .description("Anchor place delay in milliseconds")
            .range(0, 3000)
            .defaultValue(100)
            .stepFullNumbers()
            .build();

    NumberSetting glowstoneDelay = numberSetting()
            .name("Glowstone delay")
            .description("Glowstone place delay in milliseconds")
            .range(0, 3000)
            .defaultValue(100)
            .stepFullNumbers()
            .build();

    NumberSetting breakDelay = numberSetting()
            .name("Break delay")
            .description("Anchor break delay in milliseconds")
            .range(0, 3000)
            .defaultValue(100)
            .stepFullNumbers()
            .build();

    BooleanSetting render = booleanSetting()
            .name("Render")
            .description("Adds visuals to the module")
            .defaultValue(true)
            .build();

    ColorSetting color = new ColorSettingBuilder()
            .setName("Color")
            .setDescription("Render color")
            .build();

    ModeSetting placeMode = modeSetting()
            .name("Interact mode")
            .description("How should blocks be placed / broken")
            .defaultMode("Client")
            .mode("Packet")
            .mode("Client")
            .build();

    ModeSetting placeCheckMode = modeSetting()
            .name("Placement check mode")
            .description("How should placement checks be done (Fast is usually as good as accurate except for some edge cases)")
            .defaultMode("Fast")
            .mode("Accurate")
            .mode("Fast")
            .build();

    BooleanSetting swing = booleanSetting()
            .name("Swing")
            .description("Swing hand on place")
            .defaultValue(true)
            .build();

    BooleanSetting pauseOnUse = booleanSetting()
            .name("Pause on use")
            .description("Pause module when using another item")
            .defaultValue(true)
            .build();

    BooleanSetting pauseOnSurrond = booleanSetting()
            .name("Pause on surround")
            .description("Pause module when surround is placing")
            .defaultValue(true)
            .build();

    NumberSetting placesPerTick = numberSetting()
            .name("Anchors per tick")
            .description("crystals per tick")
            .range(0, 10)
            .defaultValue(1)
            .stepFullNumbers()
            .build();

    BooleanSetting redupe = booleanSetting()
            .name("Redupe")
            .description("Automatically dupe items on play.dupeanarchy.com")
            .build();

    BooleanSetting prePlace = booleanSetting()
            .name("(NI) Use preplace")
            .description("Enables below cheks")
            .defaultValue(true)
            .build();

    ModeSetting placeOnInstamine = modeSetting()
            .name("Place")
            .description("When to place, before - before instamine, after - after instamine, ignore - always assume instamine block is air)")
            .defaultMode("ignore")
            .mode("before")
            .mode("after")
            .build();

    BooleanSetting onlyOncePerTick = booleanSetting()
            .name("Only once per tick")
            .description("Don't place on instamine if already placed within that tick")
            .defaultValue(true)
            .build();

    public AutoAnchor() {
        builder()
                .name("Auto Anchor")
                .description("Automatically blow up anchors on other people")
                .settings("Range", crystalScanRange, interactionRange, targetRange)
                .settings("Health", minSelfHealth, maxSelfDamage, minTargetDamage)
                .settings("Delay", anchorDelay, glowstoneDelay, breakDelay, placesPerTick)
                .settings("Misc", performanceMode, performanceModeTicks, placeSort, silentSwitch, placeMode, placeCheckMode, redupe)
                .settings("Render", render, color, swing)
                .settings("Extrapolation", extrapolate, extrapolationTicks)
                .settings("Pause", pauseOnUse, pauseOnSurrond)
                .settings("Instamine place", prePlace, placeOnInstamine, onlyOncePerTick)
                .category(Category.COMBAT);
    }

    int ptCounter = 0;
    boolean running = false;
    boolean placed = false;
    @EventHandler
    private void tick(WorldTickEvent.Post e) {
        placed = false;
        if(mc.player.getHealth() < minSelfHealth.getValue()) {
//            PulseClient.LOGGER.info("Health too low!");
            return;
        }
//        PulseClient.LOGGER.info("Crystal Aura > Tick");
        if(performanceMode.isEnabled()) {
//            PulseClient.LOGGER.info("Performance mode enabled");
            if(ptCounter == 0) {
//                PulseClient.LOGGER.info("[Performance mode] calculating placements");
                calcPlacements();
                ptCounter++;
            } else if(ptCounter >= performanceModeTicks.getValue()) {
                ptCounter = 0;
            }
            ThreadManager.cachedPool.submit(this::run);
        } else {
            calcPlacements();
            ThreadManager.cachedPool.submit(this::run);
        }
    }

    List<AnchorData> futureAnchorLocations = new ArrayList<>();
    int cxI = 0;
    int max = 4;
    void calcPlacements() {
        if(running) return;
        futureAnchorLocations.clear();
        for (Entity entity : mc.world.getEntities()) {
            if(futureAnchorLocations.size() >= max) return;
            if(!(entity instanceof LivingEntity) || !entity.isAlive() || (entity instanceof PlayerEntity pe && PulseClient.friendSystem.isPlayerInSystem(pe.getGameProfile().getName()))) continue;
            if(entity.getDisplayName().equals(mc.player.getDisplayName())) {
                continue;
            }
            if(PosUtil.distanceBetween(mc.player.getPos(), entity.getPos()) > targetRange.getValue()) {
                continue;
            }
            List<AnchorData> possiblePlacements = new ArrayList<>();
            Vec3d targetPos = entity.getPos();
//            targetBoxes.add(entity.getBoundingBox());
            if(extrapolate.isEnabled()) targetPos = PosUtil.predictPos((LivingEntity) entity, ((int) Math.floor(extrapolationTicks.getValue())));
            cxI = 0;
            double hp = mc.player.getHealth();
            BlockUtil.forBlocksInRange((x, y, z, bp) -> {
                switch (placeCheckMode.getCurrent()) {
                    case "Accurate":
                        for (Entity worldEntity : mc.world.getEntities()) {
                            if(worldEntity instanceof LivingEntity le && PosUtil.boxCollision(Box.of(bp.toCenterPos(), 1, 1, 1), entity.getBoundingBox())) {
                                cxI++;
                                return;
                            }
                        }
                    case "Fast":
                        if(
                                bp.equals(BlockPos.ofFloored(mc.player.getPos())) || bp.equals(BlockPos.ofFloored(entity.getPos())) || PosUtil.distanceBetween(entity.getPos(), bp.toCenterPos()) < 1 || PosUtil.boxCollision(Box.of(bp.toCenterPos(), 1, 1, 1), entity.getBoundingBox())// if crystal position is same as player / target pos
                        ) {
                            cxI++;
                            return;
                        }
                }
                if(PosUtil.distanceBetween(mc.player.getPos(), bp.toCenterPos()) <= interactionRange.getValue()) {
                    if(canPlaceAnchor(bp)) {
                        float damage = DamageUtils.anchorDamage((LivingEntity) entity, bp.toCenterPos());
                        float selfDamage = DamageUtils.anchorDamage(mc.player, bp.toCenterPos());
                        double distance = PosUtil.distanceBetween(bp.toCenterPos(), entity.getPos());
                        if(BlockUtil.getBlockAt(bp).equals(Blocks.RESPAWN_ANCHOR)) {
                            BlockState state = mc.world.getBlockState(bp);
                            int charges = state.get(Properties.CHARGES).intValue();
                            possiblePlacements.add(new AnchorData(bp.toCenterPos(), distance, damage, selfDamage, false, charges < 1, entity.getPos()));
                        } else {
                            possiblePlacements.add(new AnchorData(bp.toCenterPos(), distance, damage, selfDamage, true, true, entity.getPos()));
                        }
                    }

                }
                cxI++;
            }, ((int) crystalScanRange.getValue()), targetPos);
            AnchorData bestPlacement = null;
            switch (placeSort.getCurrent()) {
                case "damage" -> bestPlacement = getBestDamage(possiblePlacements);
                case "distance" -> bestPlacement = getBestDistance(possiblePlacements);
            }

            if(bestPlacement != null) {
                futureAnchorLocations.add(bestPlacement);
            }
        }
    }

    AnchorData getBestDamage(List<AnchorData> placements) {
        AnchorData bestCrystal = null;
        for (AnchorData placement : placements) {
            if(bestCrystal == null) {
                if(placement.damageToTarget > minTargetDamage.getValue() && placement.damageToSelf < maxSelfDamage.getValue()) bestCrystal = placement;
            } else
            if(
                    (placement.damageToTarget > bestCrystal.damageToTarget && placement.damageToSelf <= maxSelfDamage.getValue()) ||
                            (Math.floor(placement.damageToTarget) == Math.floor(bestCrystal.damageToTarget) && placement.damageToSelf <= maxSelfDamage.getValue() && placement.damageToSelf < bestCrystal.damageToSelf)
            ) {
                bestCrystal = placement;
            }
        }
        return bestCrystal;
    }

    AnchorData getBestDistance(List<AnchorData> placements) {
        AnchorData bestCrystal = null;
        for (AnchorData placement : placements) {
            if(bestCrystal == null) {
                bestCrystal = placement;
                continue;
            }
            if(
                    (placement.distanceToTarget < bestCrystal.distanceToTarget && placement.damageToSelf <= maxSelfDamage.getValue()) ||
                            (Math.floor(placement.distanceToTarget) == Math.floor(bestCrystal.distanceToTarget) && placement.damageToSelf <= maxSelfDamage.getValue() && placement.damageToSelf < bestCrystal.damageToSelf)
            ) {
                bestCrystal = placement;
            }
        }
        return bestCrystal;
    }

    boolean canPlaceAnchor(BlockPos pos) {
        return Util.equalsAny(BlockUtil.getBlockAt(pos), Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.RESPAWN_ANCHOR);
    }

    int dupeTicks = 0;
    void run() {
        if(running || (Util.isUsingItem() && pauseOnUse.isEnabled())) return;
        running = true;
        if(!futureAnchorLocations.isEmpty()) SlotUtil.runWithItem((slot, inventory) -> {
            for (AnchorData anchorData : futureAnchorLocations) {
                placed = true;
                for (int i = 0; i < placesPerTick.getValueInt(); i++) {
                    doAnchor(anchorData);
                }
            }

            if(redupe.isEnabled()) {
                int countA = InventoryUtils.totalItemCount(Items.RESPAWN_ANCHOR);
                if(countA <= 32) {
                    if(dupeTicks > 0) dupeTicks--;
                    else {
                        if(countA == 32) ChatUtil.sendServerMsg("/dupe 1 respawn_anchor");
                        else if(countA >= 16) ChatUtil.sendServerMsg("/dupe 4 respawn_anchor");
                        else ChatUtil.sendServerMsg("/dupe 64 respawn_anchor");
                        dupeTicks = 10;
                    }
                }

                int countG = InventoryUtils.totalItemCount(Items.GLOWSTONE);
                if(countG <= 32) {
                    if(countG == 32) ChatUtil.sendServerMsg("/dupe 1 glowstone");
                    else if(countG >= 16) ChatUtil.sendServerMsg("/dupe 4 glowstone");
                    else ChatUtil.sendServerMsg("/dupe 64 glowstone");
                }
            }
        }, Items.RESPAWN_ANCHOR, silentSwitch.isEnabled());
        running = false;
    }

    void run$instamine() {
        if(running || (Util.isUsingItem() && pauseOnUse.isEnabled()) || placed) return;
        running = true;
        if(!futureAnchorLocations.isEmpty()) SlotUtil.runWithItem((slot, inventory) -> {
            for (AnchorData anchorData : futureAnchorLocations) {
                for (int i = 0; i < placesPerTick.getValueInt(); i++) {
                    doAnchor(anchorData);
                }
            }

            if(redupe.isEnabled()) {
                int countA = InventoryUtils.totalItemCount(Items.RESPAWN_ANCHOR);
                if(countA <= 32) {
                    if(dupeTicks > 0) dupeTicks--;
                    else {
                        if(countA == 32) ChatUtil.sendServerMsg("/dupe 1 respawn_anchor");
                        else if(countA >= 16) ChatUtil.sendServerMsg("/dupe 4 respawn_anchor");
                        else ChatUtil.sendServerMsg("/dupe 64 respawn_anchor");
                        dupeTicks = 10;
                    }
                }

                int countG = InventoryUtils.totalItemCount(Items.GLOWSTONE);
                if(countG <= 32) {
                    if(countG == 32) ChatUtil.sendServerMsg("/dupe 1 glowstone");
                    else if(countG >= 16) ChatUtil.sendServerMsg("/dupe 4 glowstone");
                    else ChatUtil.sendServerMsg("/dupe 64 glowstone");
                }
            }
        }, Items.RESPAWN_ANCHOR, silentSwitch.isEnabled());
        running = false;
    }

    void doAnchor(AnchorData anchorData) {
        if(pauseOnSurrond.isEnabled() && Surround.placing) return;
        if(anchorData.placeAnchor) {
            Util.sleep(((long) anchorDelay.getValue()));
            if (swing.isEnabled() && !mc.player.handSwinging) mc.player.swingHand(Hand.MAIN_HAND);
            placeBlock(anchorData.pos);
            Util.sleep(((long) glowstoneDelay.getValue()));
        }
        SlotUtil.runWithItem(((slot1, inventory1) -> {
            placeBlock(anchorData.pos, true);
        }), Items.GLOWSTONE, silentSwitch.isEnabled());
        Util.sleep(((int) breakDelay.getValue()));
        placeBlock(anchorData.pos, true);
    }

    void placeBlock(Vec3d pos) {
        placeBlock(pos, false);
    }

    void placeBlock(Vec3d pos, boolean insideBlock) {
        BlockHitResult result = new BlockHitResult(pos, Direction.UP, BlockPos.ofFloored(pos), insideBlock);
        switch (placeMode.getCurrent().toLowerCase()) {
            case "packet" -> {
                PendingUpdateManager pendingUpdateManager = ((IWorld) mc.world).pulse$getPendingUpdateManager().incrementSequence();
                try {
                    PacketUtil.sendImmediately(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, pendingUpdateManager.getSequence()));
                } catch (Throwable e) {
                    if(pendingUpdateManager != null) {
                        try {
                            pendingUpdateManager.close();
                        } catch (Throwable var6) {
                            e.addSuppressed(var6);
                        }
                    }
                    throw e;
                }
            }
            case "client" -> {
                try {
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, result);
                } catch (Exception ignored) {}
            }
        }
        if(render.isEnabled()) fades.add(new FadeOutBlock(BlockPos.ofFloored(pos), color.getJavaColor(), color.getJavaColor().darker(), 450));
    }

    CopyOnWriteArrayList<FadeOutBlock> fades = new CopyOnWriteArrayList<>();
    @EventHandler
    private void render3D(Render3DEvent e) {
        if(!render.isEnabled()) return;
        Renderer3d.renderThroughWalls();
        for (int i = 0; i < fades.size(); i++) {
            FadeOutBlock block = fades.get(i);
            if(block.hasFaded()) fades.remove(i);
            else block.render(e.getMatrixStack());
        }
    }

    @Override
    public void disable() {
        super.disable();
        ptCounter = 0;
        futureAnchorLocations.clear();
        running = false;
    }

    @EventHandler
    void preInstamine(InstamineEvent.Pre ignored) {
        if(prePlace.isEnabled() && (placeOnInstamine.is("before") || (placeOnInstamine.is("smart") && targetPos != null
                && targetPos.y < ((InstantBreak) Managers.MODULE.getItemByClass(InstantBreak.class)).pos.getY()
        ))) ThreadManager.cachedPool.submit(this::run$instamine);
    }

    @EventHandler
    void postInstamine(InstamineEvent.Post ignored) {
        if(prePlace.isEnabled() && (placeOnInstamine.is("after") || (placeOnInstamine.is("smart") && targetPos != null
                && targetPos.y >= ((InstantBreak) Managers.MODULE.getItemByClass(InstantBreak.class)).pos.getY()
        ))) ThreadManager.cachedPool.submit(this::run$instamine);
    }

    Vec3d targetPos = null;
    record AnchorData(Vec3d pos, Double distanceToTarget, Float damageToTarget, Float damageToSelf, boolean placeAnchor, boolean chargeAnchor, Vec3d targetPos) { }
}

