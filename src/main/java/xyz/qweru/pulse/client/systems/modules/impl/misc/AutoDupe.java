package xyz.qweru.pulse.client.systems.modules.impl.misc;

import meteordevelopment.orbit.EventHandler;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.NumberSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class AutoDupe extends ClientModule {

    private final ModeSetting mode = new ModeSetting("Execution mode", "How to execute dupes", true, "sequential", "sequential", "at once");
    public final BooleanSetting hideDupeMessages = new BooleanSetting("Hide dupe messages", "Hide dupe messages", true, true);
    
    private final BooleanSetting dupeObby = new BooleanSetting("Dupe obsidian", "should dupe obsidian", true, true);
    private final BooleanSetting dupeCrystals = new BooleanSetting("Dupe crystals", "should dupe crystals", true, true);
    private final BooleanSetting dupeAnchors = new BooleanSetting("Dupe anchors", "should dupe anchors", true, true);
    private final BooleanSetting dupeGlowstone = new BooleanSetting("Dupe glowstone", "should dupe glowstone", true, true);
    private final BooleanSetting dupeExp = new BooleanSetting("Dupe exp bottles", "should dupe exp bottles", true, true);
    private final BooleanSetting dupeGapples = new BooleanSetting("Dupe egaps", "should dupe egaps", true, true);
    private final BooleanSetting dupePearls = new BooleanSetting("Dupe pearls", "should dupe ender pearls", true, true);
    private final BooleanSetting dupeHand = new BooleanSetting("Dupe hand", "should dupe item in hand", false, true);
    public final NumberSetting ticksDelay = new NumberSetting("Delay", "Delay in ticks between dupes", 0f, 600f, 30f, true);
//    private final NumberSetting dupesPerTick = new NumberSetting("Dupes per tick", "How many dupe commands should be run every tick", 1f, 35f, 6);
    private final NumberSetting dupeItemCount = new NumberSetting("Dupe item count", "How many times should dupe", 1f, 64f, 16f, true);
    public static final NumberSetting extraTickSafety = new NumberSettingBuilder()
            .name("Tick safety")
            .description("How many ticks should be used for command limit checks")
            .shouldShow(true)
            .min(0f)
            .defaultValue(4f)
            .max(5f)
            .build();

    // at once
    private int DELAY = 0;
    private int dupeI = 0;
    private boolean dupesFinish = false;

    // sequential
    private int sequenceDelay = 0;
    public static final List<Runnable> runnableList = new ArrayList<>();
    public int ticksSinceLastDupe = 0;
    public boolean duping = false;

    public AutoDupe() {
        super("AutoDupe", "(for DupeAnarchy) automatically dupes items", -1, Category.MISC);
        builder(this)
                .settings(mode, dupeItemCount, hideDupeMessages)
                .settings("Items", dupeObby, dupeCrystals, dupeAnchors, dupeGlowstone, dupeExp, dupeGapples, dupePearls, dupeHand)
                .settings("Ticks", ticksDelay, extraTickSafety);
    }

    @Override
    public void disable() {
        super.disable();
        duping = false;
        ticksSinceLastDupe = 0;
        DELAY = 0;
        dupeI = 0;
        dupesFinish = false;
        runnableList.clear();
    }

    private void dupe(String s) {
        Util.delay(() -> {
            duping = true;
            ChatUtil.sendServerMsg("/dupe "+ ((int) dupeItemCount.getValue()) + " " + s);
            duping = false;
        }, 100L *dupeI);
        dupeI++;
    }

    private void dupeSeq(String s) {
        duping = true;
        ChatUtil.sendServerMsg("/dupe "+ ((int) dupeItemCount.getValue()) + " " + s);
        duping = false;
    }


    //    @Override
//    public void enable() {
//        super.enable();
//        ChatUtil.sendLocalMsg("Duping!");
//        if(dupeObby.getState()) dupe("obsidian");
//        if(dupeCrystals.getState()) dupe("end_crystal");
//        if(dupeAnchors.getState()) dupe("respawn_anchor");
//        if(dupeGlowstone.getState()) dupe("glowstone");
//        if(dupeExp.getState()) dupe("experience_bottles");
//        if(dupeGapples.getState()) dupe("enchanted_golden_apple");
//        if(dupeHand.getState()) dupe("");
//        this.toggle();
//    }


    private void atOnce() {
        ticksSinceLastDupe++;
        if(dupesFinish) {
            dupesFinish = false;

            if(dupeExp.isEnabled()) dupe("experience_bottle");
            if(dupeGapples.isEnabled()) dupe("enchanted_golden_apple");
            if(dupePearls.isEnabled()) dupe("ender_pearl");
            if(dupeHand.isEnabled()) dupe("");

            dupeI = 0;
            ticksSinceLastDupe = 0;
            return;
        }
        if(DELAY >= Math.floor(ticksDelay.getValue())) {
            DELAY = 0;

            if(dupeObby.isEnabled()) dupe("obsidian");
            if(dupeCrystals.isEnabled()) dupe("end_crystal");
            if(dupeAnchors.isEnabled()) dupe("respawn_anchor");
            if(dupeGlowstone.isEnabled()) dupe("glowstone");

            dupeI = 0;
            dupesFinish = true;
        }
        DELAY++;
    }

    private void sequential() {
        DELAY = 0; // reset mode "at once", if previous
        ticksSinceLastDupe++;

        if(sequenceDelay >= Math.floor(ticksDelay.getValue())) {
            sequenceDelay = 0;
            if(runnableList.isEmpty()) {
//                boolean addTotems = Modules.INSTANCE.getItemByClass(OffhandTotemDupe.class).isEnabled() && OffhandTotemDupe.autoDupeIntegration.getState();
//                boolean addBefore = addTotems && OffhandTotemDupe.beforeOrAfter.getCurrent().equalsIgnoreCase("Before");

//                if(addTotems && addBefore) runnableList.add(() -> ((OffhandTotemDupe) Modules.INSTANCE.getItemByClass(OffhandTotemDupe.class)).run());
                if(dupeObby.isEnabled()) runnableList.add(() -> dupeSeq("obsidian"));
                if(dupeCrystals.isEnabled()) runnableList.add(() -> dupeSeq("end_crystal"));
                if(dupeAnchors.isEnabled()) runnableList.add(() -> dupeSeq("respawn_anchor"));
                if(dupeGlowstone.isEnabled()) runnableList.add(() -> dupeSeq("glowstone"));
                if(dupeExp.isEnabled()) runnableList.add(() -> dupeSeq("experience_bottle"));
                if(dupeGapples.isEnabled()) runnableList.add(() -> dupeSeq("enchanted_golden_apple"));
                if(dupeGapples.isEnabled()) runnableList.add(() -> dupeSeq("ender_pearl"));
                if(dupeHand.isEnabled()) runnableList.add(() -> dupeSeq(""));
//                if(addTotems && !addBefore) runnableList.add(() -> ((OffhandTotemDupe) Modules.INSTANCE.getItemByClass(OffhandTotemDupe.class)).run());

            } else {
                runnableList.remove(0).run();
                ticksSinceLastDupe = 0;
            }
        }

        sequenceDelay++;
    }

    @EventHandler
    public void onTick(WorldTickEvent.Pre tickEvent) {
//        PulseClient.LOGGER.info("t");
        switch (mode.getCurrent()){
            case "sequential" -> sequential();
            case "at once" -> atOnce();
        }

    }

}
