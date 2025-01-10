package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerInteractEntityC2SPacket;
import xyz.qweru.pulse.client.systems.events.KeyEvent;
import xyz.qweru.pulse.client.systems.events.PostSendPacketEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AirJump extends ClientModule {

    BooleanSetting groundStrict = booleanSetting()
            .name("Ground Strict")
            .description("Ground movement correction")
            .build();

    NumberSetting resetTimerSetting = numberSetting()
            .info("Reset timer", "time to wait for reset")
            .range(0, 10000)
            .defaultValue(3000)
            .stepFullNumbers()
            .build();

    BooleanSetting limitJumps = booleanSetting()
            .name("Jump limit")
            .description("Limit the amount of jumps per reset")
            .build();

    NumberSetting jumpCount = numberSetting()
            .info("Jump count", "jump count")
            .range(0, 10)
            .defaultValue(2)
            .stepFullNumbers()
            .build();

    ModeSetting resetMode = modeSetting()
            .name("Reset mode")
            .description("(for Jump Limit) When should the jump count be reset (On Ground - reset when on ground, timer - reset when the timer ends)")
            .defaultMode("On Ground")
            .mode("Timer")
            .mode("On Ground")
            .build();

    public AirJump() {
        builder(this)
                .name("AirJump")
                .description("Jump on air")
                .bind(InputUtil.KEY_UNKNOWN)
                .settings(groundStrict, limitJumps, jumpCount, resetMode, resetTimerSetting)
                .category(Category.MOVEMENT);

        jumpCount.addOnToggle(() -> maxJumps = jumpCount.getValueInt());
    }

    @Override
    public void enable() {
        super.enable();
        jumps = 0;
    }

    int jumps = 0;
    int maxJumps = 2;
    boolean jump = true;
    @EventHandler
    void onKey(KeyEvent event) {
        if(Util.nullCheck(mc) || mc.currentScreen != null) return;
        if(limitJumps.isEnabled() && maxJumps != -1 && jumps > maxJumps) return;
        if(event.getKey() == InputUtil.KEY_SPACE && jump) {
            if(!(groundStrict.isEnabled() && mc.player.isOnGround())) mc.player.jump();
            jumps++;
            jump = false;
        } else if(event.getAction() == GLFW.GLFW_RELEASE && event.getKey() == InputUtil.KEY_SPACE) jump = true;
    }

    TimerUtil resetTimer = new TimerUtil();
    @EventHandler
    void onTick(WorldTickEvent.Post e) {
        if(resetMode.is("on ground") && mc.player.isOnGround()) jumps = 0;
        else if(resetTimer.hasReached(resetTimerSetting.getValue())) {
            jumps = 0;
            resetTimer.reset();
        }
    }


}
