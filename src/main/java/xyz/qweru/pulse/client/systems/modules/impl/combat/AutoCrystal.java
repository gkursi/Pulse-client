package xyz.qweru.pulse.client.systems.modules.impl.combat;

import me.x150.renderer.render.Renderer3d;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerInteractEntityC2SPacket;
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
import xyz.qweru.pulse.client.utils.entity.EntityFinder;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.player.RotationUtil;
import xyz.qweru.pulse.client.utils.player.Rotations;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;
import xyz.qweru.pulse.client.utils.world.PosUtil;

import java.util.*;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AutoCrystal extends ClientModule {

    NumberSetting minSelfHealth = numberSetting()
            .name("Min health")
            .description("Minimum amount of health required for the module to run")
            .range(0, 36)
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

    BooleanSetting extrapolateSelf = booleanSetting()
            .name("Extrapolate self pos")
            .description("Predicts future positions of self based on velocity")
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
            .name("Crystal scan range")
            .description("How far around the target should crystal placements be scanned for")
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
            .range(0, 36)
            .defaultValue(8.9f)
            .build();

    NumberSetting minTargetDamage = numberSetting()
            .name("Min target damage")
            .description("Min amount of damage that can be delat to target")
            .range(0, 36)
            .defaultValue(5)
            .build();

    BooleanSetting silentSwitch = booleanSetting()
            .name("Silent switch")
            .description("Switches to end crystals using packets")
            .build();

    NumberSetting placeDelay = numberSetting()
            .name("Place delay ms")
            .description("Crystal place delay in milliseconds")
            .range(0, 3000)
            .defaultValue(100)
            .stepFullNumbers()
            .build();

    NumberSetting breakDelay = numberSetting()
            .name("Break delay ms")
            .description("Crystal Break delay in milliseconds")
            .range(0, 3000)
            .defaultValue(100)
            .stepFullNumbers()
            .build();

    BooleanSetting render = booleanSetting()
            .name("Render")
            .description("Adds visuals to the module")
            .defaultValue(true)
            .build();

    ModeSetting renderMode = modeSetting()
            .name("Render mode")
            .description("How to render")
            .defaultMode("Support block")
            .mode("Crystal block")
            .mode("Support block")
            .build();

    ColorSetting renderColor = new ColorSettingBuilder()
            .setName("Render color")
            .setDescription("Color for rendering")
            .build();

    ModeSetting placeMode = modeSetting()
            .name("Interact mode")
            .description("How should crystals be placed / broken")
            .defaultMode("Client")
            .mode("Packet")
            .mode("Client")
            .build();

    NumberSetting placesPerTick = numberSetting()
            .name("Crystals per tick")
            .description("crystals per tick")
            .range(0, 10)
            .defaultValue(1)
            .stepFullNumbers()
            .build();

    BooleanSetting swing = booleanSetting()
            .name("Swing")
            .description("Swing hand on place")
            .defaultValue(true)
            .build();

    BooleanSetting pauseOnUse = booleanSetting()
            .name("Pause on use")
            .description("Pause module when using an item or while box breaker is running")
            .defaultValue(true)
            .build();

    BooleanSetting onlyVisible = booleanSetting()
            .name("Only visible")
            .description("Only attack visible crystals")
            .defaultValue(true)
            .build();

    BooleanSetting rotate = booleanSetting()
            .name("Rotate")
            .description("Rotate towards crystal")
            .defaultValue(true)
            .build();

    BooleanSetting predictID = booleanSetting()
            .name("Predict ID")
            .description("Predict the crystal entity id instead of waiting for the server to send one, good for high ping")
            .defaultValue(true)
            .build();

    BooleanSetting prePlace = booleanSetting()
            .name("Use preplace")
            .description("Assume instamine block is air, enable below check")
            .defaultValue(true)
            .build();

    ModeSetting placeOnInstamine = modeSetting()
            .name("Place on IM")
            .description("when to place on instamine: before - before instamine, after - after instamine, smart - before if pos is above target and after otherwise, dont - only assume instamine is air and nothing else")
            .defaultMode("smart")
            .mode("before")
            .mode("after")
            .mode("dont")
            .mode("smart")
            .build();

    BooleanSetting oncePerTick = booleanSetting()
            .name("Once per tick")
            .description("Don't place on instamine if already placed during that tick")
            .defaultValue(true)
            .build();

    // todo ^^

    NumberSetting predictPackets = numberSetting()
            .name("Predict packets")
            .description("how many prediction packets should be sent")
            .range(0, 10)
            .defaultValue(1)
            .stepFullNumbers()
            .build();

    NumberSetting predictIncrement = numberSetting()
            .name("Predict increment")
            .description("id increment for each packet")
            .range(0, 10)
            .defaultValue(1)
            .stepFullNumbers()
            .build();

    int dupeTicks = 0;
    BooleanSetting redupe = booleanSetting()
            .name("Redupe")
            .description("Automatically dupe items on play.dupeanarchy.com")
            .build();

    BooleanSetting pauseOnSurrond = booleanSetting()
            .name("Pause on surround")
            .description("Pause module when surround is placing")
            .defaultValue(true)
            .build();

    public AutoCrystal() {
        builder(this)
                .name("Auto Crystal")
                .description("Automatically blow up end crystals on other people")
                .settings("Range", crystalScanRange, interactionRange, targetRange)
                .settings("HP", minSelfHealth, maxSelfDamage, minTargetDamage)
                .settings("Delay", placeDelay, breakDelay)
                .settings("Place", /*placeSort, todo implement scrolling and add back*/ placeMode, placesPerTick)
//                .settings("Look", rotate, onlyVisible) todo << this too
                .settings("Misc", performanceMode, performanceModeTicks, silentSwitch, pauseOnUse, pauseOnSurrond, redupe, rotate)
                .settings("Render", render, renderMode, renderColor, swing)
                .settings("Extrapolation", extrapolate, extrapolateSelf, extrapolationTicks)
                .settings("ID prediction", predictID, predictPackets, predictIncrement)
                .settings("Instamine place", prePlace, placeOnInstamine, oncePerTick)
                .category(Category.COMBAT);
    }

    int ptCounter = 0;
    boolean usingCrystals = false;
    boolean placed = false;
    @EventHandler
    private void tick(WorldTickEvent.Post e) {
        placed = false;
        if(mc.player.getHealth() < minSelfHealth.getValue()) {
            return;
        }
        if(performanceMode.isEnabled()) {
            if(ptCounter == 0) {
                calcPlacements();
                ptCounter++;
            } else if(ptCounter >= performanceModeTicks.getValue()) {
                ptCounter = 0;
            }
        } else {
            calcPlacements();
        }
        ThreadManager.cachedPool.submit(this::run);
    }

    boolean canSeePos(Vec3d pos) {
        Vec3d vec3d = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
        if (pos.distanceTo(vec3d) > 128.0) {
            return false;
        } else {
            return mc.player.getWorld().raycast(new RaycastContext(vec3d, pos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS;
        }
    }

    List<CompletableEndCrystalData> crystalsToPlace = new ArrayList<>();
    void calcPlacements() {
        if(usingCrystals) return;
        crystalsToPlace.clear();
        for (Entity entity : mc.world.getEntities()) {
            if(!(entity instanceof LivingEntity) || (entity instanceof PlayerEntity pe && PulseClient.friendSystem.isPlayerInSystem(pe.getGameProfile().getName())) || !entity.isAlive() || !((LivingEntity) entity).canTakeDamage()) continue;
            if(entity.getDisplayName().equals(mc.player.getDisplayName())) continue;
            if(PosUtil.distanceBetween(mc.player.getPos(), entity.getPos()) > targetRange.getValue()) continue;
            List<CompletableEndCrystalData> possiblePlacements = new ArrayList<>();
            Vec3d targetPos = entity.getPos();
            Vec3d playerPos = mc.player.getPos();
            if(extrapolate.isEnabled()) targetPos = PosUtil.predictPos((LivingEntity) entity, ((int) Math.floor(extrapolationTicks.getValue())));
            if(extrapolateSelf.isEnabled()) playerPos = PosUtil.predictPos(mc.player, ((int) Math.floor(extrapolationTicks.getValue())));
            Vec3d finalPlayerPos = playerPos;
            BlockUtil.forBlocksInRange((x, y, z, bp) -> {
                if(bp.equals(BlockPos.ofFloored(mc.player.getPos())) || bp.equals(BlockPos.ofFloored(entity.getPos()))) return; // todo fix collision checking
//                todo uncommment once scrolling is implemented if(onlyVisible.isEnabled() && !canSeePos(bp.add(0, 1, 0).toBottomCenterPos())) return;
                if(PosUtil.distanceBetween(mc.player.getPos(), bp.toCenterPos()) <= interactionRange.getValue() && canPlaceCrystal(bp.toCenterPos()) && PosUtil.distanceBetween(entity.getPos(), bp.toCenterPos()) >= 1) {
                    boolean spoofInstamineBlock = InstantBreak.isBreaking() && prePlace.isEnabled();
                    BlockPos spoofPos = ((InstantBreak) Managers.MODULE.getItemByClass(InstantBreak.class)).pos;
                    BlockState prev = null;
                    if(spoofInstamineBlock) {
                        prev = mc.world.getBlockState(spoofPos);
                        mc.world.setBlockState(spoofPos, Blocks.AIR.getDefaultState());
                    }
                    float damage = DamageUtils.crystalDamage((LivingEntity) entity, bp.add(0, 1, 0).toCenterPos());
                    Vec3d pos = mc.player.getPos();
                    if(extrapolateSelf.isEnabled()) mc.player.setPos(finalPlayerPos.getX(), finalPlayerPos.getY(), finalPlayerPos.getZ());
                    float selfDamage = DamageUtils.crystalDamage(mc.player, bp.add(0, 1, 0).toCenterPos());
                    if(extrapolateSelf.isEnabled()) mc.player.setPos(pos.getX(), pos.getY(), pos.getZ());
                    double distance = PosUtil.distanceBetween(bp.add(0, 1, 0).toBottomCenterPos(), entity.getPos());
                    possiblePlacements.add(new CompletableEndCrystalData(bp.toCenterPos(), distance, damage, selfDamage, entity.getPos()));
                    if(spoofInstamineBlock) {
                        mc.world.setBlockState(spoofPos, prev);
                    }
                }
            }, ((int) crystalScanRange.getValue()), targetPos);
            CompletableEndCrystalData bestPlacement = null;
            switch (placeSort.getCurrent()) {
                case "damage" -> bestPlacement = getBestDamage(possiblePlacements);
                case "distance" -> bestPlacement = getBestDistance(possiblePlacements);
            }
            if(bestPlacement != null) {
                crystalsToPlace.add(bestPlacement);
            }
        }
    }

    CompletableEndCrystalData getBestDamage(List<CompletableEndCrystalData> placements) {
        CompletableEndCrystalData bestCrystal = null;
        for (CompletableEndCrystalData placement : placements) {
            if(bestCrystal == null) {
                if(placement.damageToTarget > minTargetDamage.getValue() && placement.damageToSelf < maxSelfDamage.getValue()) bestCrystal = placement;
            } else
            if((placement.damageToTarget > bestCrystal.damageToTarget && placement.damageToSelf <= maxSelfDamage.getValue()) ||
                            (Math.floor(placement.damageToTarget) == Math.floor(bestCrystal.damageToTarget) && placement.damageToSelf <= maxSelfDamage.getValue()
                                    && placement.damageToSelf < bestCrystal.damageToSelf)
            ) {
                bestCrystal = placement;
            }
        }
        return bestCrystal;
    }

    CompletableEndCrystalData getBestDistance(List<CompletableEndCrystalData> placements) {
        CompletableEndCrystalData bestCrystal = null;
        for (CompletableEndCrystalData placement : placements) {
            if(bestCrystal == null) {
                bestCrystal = placement;
                continue;
            }
            if((placement.distanceToTarget < bestCrystal.distanceToTarget && placement.damageToSelf <= maxSelfDamage.getValue()) ||
                            (Math.floor(placement.distanceToTarget) == Math.floor(bestCrystal.distanceToTarget) && placement.damageToSelf <= maxSelfDamage.getValue()
                                    && placement.damageToSelf < bestCrystal.damageToSelf)) {
                bestCrystal = placement;
            }
        }
        return bestCrystal;
    }

    boolean canPlaceCrystal(Vec3d pos) {
        if(Managers.MODULE.getItemByClass(InstantBreak.class).isEnabled()) {
            InstantBreak b = (InstantBreak) Managers.MODULE.getItemByClass(InstantBreak.class);
            if(b.pos != null && b.pos.equals(BlockPos.ofFloored(pos))) {
                return true; // the block is probably broken
            }
        }
        if(!mc.world.canPlace(mc.world.getBlockState(BlockPos.ofFloored(pos)), BlockPos.ofFloored(pos), ShapeContext.absent()) && pos.getY() < 320 /* so crystals can be placed above height limit */) return false;
        return (BlockUtil.getBlockAt(BlockPos.ofFloored(pos)).equals(Blocks.OBSIDIAN) || BlockUtil.getBlockAt(BlockPos.ofFloored(pos)).equals(Blocks.BEDROCK))
                && (BlockUtil.getBlockAt(BlockPos.ofFloored(pos.add(0, 1, 0))).equals(Blocks.AIR)
                    /*|| (prePlace.isEnabled() && InstantBreak.isBreaking(BlockPos.ofFloored(pos.add(0, 1, 0))))*/);
    }

    void run() {
        if(usingCrystals) return;
        if((pauseOnUse.isEnabled() && Util.isUsingItem())) {
            Managers.SLOT.syncNow();
            return;
        }
        usingCrystals = true;
        if(!crystalsToPlace.isEmpty()) {
            for (CompletableEndCrystalData crystalData : crystalsToPlace) {
                placed = true;
                targetPos = crystalData.targetPos;
                for (int i = 0; i < placesPerTick.getValue(); i++) exec(crystalData);
                break; // only attack one entity
            }
            if (redupe.isEnabled()) {
                int count = InventoryUtils.totalItemCount(Items.END_CRYSTAL);
                if (count <= 32) {
                    if (dupeTicks > 0) dupeTicks--;
                    else {
                        if (count == 32) ChatUtil.sendServerMsg("/dupe 1 end_crystal");
                        else if (count >= 16) ChatUtil.sendServerMsg("/dupe 4 end_crystal");
                        else ChatUtil.sendServerMsg("/dupe 64 end_crystal");
                        dupeTicks = 10;
                    }
                }
            }
        }
        usingCrystals = false;
    }

    void run$instamine() {
        if(usingCrystals || placed) return;
        if((pauseOnUse.isEnabled() && Util.isUsingItem())) {
            Managers.SLOT.syncNow();
            return;
        }
        usingCrystals = true;
        if(!crystalsToPlace.isEmpty()) {
            for (CompletableEndCrystalData crystalData : crystalsToPlace) {
                for (int i = 0; i < placesPerTick.getValue(); i++) exec(crystalData);
            }
            if (redupe.isEnabled()) {
                int count = InventoryUtils.totalItemCount(Items.END_CRYSTAL);
                if (count <= 32) {
                    if (dupeTicks > 0) dupeTicks--;
                    else {
                        if (count == 32) ChatUtil.sendServerMsg("/dupe 1 end_crystal");
                        else if (count >= 16) ChatUtil.sendServerMsg("/dupe 4 end_crystal");
                        else ChatUtil.sendServerMsg("/dupe 64 end_crystal");
                        dupeTicks = 10;
                    }
                }
            }
        }
        usingCrystals = false;
    }

    Entity lastEntity = null;
    void exec(CompletableEndCrystalData crystalData) {
        if(pauseOnSurrond.isEnabled() && Surround.placing) return;
        if(rotate.isEnabled()) {
            Rotations.rotate(
                    Rotations.getYaw(crystalData.pos.add(0, 0.5, 0)),
                    Rotations.getPitch(crystalData.pos.add(0, 0.5, 0)),
                    100
            );
        }
        int slot = InventoryUtils.getItemSlotAll(Items.END_CRYSTAL);
        boolean offhand = mc.player.getOffHandStack().getItem().equals(Items.END_CRYSTAL);
        if(!offhand) {
            if(slot == -1) return;
            Managers.SLOT.selectOnServer(slot);
        }

        BlockHitResult hitResult = new BlockHitResult(crystalData.pos, Direction.UP, BlockPos.ofFloored(crystalData.pos), false);
        crystal(hitResult, offhand);
        if(!offhand) Managers.SLOT.syncNow();
    }

    void crystal(BlockHitResult hitResult, boolean offhand) {
        Util.sleep(((long) placeDelay.getValue()));
        if(swing.isEnabled() && !mc.player.handSwinging) mc.player.swingHand(Hand.MAIN_HAND);
        placeCrystal(hitResult, offhand);
        Util.sleep(((long) breakDelay.getValue()));
        List<Entity> entities = EntityFinder.findEntitiesInRange(((int) interactionRange.getValue()), mc.player.getPos()).filterClass(EndCrystalEntity.class).get();
        Entity entity = null;
        try {
            entity = entities.get(0);
        } catch (Exception ignored) {}
        if(entity != null && entity.isAlive() && entity != lastEntity) {
            breakCrystal(entity);
            lastEntity = entity;
        } else if(predictID.isEnabled()) {
            int id = getHighest() + predictIncrement.getValueInt();
            for (int i = 1; i < predictPackets.getValueInt(); i++) {
                breakCrystal(id);
                id += predictIncrement.getValueInt();
            }
        }
    }

    int getHighest() {
        Iterator<Entity> entityList = mc.world.getEntities().iterator();
        Entity last;
        int highest = confirmed;
        while (entityList.hasNext()) {
            last = entityList.next();
            if(last.getId() > highest) highest = last.getId();
        }
        if (highest > confirmed) confirmed = highest;
        return highest;
    }

    void placeCrystal(BlockHitResult result, boolean offhand) {
        switch (placeMode.getCurrent().toLowerCase()) {
            case "packet" -> {
                PendingUpdateManager pendingUpdateManager = ((IWorld) mc.world).pulse$getPendingUpdateManager().incrementSequence();
                try {
                    PacketUtil.sendImmediately(new PlayerInteractBlockC2SPacket(offhand ? Hand.OFF_HAND : Hand.MAIN_HAND, result, pendingUpdateManager.getSequence()));
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
            case "client" -> mc.interactionManager.interactBlock(mc.player, offhand ? Hand.OFF_HAND : Hand.MAIN_HAND, result);
        }
    }

    int confirmed = Integer.MIN_VALUE;

    void breakCrystal(Entity entity) {
        if(entity.getId() > confirmed) confirmed = entity.getId();
        switch (placeMode.getCurrent().toLowerCase()) {
            case "packet" -> PacketUtil.sendImmediately(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));
            case "client" -> mc.interactionManager.attackEntity(mc.player, entity);
        }
    }

    void breakCrystal(int entity) {
        if(entity > confirmed) confirmed = entity;
        PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.attack(mc.player, mc.player.isSneaking());
        ((IPlayerInteractEntityC2SPacket) packet).pulse$setID(entity);
        PacketUtil.sendImmediately(packet);
    }

    List<FadeOutBlock> fades = new ArrayList<>();
    @EventHandler
    private void render3D(Render3DEvent e) {
        if(!render.isEnabled()) return;
        Renderer3d.renderThroughWalls();

//        Vec3d end = RenderUtil.getPointInDirection(mc.player.getCameraPosVec(e.getTickCounter().getTickDelta(true)), RotationUtil.getServerPitch(), RotationUtil.getServerYaw(), 7);
//        Renderer3d.renderLine(e.getMatrixStack(), Color.blue, mc.player.getCameraPosVec(e.getTickCounter().getTickDelta(true)), end);

        for (CompletableEndCrystalData crystalData : crystalsToPlace) {
            if(renderMode.is("Support block")) {
                fades.add(new FadeOutBlock(BlockPos.ofFloored(crystalData.pos), renderColor.getJavaColor(), renderColor.getJavaColor().darker(), 450));
            } else if (renderMode.is("Crystal block")) {
                fades.add(new FadeOutBlock(BlockPos.ofFloored(crystalData.pos.add(0, 1, 0)), renderColor.getJavaColor(), renderColor.getJavaColor().darker(), 450));
            }
        }

        Iterator<FadeOutBlock> iterator = fades.iterator();
        while (iterator.hasNext()) {
            FadeOutBlock block = iterator.next();
            if(block.hasFaded()) iterator.remove();
            else block.render(e.getMatrixStack());
        }
    }

    @Override
    public void enable() {
        super.enable();
//        if(rotate.isEnabled()) RotationUtil.keep = true;
    }

    @Override
    public void disable() {
        super.disable();
        ptCounter = 0;
        crystalsToPlace.clear();
        usingCrystals = false;
        if(Util.nullCheck()) return;
        Managers.SLOT.syncNow();
//        if(rotate.isEnabled()) RotationUtil.keep = false;
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
    @Override
    public String getDisplayData() {
        return targetPos == null ? "" : targetPos.toString();
    }

    record EndCrystalData(EndCrystalEntity entity, Vec3d pos, Double distanceToTarget, Float damageToTarget, Float damageToSelf, Vec3d targetPos) {
        public CompletableEndCrystalData getCompletable() {
            return new CompletableEndCrystalData(pos, distanceToTarget, damageToTarget, damageToSelf, targetPos);
        }
    }
    record CompletableEndCrystalData(Vec3d pos, Double distanceToTarget, Float damageToTarget, Float damageToSelf, Vec3d targetPos) {
        public EndCrystalData complete(EndCrystalEntity entity) {
            return new EndCrystalData(entity, pos(), distanceToTarget(), damageToTarget(), damageToSelf(), targetPos);
        }
    }
}
