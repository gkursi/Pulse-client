package xyz.qweru.pulse.client.utils.render.shaders;

import me.x150.renderer.render.OutlineFramebuffer;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;

import java.awt.*;

public class ShaderManager {
    public void renderShader(Runnable task, Shader shader) {
        switch (shader) {
            case OUTLINE -> {
                OutlineFramebuffer.use(task);
                OutlineFramebuffer.draw(4, ThemeInfo.COLORSCHEME.getBorderColor(), Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.PRIMARY(), 50));
            }
        }
    }

}
