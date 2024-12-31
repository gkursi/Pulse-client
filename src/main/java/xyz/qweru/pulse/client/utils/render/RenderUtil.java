package xyz.qweru.pulse.client.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;
import xyz.qweru.pulse.client.utils.render.font.Font;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class RenderUtil {

    public static FontRenderer textRenderer;
    public static float fontOffsetY = 0; // some fonts have an offset for some reason, workaround

    static Font FLUX_BOLD;
    static Font FLUX_EXTRALIGHT;
    static Font FLUX_LIGHT;
    static Font FLUX_MEDIUM;
    static Font FLUX_THIN;
    static Font NOTO_REGULAR;
    static Font NOTOSANS_LIGHT;
    static Font VERDANA;
    static Font COM_BOLD;
    static Font COM_LIGHT;
    static Font COM_MEDIUM;
    static Font COM_REGULAR;
    static Font COM_SEMIBOLD;

    static Font curFont;
    public static String curFontName = "flux-medium";
    public static void initFont() {
        FLUX_BOLD = new Font("flux-bold.ttf", 18f, -2.5f);
        FLUX_EXTRALIGHT = new Font("flux-extralight.ttf", 18f, -2);
        FLUX_LIGHT = new Font("flux-light.ttf", 18f, -2);
        FLUX_MEDIUM = new Font("flux-medium.ttf", 18f, -2.5f);
        FLUX_THIN = new Font("flux-thin.ttf", 18f, -2);
        NOTO_REGULAR = new Font("noto-regular.ttf", 18f, -3);
        NOTOSANS_LIGHT = new Font("notosans-light.ttf", 18f, -3);
        VERDANA = new Font("Verdana", 18f, 0);
        COM_BOLD = new Font("com-bold.ttf", 16f, -1);
        COM_LIGHT = new Font("com-light.ttf", 16f, -1);
        COM_MEDIUM = new Font("com-medium.ttf", 16f, -1);
        COM_REGULAR = new Font("com-regular.ttf", 16f, -1);
        COM_SEMIBOLD = new Font("com-semibold.ttf", 16f, -1);

        textRenderer = FLUX_MEDIUM.get();
        fontOffsetY = FLUX_MEDIUM.getOffsetY();
        curFont = FLUX_MEDIUM;

        updateFont();
    }

    public static void updateFont() {
        switch (curFontName.toLowerCase()) {
            case "flux-bold" -> {
                curFont = FLUX_BOLD;
            }
            case "flux-extralight" -> {
                curFont = FLUX_EXTRALIGHT;
            }
            case "flux-light" -> {
                curFont = FLUX_LIGHT;
            }
            case "flux-medium" -> {
                curFont = FLUX_MEDIUM;
            }
            case "flux-thin" -> {
                curFont = FLUX_THIN;
            }
            case "noto-regular" -> {
                curFont = NOTO_REGULAR;
            }
            case "notosans-light" -> {
                curFont = NOTOSANS_LIGHT;
            }
            case "comforta-bold" -> {
                curFont = COM_BOLD;
            }
            case "comforta-light" -> {
                curFont = COM_LIGHT;
            }
            case "comforta-medium" -> {
                curFont = COM_MEDIUM;
            }
            case "comforta-regular" -> {
                curFont = COM_REGULAR;
            }
            case "comforta-semibold" -> {
                curFont = COM_SEMIBOLD;
            }
            case "verdana" -> curFont = VERDANA;
            default -> curFont = NOTOSANS_LIGHT;
        }

        try {
            textRenderer = curFont.get();
            fontOffsetY = curFont.getOffsetY();
        } catch (Exception ignored) {}
    }

    public static boolean isInside(double mouseX, double mouseY, float x1, float y1, float x2, float y2) {
        return mouseX > x1 && mouseX < x2 && mouseY > y1 && mouseY < y2;
    }

    public static Vector3d set(Vector3d vec, Vec3d v) {
        vec.x = v.x;
        vec.y = v.y;
        vec.z = v.z;

        return vec;
    }

    public static Vector3d set(Vector3d vec, Entity entity, double tickDelta) {
        vec.x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        vec.y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        vec.z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

        return vec;
    }

    public static Vec3d lerp(Vec3d pos1, Vec3d pos2, double tickDelta) {
        double x = MathHelper.lerp(tickDelta, pos1.x, pos2.x);
        double y = MathHelper.lerp(tickDelta, pos1.y, pos2.y);
        double z = MathHelper.lerp(tickDelta, pos1.z, pos2.z);

        return new Vec3d(x, y, z);
    }

    static VertexSorter vertexSorter;
    public static void unscaledProjection() {
        vertexSorter = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(
                new Matrix4f().setOrtho(
                        0f,
                        mc.getWindow().getFramebufferWidth(),
                        mc.getWindow().getFramebufferHeight(), 0f, 1000f, 21000f
                ), VertexSorter.BY_Z
        );
    }

    public static void scaledProjection() {
        RenderSystem.setProjectionMatrix(
                new Matrix4f().setOrtho(
                        0f,
                        (float) (mc.getWindow().getFramebufferWidth() / mc.getWindow().getScaleFactor()),
                        (float) (mc.getWindow().getFramebufferHeight() / mc.getWindow().getScaleFactor()), 0f, 1000f, 21000f
                ), vertexSorter
        );
    }

    public static void drawItem(DrawContext drawContext, ItemStack itemStack, int x, int y, float scale, boolean overlay, String countOverride) {
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        matrices.scale(scale, scale, 1f);
        matrices.translate(0, 0, 401); // Thanks Mojang

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        drawContext.drawItem(itemStack, scaledX, scaledY);
        if (overlay) drawContext.drawItemInSlot(mc.textRenderer, itemStack, scaledX, scaledY, countOverride);

        matrices.pop();
    }

    public static void drawItem(DrawContext drawContext, ItemStack itemStack, int x, int y, float scale, boolean overlay) {
        drawItem(drawContext, itemStack, x, y, scale, overlay, null);
    }

    public static Vec3d getPointInDirection(Vec3d startingPoint, float pitch, float yaw, double distance) {
        double pitchRadians = Math.toRadians(pitch);
        double yawRadians = Math.toRadians(yaw);

        double x = -Math.cos(yawRadians) * Math.cos(pitchRadians);
        double y = -Math.sin(pitchRadians);
        double z = Math.sin(yawRadians) * Math.cos(pitchRadians);

        Vec3d direction = new Vec3d(x, y, z).normalize().multiply(distance);
        return startingPoint.add(direction);
    }
}
