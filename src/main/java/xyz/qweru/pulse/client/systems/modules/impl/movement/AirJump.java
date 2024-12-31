package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.systems.events.KeyEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AirJump extends ClientModule {
    public AirJump() {
        builder(this)
                .name("AirJump")
                .description("Jump on air")
                .bind(InputUtil.KEY_UNKNOWN)
                .category(Category.MOVEMENT);
    }

    @Override
    public void enable() {
        super.enable();
    }

    boolean jump = true;
    @EventHandler
    public void onKey(KeyEvent event) {
        if(Util.nullCheck(mc) || mc.currentScreen != null) return;
        if(event.getKey() == InputUtil.KEY_SPACE && jump) {
            mc.player.jump();
            jump = false;
        } else if(event.getAction() == GLFW.GLFW_RELEASE && event.getKey() == InputUtil.KEY_SPACE) jump = true;
    }
}
