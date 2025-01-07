package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.systems.events.KeyEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AirJump extends ClientModule {

    BooleanSetting groundStrict = booleanSetting()
            .name("Ground Strict")
            .description("Ground movement correction")
            .build();

    ModeSetting mode = modeSetting()
            .name("Mode")
            .description("AirJump mode (vulcan and vulcan strict limit air jumps, vulcan strict basically doesn't flag)")
            .defaultMode("Vanilla")
            .mode("Vulcan")
            .mode("VulcanStrict")
            .mode("Vanilla")
            .build();

    public AirJump() {
        builder(this)
                .name("AirJump")
                .description("Jump on air")
                .bind(InputUtil.KEY_UNKNOWN)
                .settings(groundStrict, mode)
                .category(Category.MOVEMENT);

        mode.addOnToggle(() -> {
            if(mode.is("vanilla")) maxJumps = -1;
            else if(mode.is("vulcan")) maxJumps = 3;
            else if(mode.is("vulcanStrict")) maxJumps = 1;
        });
    }

    @Override
    public void enable() {
        super.enable();
        jumps = 0;
    }

    int jumps = 0;
    int maxJumps = -1;
    boolean jump = true;
    @EventHandler
    void onKey(KeyEvent event) {
        if(Util.nullCheck(mc) || mc.currentScreen != null || (groundStrict.isEnabled() && mc.player.isOnGround())) return;
        if(maxJumps != -1 && jumps > maxJumps) return;
        if(event.getKey() == InputUtil.KEY_SPACE && jump) {
            mc.player.jump();
            jumps++;
            jump = false;
        } else if(event.getAction() == GLFW.GLFW_RELEASE && event.getKey() == InputUtil.KEY_SPACE) jump = true;
    }

    @EventHandler
    void onTick(WorldTickEvent.Post e) {
        if(mc.player.isOnGround()) jumps = 0;
    }
}
