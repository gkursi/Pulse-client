package xyz.qweru.pulse.client.systems.modules.impl.hud;

import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.render.ui.color.Colors;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Arraylist extends HudModule {
    public Arraylist() {
        super("Arraylist", "Display all enabled modules", -1, Category.HUD, 2, 2, 15, 10);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        if(this.x < (double) context.screenWidth() / 2 && this.y < context.screenHeight()) topLeft(drawContext);
        else if(this.x > (double) context.screenWidth() / 2 && this.y < context.screenHeight()) topRight(drawContext);
        else if(this.x < (double) context.screenWidth() / 2 && this.y > context.screenHeight()) bottomLeft(drawContext);
        else if(this.x > (double) context.screenWidth() / 2 && this.y > context.screenHeight()) bottomRight(drawContext);
    }


    void topRight(DrawContext context) {
        List<String> data = getArray(context);
        if(data.isEmpty()) return;
        height = 0;

        int i = -1;
        for (String str : data) {
            double sy = y + i*RenderUtil.textRenderer.getStringHeight(str, false) + RenderUtil.fontOffsetY + 2 * i;
            double sx = x + width - RenderUtil.textRenderer.getWidth(str);

//            Pulse2D.drawRound(context.getMatrices(), (float) sx - 2, (float) sy - 2, (float) sx + 2 + RenderUtil.textRenderer.getWidth(str),
//                    (float) sy + 2 + RenderUtil.textRenderer.getStringHeight(str, false),
//                    Pulse2D.cornerRad, Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.PRIMARY(), 170));

            RenderUtil.textRenderer.drawString(context.getMatrices(), str, sx, sy, ThemeInfo.COLORSCHEME.TEXT().getRGB());
            height += RenderUtil.textRenderer.getStringHeight(str, false) + 2;
        }

    }

    void topLeft(DrawContext context) {
        List<String> data = getArray(context);
        double nHeight = 0;
        double tY = y;

        for (String mod : data) {
//            Pulse2D.drawRound(context.getMatrices(), (float) x - 2, (float) tY + RenderUtil.fontOffsetY - 2, (float) x + 2 + RenderUtil.textRenderer.getWidth(mod),
//                    (float) tY + RenderUtil.fontOffsetY + 2 + RenderUtil.textRenderer.getStringHeight(mod, false),
//                    Pulse2D.cornerRad, Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.PRIMARY(), 170));
            RenderUtil.textRenderer.drawString(context.getMatrices(), mod, x, tY + RenderUtil.fontOffsetY, Colors.DEFAULT.TEXT().getRGB());
            tY += 1 + RenderUtil.textRenderer.getStringHeight(mod, false);
            nHeight += 1 + RenderUtil.textRenderer.getStringHeight(mod, false);
        }

        height = nHeight;
    }

    void bottomRight(DrawContext context) {
        List<String> data = getArray(context);
        if(data.isEmpty()) return;

        int i = -1;
        for (String str : data) {
            double sy = y + i*RenderUtil.textRenderer.getStringHeight(str, false) + RenderUtil.fontOffsetY + 2 * i;
            double sx = x + width - RenderUtil.textRenderer.getWidth(str);

//            Pulse2D.drawRound(context.getMatrices(), (float) sx - 2, (float) sy - 2, (float) sx + 2 + RenderUtil.textRenderer.getWidth(str),
//                    (float) sy + 2 + RenderUtil.textRenderer.getStringHeight(str, false),
//                    Pulse2D.cornerRad, Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.PRIMARY(), 170));

            RenderUtil.textRenderer.drawString(context.getMatrices(), str, sx, sy, ThemeInfo.COLORSCHEME.TEXT().getRGB());
            height += RenderUtil.textRenderer.getStringHeight(str, false) + 2;
        }
    }

    void bottomLeft(DrawContext context) {
        List<String> data = getArray(context);
        double nHeight = 0;
        double tY = y;

//        Pulse2D.drawHudBase(context.getMatrices(), (float) x - 2, (float) y - 2, (float) width + 2, (float) height + 2, Pulse2D.cornerRad);
        for (String mod : data) {
//            Pulse2D.drawRound(context.getMatrices(), (float) x - 2, (float) tY + RenderUtil.fontOffsetY - 2, (float) x + 2 + RenderUtil.textRenderer.getWidth(mod),
//                    (float) tY + RenderUtil.fontOffsetY + 2 + RenderUtil.textRenderer.getStringHeight(mod, false),
//                    Pulse2D.cornerRad, Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.PRIMARY(), 170));
            RenderUtil.textRenderer.drawString(context.getMatrices(), mod, x, tY + RenderUtil.fontOffsetY, Colors.DEFAULT.TEXT().getRGB());
            tY += 1 + RenderUtil.textRenderer.getStringHeight(mod, false);
            nHeight += 1 + RenderUtil.textRenderer.getStringHeight(mod, false);
        }

        height = nHeight;
    }

    ArrayList<String> getArray(DrawContext context) {
        ArrayList<String> enabled = new ArrayList<>();

        for (ClientModule module : ModuleManager.INSTANCE.getItemList()) {
            if(module.isEnabled()) {
                enabled.add(module.getName());
            }
        }

        enabled.sort(Comparator.comparingDouble((value -> (this.y <= (double) context.getScaledWindowHeight() / 2 ? -RenderUtil.textRenderer.getWidth(value) : RenderUtil.textRenderer.getWidth(value)))));
        return enabled;
    }
}
