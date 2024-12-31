package xyz.qweru.pulse.client.render.renderer;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL40C;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ClickGUI;
import xyz.qweru.pulse.client.systems.modules.impl.setting.HudEditor;
import xyz.qweru.pulse.client.utils.render.ColorUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;
import xyz.qweru.pulse.client.utils.render.shaders.impl.*;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static xyz.qweru.pulse.client.PulseClient.mc;

/**
 * 2D gui lib skid from coffee client (glScissor, some random stuff) and thunderhack (rectangle, arc shaders, ShaderProgram impl w satin)
 */
public class Pulse2D {

//    public static ManagedShaderEffect BLUR_SHADER = ShaderEffectManager.getInstance().manage(Identifier.of("shaders/post/blur.json"));

    public static RectangleShader RECT_PROGRAM = new RectangleShader();
    public static ArcShader ARC_PROGRAM = new ArcShader();
    public static HudShader HUD_PROGRAM = new HudShader();
    public static BlurProgram BLUR_PROGRAM = new BlurProgram();
    public static SnowShader SNOW_PROGRAM = new SnowShader();

    public static float cornerRad = 2f;
    public static float borderWidth = 1f;

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void endRender() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static double distance(float x1, float y1, float x2, float y2) {
        float xd = Math.abs(x1 - x2);
        float yd = Math.abs(y1 - y2);
        return Math.sqrt(xd * xd * yd * yd);
    }

    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }

    public static Color rainbow(int delay, float saturation, float brightness) {
        double rainbow = Math.ceil((System.currentTimeMillis() + delay) / 16f);
        rainbow %= 360;
        return Color.getHSBColor((float) (rainbow / 360), saturation, brightness);
    }

    public static Color rainbow(double delay, float saturation, float brightness, float speed) {
        double rainbow = Math.ceil((System.currentTimeMillis() + delay) / speed);
        float hue = (float) ((rainbow % 6000) / 6000f);
        return new Color(Color.HSBtoRGB(hue, saturation, brightness));
    }

    public static Color skyRainbow(int speed, int index) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        return Color.getHSBColor((double) ((float) ((angle %= 360) / 360.0)) < 0.5 ? -((float) (angle / 360.0)) : (float) (angle / 360.0), 0.5F, 1.0F);
    }

    public static Color fade(int speed, int index, Color color, float alpha) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;

        Color colorHSB = new Color(Color.HSBtoRGB(hsb[0], hsb[1], angle / 360f));

        return new Color(colorHSB.getRed(), colorHSB.getGreen(), colorHSB.getBlue(), Math.max(0, Math.min(255, (int) (alpha * 255))));
    }

    public static Color getAnalogousColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float degree = 0.84f;
        float newHueSubtracted = hsb[0] - degree;
        return new Color(Color.HSBtoRGB(newHueSubtracted, hsb[1], hsb[2]));
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static int applyOpacity(int color_int, float opacity) {
        opacity = Math.min(1, Math.max(0, opacity));
        Color color = new Color(color_int);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity)).getRGB();
    }

    public static Color darker(Color color, float factor) {
        return new Color(Math.min(Math.max((int) (color.getRed() * factor), 0), 255), Math.min(Math.max((int) (color.getGreen() * factor), 0), 255), Math.min(Math.max((int) (color.getBlue() * factor), 0), 255), color.getAlpha());
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255))));
    }

    public static Color interpolateColorsBackAndForth(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? interpolateColorHue(start, end, angle / 360f) : interpolateColorC(start, end, angle / 360f);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount), interpolateInt(color1.getGreen(), color2.getGreen(), amount), interpolateInt(color1.getBlue(), color2.getBlue(), amount), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        float[] color1HSB = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] color2HSB = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        Color resultColor = Color.getHSBColor(interpolateFloat(color1HSB[0], color2HSB[0], amount), interpolateFloat(color1HSB[1], color2HSB[1], amount), interpolateFloat(color1HSB[2], color2HSB[2], amount));

        return new Color(resultColor.getRed(), resultColor.getGreen(), resultColor.getBlue(), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return (float) interpolate(oldValue, newValue, (float) interpolationValue);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return (int) interpolate(oldValue, newValue, (float) interpolationValue);
    }

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX - width <= x && mouseY >= y && mouseY - height <= y;
    }

    public static void renderTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        double z = 0;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, (float) x0, (float) y1, (float) z).texture((u) / (float) textureWidth, (v + (float) regionHeight) / (float) textureHeight);
        buffer.vertex(matrix, (float) x1, (float) y1, (float) z).texture((u + (float) regionWidth) / (float) textureWidth, (v + (float) regionHeight) / (float) textureHeight);
        buffer.vertex(matrix, (float) x1, (float) y0, (float) z).texture((u + (float) regionWidth) / (float) textureWidth, (v) / (float) textureHeight);
        buffer.vertex(matrix, (float) x0, (float) y0, (float) z).texture((u) / (float) textureWidth, (v + 0.0F) / (float) textureHeight);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public static void renderGradientTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight, Color c1, Color c2, Color c3, Color c4) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        renderGradientTextureInternal(buffer, matrices, x0, y0, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight, c1, c2, c3, c4);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    public static void renderGradientTextureInternal(BufferBuilder buff, MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth, double textureHeight, Color c1, Color c2, Color c3, Color c4) {
        double x1 = x0 + width;
        double y1 = y0 + height;
        double z = 0;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        buff.vertex(matrix, (float) x0, (float) y1, (float) z).texture((u) / (float) textureWidth, (v + (float) regionHeight) / (float) textureHeight).color(c1.getRGB());
        buff.vertex(matrix, (float) x1, (float) y1, (float) z).texture((u + (float) regionWidth) / (float) textureWidth, (v + (float) regionHeight) / (float) textureHeight).color(c2.getRGB());
        buff.vertex(matrix, (float) x1, (float) y0, (float) z).texture((u + (float) regionWidth) / (float) textureWidth, (v) / (float) textureHeight).color(c3.getRGB());
        buff.vertex(matrix, (float) x0, (float) y0, (float) z).texture((u) / (float) textureWidth, (v + 0.0F) / (float) textureHeight).color(c4.getRGB());
    }

    public static void renderRoundedGradientRect(MatrixStack matrices, Color color1, Color color2, Color color3, Color color4, float x, float y, float width, float height, float Radius) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL40C.GL_COLOR_BUFFER_BIT, false);
        RenderSystem.colorMask(true, true, true, true);

        drawRound(matrices, x, y, width, height, Radius, color1);
        setupRender();
        RenderSystem.blendFunc(GL40C.GL_DST_ALPHA, GL40C.GL_ONE_MINUS_DST_ALPHA);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(color1.getRGB());
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(color2.getRGB());
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(color3.getRGB());
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(color4.getRGB());
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }

    public static BufferBuilder preShaderDraw(MatrixStack matrices, float x, float y, float width, float height) {
        setupRender();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        setRectanglePoints(buffer, matrix, x, y, x + width, y + height);
        return buffer;
    }

    public static void setRectanglePoints(BufferBuilder buffer, Matrix4f matrix, float x, float y, float x1, float y1) {
        buffer.vertex(matrix, x, y, 0);
        buffer.vertex(matrix, x, y1, 0);
        buffer.vertex(matrix, x1, y1, 0);
        buffer.vertex(matrix, x1, y, 0);
    }

    public static void drawRound(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color) {
        drawRound$randMod(matrices, x, y, width, height, radius, color);
    }

    public static void drawRound(MatrixStack matrices, float x, float y, float width, float height, float radius, GradientRect color) {
        if(ClickGUI.gradient.isEnabled()) drawRect(matrices, x, y, width, height, radius, color.c1.getAlpha(), color.c1, color.c2, color.c3, color.c4);
        else drawRect(matrices, x, y, width, height, radius, color.c1.getAlpha(), color.c1, color.c1, color.c1, color.c1);
    }

    static float m1 = 0;
    static float m2 = 0;
    static float m3 = 0;
    static float m4 = 0;
    static Random random = null;

    public static void drawRound$randMod(MatrixStack matrices, float x, float y, float width, float height, float radius, Color color) {
        if(random == null) random = new Random();
        m1 = 1f + random.nextFloat(-0.2f, 0);
        m2 = 1f + random.nextFloat(-0.2f, 0);
        m3 = 1f + random.nextFloat(-0.2f, 0);
        m4 = 1f + random.nextFloat(-0.2f, 0);


        Color c1 = darker(color, m1);
        Color c2 = darker(color, m2);
        Color c3 = darker(color, m3);
        Color c4 = darker(color, m4);

        if(ClickGUI.colRandomizer.isEnabled()) drawRect(matrices, x, y, width, height, radius, color.getAlpha(), c1, c2, c3, c4);
        else drawRect(matrices, x, y, width, height, radius, color.getAlpha(), color, color, color, color);
    }

    public record GradientRect(Color c1, Color c2, Color c3, Color c4) {
        public static GradientRect of(Color c) {
            return new GradientRect(c, c, c, c);
        }
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, float radius, float alpha) {
        BufferBuilder bb = preShaderDraw(matrices, x - 10, y - 10, width + 20, height + 20);
        RECT_PROGRAM.setParameters(x, y, width, height, radius, alpha);
        RECT_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram(bb.end());
        endRender();
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, float radius, float alpha, Color c1, Color c2, Color c3, Color c4) {
        BufferBuilder bb = preShaderDraw(matrices, x - 10, y - 10, width + 20, height + 20);
        RECT_PROGRAM.setParameters(x, y, width, height, radius, alpha, c1, c2, c3, c4);
        RECT_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram(bb.end());
        endRender();
    }

    public static void drawArc(MatrixStack matrices, float x, float y, float width, float height, float radius, float thickness, float start, float end, Color c1, Color c2) {
        BufferBuilder bb = preShaderDraw(matrices, x - width / 2f, y - height / 2f, x + width / 2f, y + height / 2f);
        ARC_PROGRAM.setParameters(x, y, width, height, radius, thickness, start, end, c1, c2);
        ARC_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram(bb.end());
        endRender();
    }

    public static void drawHudBase(MatrixStack matrices, float x, float y, float width, float height, float radius, float alpha) {
        radius = Math.max(1.5f, radius);
        BufferBuilder bb = preShaderDraw(matrices, x - 10, y - 10, width + 20, height + 20);
        HUD_PROGRAM.setParameters(x, y, width, height, radius, alpha, alpha);
        HUD_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram(bb.end());
        endRender();
    }

    public static void drawSnow(MatrixStack matrices, float time) {
        BufferBuilder bb = preShaderDraw(matrices, 0, 0, mc.getWindow().getWidth(), mc.getWindow().getHeight());
        SNOW_PROGRAM.setParameters(1f, 1f, time);
        SNOW_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram(bb.end());
        endRender();
    }

    private static void drawSnowInternal(MatrixStack matrices, float time, float speed) {
        float normalizedValue = (float)(Math.sin(time * speed / 10) * 0.5 + 0.5);
        float alpha = alphaFromFloat(speed);

        float winW = mc.getWindow().getScaledWidth();
        float winH = mc.getWindow().getScaledHeight();

        for (int n = 0; n < mc.getWindow().getScaledHeight(); n+=5) {
            for (float i = 0; i <= 1; i+=0.02f) {
                float x = winW * i + n;
                float y = winH * normalizedValue + n;
                float w = 0.5f;
                float h = 0.5f;

                drawRound(matrices, x, y, w, h, 0.5f, injectAlpha(Color.WHITE, (int) (alpha * 255)));
            }
        }
    }

    public static float alphaFromFloat(float iValue) {
        float value = (float)(Math.sin(iValue) * 0.5 + 0.5);
        if(iValue <= 1f && iValue > 0f) value = iValue;
        if(value >= 1f) return 1f;
        else if(value >= 0.75f) return 0.8f;
        else if(value >= 0.5f) return 0.6f;
        else if(value >= 0.2f) return 0.4f;
        else return 0.2f;
    }

    public static void drawRoundedBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1, float blurStrenth, float blurOpacity) {
        BufferBuilder bb = preShaderDraw(matrices, x - 10, y - 10, width + 20, height + 20);
        BLUR_PROGRAM.setParameters(x, y, width, height, radius, c1, blurStrenth, blurOpacity);
        BLUR_PROGRAM.use();
        BufferRenderer.drawWithGlobalProgram(bb.end());
        endRender();
    }

    public static void drawTextHudBase(RenderContext context, float x, float y, AtomicDouble width, AtomicDouble height, String text) {
        Pulse2D.drawHudBase(context.getMatrices(), (float) x, (float) y, (float) 3 + RenderUtil.textRenderer.getWidth(text), (float) height.get(),
                Math.max(1.5f, cornerRad), 0.85f);
        RenderUtil.textRenderer.drawString(context.matrixStack(), text, x + 1.5, y + 1 + RenderUtil.fontOffsetY, context.colorScheme().TEXT().getRGB());
        width.set(3 + RenderUtil.textRenderer.getWidth(text));
        height.set(2 + RenderUtil.textRenderer.getStringHeight(text, false));
    }

    public static void drawTextHudBase(RenderContext context, float x, float y, AtomicDouble width, AtomicDouble height, FontRenderer.ColoredString text) {
        Pulse2D.drawHudBase(context.getMatrices(), (float) x, (float) y, (float) 3 + text.getWidth(), (float) height.get(),
                Math.max(1.5f, cornerRad), 0.85f);
        RenderUtil.textRenderer.drawColoredString(context.matrixStack(), text, (float) (x + 1.5), y + 1 + RenderUtil.fontOffsetY);
        width.set(3 + text.getWidth());
        height.set(2 + text.getHeight());
    }

    public static void drawBlurredRect(MatrixStack matrices, float x, float y, float width, float height, float radius, Color c1, float blurStrenth, float blurOpacity) {
//        drawRound(matrices, x, y, width, height, radius, injectAlpha(c1, 50));
        drawRoundedBlur(matrices, x, y, width, height, radius, c1, blurStrenth, blurOpacity);
    }

    public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples) {
        renderRoundedQuadInternal(matrix, cr, cg, cb, ca, fromX, fromY, toX, toY, rad, rad, rad, rad, samples);
    }

    public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radC1, double radC2,
                                                 double radC3, double radC4, double samples) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        double[][] map = new double[][] { new double[] { toX - radC4, toY - radC4, radC4 }, new double[] { toX - radC2, fromY + radC2, radC2 },
                new double[] { fromX + radC1, fromY + radC1, radC1 }, new double[] { fromX + radC3, toY - radC3, radC3 } };
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = i * 90d; r < 360 / 4d + i * 90d; r += 90 / samples) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
            }
            float rad1 = (float) Math.toRadians(360 / 4d + i * 90d);
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static void renderRoundedQuadWithShadow(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double rad, double samples) {
        int color = c.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = transformColor((float) (color >> 24 & 255) / 255.0F);
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, rad, rad, rad, rad, samples);

        renderRoundedShadow(matrices, new Color(10, 10, 10, 100), fromX, fromY, toX, toY, rad, samples, 3);
        endRender();
    }

    public static float transformColor(float f) {
        return AlphaOverride.compute((int) (f * 255)) / 255f;
    }

    public static void renderRoundedQuad(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radC1, double radC2, double radC3, double radC4,
                                         double samples) {
        int color = c.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = transformColor((float) (color >> 24 & 255) / 255.0F);
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, radC1, radC2, radC3, radC4, samples);
        endRender();
    }

    public static void renderRoundedShadow(MatrixStack matrices, Color innerColor, double fromX, double fromY, double toX, double toY, double rad, double samples, double shadowWidth) {
        int color = innerColor.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        renderRoundedShadowInternal(matrix, g, h, k, transformColor(f), fromX, fromY, toX, toY, rad, samples, shadowWidth);
        endRender();
    }

    public static void renderRoundedShadowInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double rad, double samples,
                                                   double wid) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        double[][] map = new double[][] { new double[] { toX1, toY1 }, new double[] { toX1, fromY1 }, new double[] { fromX1, fromY1 },
                new double[] { fromX1, toY1 } };
        for (int i = 0; i < map.length; i++) {
            double[] current = map[i];
            for (double r = i * 90d; r < 360 / 4d + i * 90d; r += 90 / samples) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
                float sin1 = (float) (sin + Math.sin(rad1) * wid);
                float cos1 = (float) (cos + Math.cos(rad1) * wid);
                bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f);
            }
        }
        double[] current = map[0];
        float rad1 = (float) Math.toRadians(0);
        float sin = (float) (Math.sin(rad1) * rad);
        float cos = (float) (Math.cos(rad1) * rad);
        bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
        float sin1 = (float) (sin + Math.sin(rad1) * wid);
        float cos1 = (float) (cos + Math.cos(rad1) * wid);
        bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static void renderRoundedQuad(MatrixStack stack, Color c, double x, double y, double x1, double y1, double rad, double samples) {
        renderRoundedQuad(stack, c, x, y, x1, y1, rad, rad, rad, rad, samples);
    }

    public static Color astolfo(int yOffset) {
        float speed = 20 * 100;
        float hue = (System.currentTimeMillis() % (int) speed) + yOffset;
        if (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5F) {
            hue = 0.5F - (hue - 0.5F);
        }
        hue += 0.5F;
        return Color.getHSBColor(hue, 0.4F, 1F);
    }

    public static void beginScissor(double x, double y, double endX, double endY) {
        double width = endX - x;
        double height = endY - y;
        width = Math.max(0, width);
        height = Math.max(0, height);
        float mulScale = (float) mc.getWindow().getScaleFactor();
        int invertedY = (int) ((mc.getWindow().getScaledHeight() - (y + height)) * mulScale);
        RenderSystem.enableScissor((int) (x * mulScale), invertedY, (int) (width * mulScale), (int) (height * mulScale));
    }

    public static void endScissor() {
        RenderSystem.disableScissor();
    }

    public static double distance(double x, double z, double x1, double z1) {
        return distance(((float) x), ((float) z), ((float) x1), ((float) z1));
    }

    public static class Elements {
        public static void container(RenderContext context, float x0, float y0, float w, float h, boolean alternativeBG, boolean alternativeBorder, boolean rainbowBorder) {
            Color bColor = rainbowBorder ? astolfo(0) : (alternativeBorder ? context.colorScheme().getBorderColor().brighter() : context.colorScheme().getBorderColor());

            drawRound(context.matrixStack(), x0, y0, w, h, cornerRad, bColor);
            drawRound(context.matrixStack(), x0 + borderWidth, y0 + borderWidth, w - borderWidth * 2, h - borderWidth  * 2, cornerRad, alternativeBG ? context.colorScheme().PRIMARY().brighter() : context.colorScheme().PRIMARY());
        }

        public static void container(RenderContext context, float x, float y, float w, float h, boolean alternateBorder) {
            container(context, x, y, w, h, false, alternateBorder, false);
        }

        public static void container(RenderContext context, float x, float y, float w, float h) {
            container(context, x, y, w, h, false, false, false);
        }

        public static void container(RenderContext context, float x, float y, float w, float h, boolean alternativeBG, boolean alternativeBorder) {
            container(context, x, y, w, h, alternativeBG, alternativeBorder, false);
        }
    }

    public static void renderQuad(RenderContext context, Color color, double x1, double y1, double x2, double y2) {
        MatrixStack matrices = context.matrixStack();

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int colorInt = color.getRGB();

        useBuffer(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR, buffer -> {
            buffer.vertex(matrix, (float) x1, (float) y2, 0.0F)
                    .color(colorInt);
            buffer.vertex(matrix, (float) x2, (float) y2, 0.0F)
                    .color(colorInt);
            buffer.vertex(matrix, (float) x2, (float) y1, 0.0F)
                    .color(colorInt);
            buffer.vertex(matrix, (float) x1, (float) y1, 0.0F)
                    .color(colorInt);

            setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            endRender();
        });
    }

    public static class Icons {
        public static Identifier CONFIG = Identifier.of("pulse", "icons/config.png");
        public static Identifier COMBAT = Identifier.of("pulse", "icons/combat.png");
        public static Identifier MOVE = Identifier.of("pulse", "icons/movement.png");
        public static Identifier RENDER = Identifier.of("pulse", "icons/render.png");
        public static Identifier WORLD = Identifier.of("pulse", "icons/world.png");
        public static Identifier MISC = Identifier.of("pulse", "icons/misc.png");
        public static Identifier HUD = Identifier.of("pulse", "icons/hud.png");
        public static Identifier ARROW_LEFT = Identifier.of("pulse", "icons/category-hidden.png");

        static Identifier DARK_CONFIG = Identifier.of("pulse", "icons/dark_config.png");
        static Identifier DARK_COMBAT = Identifier.of("pulse", "icons/dark_combat.png");
        static Identifier DARK_MOVE = Identifier.of("pulse", "icons/dark_movement.png");
        static Identifier DARK_RENDER = Identifier.of("pulse", "icons/dark_render.png");
        static Identifier DARK_WORLD = Identifier.of("pulse", "icons/dark_world.png");
        static Identifier DARK_MISC = Identifier.of("pulse", "icons/dark_misc.png");
        static Identifier DARK_HUD = Identifier.of("pulse", "icons/dark_hud.png");

        static void setColor() {
            Color c = ClickGUI.iconColor.is("Secondary") ? ThemeInfo.COLORSCHEME.SECONDARY() : ThemeInfo.COLORSCHEME.ACCENT();
            setColor(c);
        }

        static void setColor(Color c) {
            RenderSystem.setShaderColor((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);
        }

        public static void config(RenderContext context, double x, double y, double w, double h) {
            setColor();
            Renderer2d.renderTexture(context.matrixStack(), CONFIG, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        public static void combat(RenderContext context, double x, double y, double w, double h) {
            setColor();
            Renderer2d.renderTexture(context.matrixStack(), COMBAT, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        public static void move(RenderContext context, double x, double y, double w, double h) {
            setColor();
            Renderer2d.renderTexture(context.matrixStack(), MOVE, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        public static void render(RenderContext context, double x, double y, double w, double h) {
            setColor();
            Renderer2d.renderTexture(context.matrixStack(), RENDER, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        public static void world(RenderContext context, double x, double y, double w, double h) {
            setColor();
            Renderer2d.renderTexture(context.matrixStack(), WORLD, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        public static void misc(RenderContext context, double x, double y, double w, double h) {
            setColor();
            Renderer2d.renderTexture(context.matrixStack(), MISC, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        public static void hud(RenderContext context, double x, double y, double w, double h) {
            setColor();
            Renderer2d.renderTexture(context.matrixStack(), HUD, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        public static void arrowLeft(RenderContext context, double x, double y, double w, double h) {
            setColor();
            Renderer2d.renderTexture(context.matrixStack(), ARROW_LEFT, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        public static void renderIcon(Identifier icon, RenderContext context, double x, double y, double w, double h, @Nullable Color colorOverride) {
            if(colorOverride != null) setColor(colorOverride);
            else setColor();
            Renderer2d.renderTexture(context.matrixStack(), icon, x, y, w, h);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        static List<Runnable> queue = new ArrayList<>();
        public static void queue(Runnable runnable) {
            queue.add(runnable);
        }

        public static void renderQueue() {
            Iterator<Runnable> i = queue.iterator();
            while (i.hasNext()) {
                i.next().run();
                i.remove();
            }
        }
    }

    public static void useBuffer(VertexFormat.DrawMode mode, VertexFormat format, BufferRunnable runnable) {
        runnable.run(Tessellator.getInstance().begin(mode, format));
    }

    public static interface BufferRunnable {
        void run(BufferBuilder buffer);
    }

//    public Vec2f projectVector(Vec3d vec, Quaternionf rotation, Vec3d origin) {
//        Vector3f transform = vec.subtract(origin).toVector3f();
//        transform = rotation.transform(transform).add(origin.toVector3f());
//        return projectVector(transform.x, transform.y, transform.z);
//    }
//
//    public Vec2f projectVector(Vec3d vec, Quaternionf rotation) {
//        Vector3f transform = rotation.transform(vec.toVector3f());
//        return projectVector(transform.x, transform.y, transform.z);
//    }
//
//    public Vec2f projectVector(double x, double y, double z) {
//        double focal = -MathUtils.lerpClamped(focalPoint.z, 1, zoomDelta);
//        double depth = MathUtils.lerpClamped(focal + z, (focal + z) * 0.055, zoomDelta);
//        if (depth >= 0)
//            depth = -0.00000000000000000000000000000000001;
//
//        float px = (float)(focal * x / depth);
//        float py = (float)(focal * y / depth);
//        //System.out.printf("PROJECT [%s, %s, %s] -> [%s, %s]%n".formatted((int)x, (int)y, (int)z, (int)px, (int)py));
//        return new Vec2f(px, py);
//    }
}
