package xyz.qweru.pulse.client.systems.modules.impl.world;

//import baritone.api.BaritoneAPI;
//import baritone.api.pathing.goals.Goal;
//import baritone.api.pathing.goals.GoalNear;
//import baritone.api.pathing.goals.GoalRunAway;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.impl.combat.AutoCrystal;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

@ExcludeModule
public class AutoPVP extends ClientModule {

    /*
     * todo: keep in combat, pearl, phase, hole goal
     */

    NumberSetting tAirTicks = numberSetting()
            .name("Air ticks")
            .description("How many ticks can the target spend in air until an attempt is made to find a better target (-1 means infinity)")
            .range(-1, 200)
            .defaultValue(30)
            .build();

    // if the target flies / pearls 15 blocks in one tick, try to find a better target
    NumberSetting tMoveChange = numberSetting()
            .name("(Stop) Move distance")
            .description("How far can the target go from the target position until an attempt is made to find a better target (-1 means infinity)")
            .range(-1, 128)
            .defaultValue(15)
            .build();

    NumberSetting tGoalChange = numberSetting()
            .name("(Goal) Move distance")
            .description("How far can the target go from you until the position goal position is changed")
            .range(0, 128)
            .defaultValue(15)
            .build();

    int runDist = 10;

    public AutoPVP() {
        builder()
                .name("Auto PVP")
                .description("Automatically move to and attack targets. WARNING: this module automatically toggles modules")
                .settings("Targets", tAirTicks, tMoveChange, tGoalChange)
                .category(Category.WORLD);
    }

//    @Override
//    public void enable() {
//        super.enable();
//        if(Util.nullCheck()) return;
//        selectTarget();
//        setGoal();
//        if(currentGoal == null) {
//            ChatUtil.info("Current goal is null!");
//        }
//        else ChatUtil.info("Goal set to %s, estimated time: %s".formatted(currentGoal.toString(), currentGoal.heuristic()));
//    }

    boolean locked = false;
    @EventHandler
    void tick(WorldTickEvent.Post ignored) {
        if(locked) return;
        ThreadManager.cachedPool.submit(this::mainLoop);
    }

    void mainLoop() {
        locked = true;

        if(shouldRun() && target != null) {
//            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalRunAway(runDist, target.getBlockPos()));
        }
        else if(!checkTarget()) {
            clearTarget();
            selectTarget();
        }
//        if(currentGoal.isInGoal(mc.player.getBlockPos())) currentGoal = null;

        toggleModulesIfNecessary();

        locked = false;
    }

    LivingEntity target = null;
    int airTicks = 0;
//    Goal currentGoal = null;

    void selectTarget() {
        List<Entity> l = new ArrayList<>();
        mc.world.getEntities().forEach(l::add);

        if(l.isEmpty()) return;

        l.sort(Comparator.comparingDouble(entity -> -entity.distanceTo(mc.player)));

        for (Entity entity : l) {
            if(entity instanceof PlayerEntity pe && !PulseClient.friendSystem.isPlayerInSystem(pe) && pe != mc.player) {
                target = pe;
                break;
            }
        }
    }

    void clearTarget() {
        target = null;
        airTicks = 0;
    }

    void setGoal() {
        if(target == null) return;
//        currentGoal = new GoalNear(target.getBlockPos(), 4);
//        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(currentGoal);
    }

    boolean checkTarget() {
        if(target == null) return false;
        double dist = target.distanceTo(mc.player);
        if(target.isOnGround()) airTicks = 0;
        else airTicks++;

        /*if(dist >= tMoveChange.getValue()) return false;
        else*/ if(tAirTicks.getValue() != -1 && airTicks >= tAirTicks.getValue()) return false;
//        if((dist >= tGoalChange.getValue() || currentGoal == null) && !shouldRun()) setGoal();
        return true;
    }

    boolean shouldRun() {
        int totems = InventoryUtils.totalItemCount(Items.TOTEM_OF_UNDYING);
        double hp = mc.player.getHealth();

        return totems <= 1 || hp < 2;
    }

    void toggleModulesIfNecessary() {
//        if(currentGoal == null) return;
//        if(currentGoal.isInGoal(mc.player.getBlockPos())) {
            // todo: detect when using crystalaura is impossible and switch to anchors / swordpvp
//            Managers.MODULE.getItemByClass(AutoCrystal.class).setEnabled(true);
//        }
    }
}
