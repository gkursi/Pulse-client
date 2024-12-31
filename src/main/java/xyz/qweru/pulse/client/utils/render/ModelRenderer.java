package xyz.qweru.pulse.client.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.impl.render.CrystalTweaks;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;

public class ModelRenderer {

    public static final float SINE_45_DEGREES = (float)Math.sin(0.7853981633974483);

    public static void renderCrystal(MatrixStack matrixStack, int i, ModelPart core, ModelPart frame, ColorSetting crystalColor) {
        float pulse$scale = ModuleManager.INSTANCE.getItemByClass(CrystalTweaks.class).isEnabled() ?
                ((CrystalTweaks) ModuleManager.INSTANCE.getItemByClass(CrystalTweaks.class)).scale.getValue() : 1;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        BufferBuilder buffer;

        RenderSystem.setShader(GameRenderer::getPositionProgram);
        buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

        matrixStack.push();
        float h = 0.75f;
        matrixStack.push();
        RenderSystem.setShaderColor(crystalColor.getRed().asFloat(), crystalColor.getGreen().asFloat(), crystalColor.getBlue().asFloat(), crystalColor.getAlpha().asFloat());
        matrixStack.scale(2.0F * pulse$scale, 2.0F * pulse$scale, 2.0F * pulse$scale);
        matrixStack.translate(0.0f, -0.5f, 0.0f);
        int k = OverlayTexture.DEFAULT_UV;
        matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        frame.render(matrixStack, buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        frame.render(matrixStack, buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        core.render(matrixStack, buffer, i, k);
        matrixStack.pop();
        matrixStack.pop();
        BuiltBuffer builtBuffer = buffer.endNullable();
        if (builtBuffer != null)
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }


    public static void renderModelPart(MatrixStack matrices, ModelPart part, Vec3d pos) {
        Renderer3d.renderThroughWalls();
        part.forEachCuboid(matrices, (matrix, path, index, cuboid) -> {
            renderCuboid(cuboid, matrices, part, pos);
        });
    }

    public static void renderCuboid(ModelPart.Cuboid cuboid, MatrixStack matrices, ModelPart parent, Vec3d pos) {
        Vec3d cubeDimensions = new Vec3d(cuboid.maxX - cuboid.minX, cuboid.maxY - cuboid.minY, cuboid.maxZ - cuboid.minZ);
        Renderer3d.renderEdged(
                matrices,
                Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.PRIMARY(), 50),
                Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.ACCENT(), 180),
                pos,
                cubeDimensions
        );
    }
}
