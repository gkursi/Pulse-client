package xyz.qweru.pulse.client.systems.modules.impl.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.impl.setting.Random;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.RotationUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import java.util.Comparator;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Killaura extends ClientModule {
    NumberSetting range = numberSetting()
            .name("Range")
            .description("Target range")
            .defaultValue(4)
            .range(0, 7)
            .build();

    ModeSetting sort = modeSetting()
            .name("Sort")
            .description("How to sort entities")
            .defaultMode("Health")
            .mode("Distance")
            .mode("Health")
            .build();

    BooleanSetting players = booleanSetting()
            .name("Players")
            .description("Attack players")
            .build();

    BooleanSetting passive = booleanSetting()
            .name("Passive")
            .description("Attack passive entities")
            .build();

    BooleanSetting hostile = booleanSetting()
            .name("Hostile")
            .description("Attack hostile entities")
            .build();

    BooleanSetting rotate = booleanSetting()
            .name("Rotate")
            .description("Should rotate")
            .build();

    BooleanSetting swing = booleanSetting()
            .name("Swing")
            .description("Swing hand")
            .build();

    BooleanSetting swingPacket = booleanSetting()
            .name("Swing packet")
            .description("Swing hand")
            .build();

    BooleanSetting randomize = booleanSetting()
            .name("Random hit delay")
            .description("Random hit delay")
            .build();

    BooleanSetting noDelay = booleanSetting()
            .name("1.8")
            .description("Disable base minecraft delay")
            .build();

    BooleanSetting lookOnly = booleanSetting()
            .name("Only on look")
            .description("Only attack while looking at entity")
            .build();

    public Killaura() {
        builder(this)
                .name("Killaura")
                .description("Automatically hit entities")
                .settings("Targets", players, passive, hostile)
                .settings("Attack", range, sort, rotate, randomize, noDelay, lookOnly)
                .settings("Swing", swing, swingPacket)
                .category(Category.COMBAT);

        rotate.addOnToggle(() -> deferEnableToRotationUnlock(rotate.isEnabled()));
    }

    boolean bl = false;
    TimerUtil randomTimer = new TimerUtil();
    float nextRandom = 0f;
    float randNum = 200f;
    @EventHandler
    void tick(WorldTickEvent.Pre ignored) {
        if (Util.nullCheck()) return;
        LivingEntity target = findTarget();
        if(target == null) return;

        float hitPercentage = Random.hitPercentage.getValue() / 100;

        if (mc.player.getAttackCooldownProgress(0.5f) == 1 || noDelay.isEnabled()) {
            if(randomize.isEnabled()) {
                java.util.Random jRandom = new java.util.Random();
                if(!bl) {
                    bl = true;
                    randomTimer.reset();
                    nextRandom = (float) Random.getRandom(randNum, Random.RandomizerMode.UP_AND_DOWN, true);
                    randNum = jRandom.nextFloat(0, 200);
                } else if(randomTimer.hasReached(nextRandom)) {
                    if(jRandom.nextFloat(0f, 1f) > hitPercentage) {
                        miss();
                        return;
                    }
                    attack(target);
                    bl = false;
                }
            } else {
                attack(target);
            }
        }
    }

    LivingEntity findTarget() {
        if(lookOnly.isEnabled()) {
            if(mc.crosshairTarget instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity e) {
                return e;
            } else return null;
        } else {
            List<LivingEntity> targets = nearestTarget();
            if (targets.isEmpty()) return null;
            return targets.get(0);
        }
    }

    void attack(LivingEntity target) {
        if (rotate.isEnabled()) {
            if(!isEnabled()) return;
            Vector3d s = new Vector3d();
            RenderUtil.set(s, target, mc.getRenderTickCounter().getTickDelta(true));

            RotationUtil.addRotation(new Vec3d(s.x, s.y + target.getHeight() / 2, s.z), () -> {
                mc.interactionManager.attackEntity(mc.player, target);
                if(swing.isEnabled()) mc.player.swingHand(Hand.MAIN_HAND);
                if(swingPacket.isEnabled()) PacketUtil.sendImmediately(new HandSwingC2SPacket(Hand.MAIN_HAND));
            });
        }
        else {
            mc.interactionManager.attackEntity(mc.player, target);
            if(swing.isEnabled()) mc.player.swingHand(Hand.MAIN_HAND);
            if(swingPacket.isEnabled()) PacketUtil.sendImmediately(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }
    }

    void miss() {
        if(swing.isEnabled()) mc.player.swingHand(Hand.MAIN_HAND);
        if(swingPacket.isEnabled()) PacketUtil.sendImmediately(new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    private List<LivingEntity> nearestTarget() {
        if (Util.nullCheck(mc)) return null;
        Comparator<LivingEntity> comparator = sort.is("Distance") ? Comparator.comparingDouble(entity -> entity.distanceTo(mc.player)) : Comparator.comparingDouble(LivingEntity::getHealth);
        return mc.world.getEntitiesByClass(LivingEntity.class, mc.player.getBoundingBox().expand(range.getValue()), this::isValid)
                .stream().sorted(comparator).toList();

    }
    private boolean isValid (Entity entity){
        if (entity == mc.player) return false;
        if (!entity.isAlive()) return false;
        else if (entity instanceof PlayerEntity pe && (!players.isEnabled() || PulseClient.friendSystem.isPlayerInSystem(pe.getGameProfile().getName())))
            return false;
        else if (entity instanceof PassiveEntity && !passive.isEnabled()) return false;
        else if (entity instanceof HostileEntity && !hostile.isEnabled()) return false;
        else return entity instanceof LivingEntity;
    }

}
