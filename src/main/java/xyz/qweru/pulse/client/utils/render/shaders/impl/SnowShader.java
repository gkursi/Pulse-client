package xyz.qweru.pulse.client.utils.render.shaders.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.managed.ManagedCoreShader;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.ladysnake.satin.api.managed.uniform.Uniform1i;
import org.ladysnake.satin.api.managed.uniform.Uniform2f;
import org.ladysnake.satin.api.managed.uniform.Uniform4f;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class SnowShader {

    private Uniform2f resolution;
    private Uniform1f time;
    private Uniform1i quality;
    private Uniform4f color;

    public static final ManagedCoreShader RECTANGLE_SHADER = ShaderEffectManager.getInstance()
            .manageCoreShader(Identifier.of("pulse", "snow"), VertexFormats.POSITION);

    public SnowShader() {
        setup();
    }

    public void setParameters(float resX, float resY, float time) {
        float i = (float) mc.getWindow().getScaleFactor();
        this.time.set(time);
        resolution.set(resX, resY);
        quality.set(1);
        Color c = ThemeInfo.COLORSCHEME.ACCENT();
        color.set((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, (float) c.getAlpha() / 255);
    }

    public void use() {
        RenderSystem.setShader(RECTANGLE_SHADER::getProgram);
    }

    protected void setup() {
        resolution = RECTANGLE_SHADER.findUniform2f("resolution");
        time = RECTANGLE_SHADER.findUniform1f("time");
        color = RECTANGLE_SHADER.findUniform4f("color");
        quality = RECTANGLE_SHADER.findUniform1i("quality");
    }
}
