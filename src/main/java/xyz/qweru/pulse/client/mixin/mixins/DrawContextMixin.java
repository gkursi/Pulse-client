package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import java.awt.*;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Shadow @Final private MatrixStack matrices;

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)I", at = @At("HEAD"), cancellable = true)
    void ordererText(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir) {
//        FontRenderer.ColoredString string = FontRenderer.ColoredString.of("", Color.GREEN);
//
//        text.accept((index, style, codePoint) -> {
//            String ch = new StringBuilder().appendCodePoint(codePoint).toString();
//            Color col = style.getColor() == null ? Color.WHITE : new Color(style.getColor().getRgb());
//            string.add(ch, col);
//            return true;
//        });
//
//        RenderUtil.textRenderer.drawColoredString(matrices, string, x, y + RenderUtil.fontOffsetY);
//        cir.cancel();
    }

}
