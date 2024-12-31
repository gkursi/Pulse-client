package xyz.qweru.pulse.client.utils.render.shaders.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.managed.ManagedCoreShader;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.ladysnake.satin.api.managed.uniform.Uniform2f;
import org.ladysnake.satin.api.managed.uniform.Uniform4f;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.impl.setting.HudEditor;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class HudShader {
    private Uniform2f uSize;
    private Uniform2f uLocation;
    private Uniform1f radius;
    private Uniform1f blend;
    private Uniform1f alpha;
    private Uniform1f outline;
    private Uniform1f glow;
    private Uniform4f color1;
    private Uniform4f color2;
    private Uniform4f color3;
    private Uniform4f color4;

    public static final ManagedCoreShader HUD_SHADER = ShaderEffectManager.getInstance()
            .manageCoreShader(Identifier.of("pulse", "hud"), VertexFormats.POSITION);

    public HudShader() {
        setup();
    }

    public void setParameters(float x, float y, float width, float height, float r, float externalAlpha, float internalAlpha) {
        if(HudEditor.color.is("None")) return;
        float i = (float) mc.getWindow().getScaleFactor();
        radius.set(r * i);
        uLocation.set(x * i, -y * i + mc.getWindow().getScaledHeight() * i - height * i);
        uSize.set(width * i, height * i);

        Color baseC = HudEditor.color.is("Primary") ? ThemeInfo.COLORSCHEME.PRIMARY() : ThemeInfo.COLORSCHEME.ACCENT();

        Color c1 = Pulse2D.darker(baseC, 0.9f);
        Color c2 = Pulse2D.darker(baseC, 0.8f);
        Color c3 = Pulse2D.darker(baseC, 0.8f);
        Color c4 = Pulse2D.darker(baseC, 0.9f);

        if(HudEditor.color.is("Rainbow")) {
            double yOffset = y * 50;

            c1 = Pulse2D.rainbow(0d + yOffset, 0.45f, 0.9f, 0.5f);
            c2 = Pulse2D.rainbow(3000d + yOffset, 0.45f, 0.9f, 0.5f);
            c3 = Pulse2D.rainbow(2000d + yOffset, 0.45f, 0.9f, 0.5f);
            c4 = Pulse2D.rainbow(4000d + yOffset, 0.45f, 0.9f, 0.5f);
        }

        color1.set(c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f, externalAlpha);
        color2.set(c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, externalAlpha);
        color3.set(c3.getRed() / 255f, c3.getGreen() / 255f, c3.getBlue() / 255f, externalAlpha);
        color4.set(c4.getRed() / 255f, c4.getGreen() / 255f, c4.getBlue() / 255f, externalAlpha);
        blend.set(10f);
        outline.set(HudEditor.outline.getValue());
        glow.set(HudEditor.glow.getValue());
        alpha.set(internalAlpha);
    }

    public void use() {
        RenderSystem.setShader(HUD_SHADER::getProgram);
    }

    public void setup() {
        uSize = HUD_SHADER.findUniform2f("uSize");
        uLocation = HUD_SHADER.findUniform2f("uLocation");
        radius = HUD_SHADER.findUniform1f("radius");
        blend = HUD_SHADER.findUniform1f("blend");
        alpha = HUD_SHADER.findUniform1f("alpha");
        color1 = HUD_SHADER.findUniform4f("color1");
        color2 = HUD_SHADER.findUniform4f("color2");
        color3 = HUD_SHADER.findUniform4f("color3");
        color4 = HUD_SHADER.findUniform4f("color4");
        outline = HUD_SHADER.findUniform1f("outline");
        glow = HUD_SHADER.findUniform1f("glow");
    }
}
