package xyz.qweru.pulse.client.render.renderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import xyz.qweru.pulse.client.render.ui.color.ColorScheme;
import xyz.qweru.pulse.client.render.ui.gui.PulseScreen;

public record RenderContext(MatrixStack matrixStack, DrawContext context, int mouseX, int mouseY, int screenWidth, int screenHeight, float delta, ColorScheme colorScheme, PulseScreen parent) {
    public MatrixStack getMatrices() {
        return matrixStack;
    }
}
