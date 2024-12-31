package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Coords extends HudModule {

    BooleanSetting nether = booleanSetting()
            .name("Nether")
            .description("Also show nether coords")
            .build();

    BooleanSetting multiline = booleanSetting()
            .name("Multi-line")
            .description("Display coordinates in different lines")
            .build();

    public Coords() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(30, 13)
                .getBuilder()
                .name("Coords")
                .description("Shows your coordinates")
                .settings(nether, multiline)
                .category(Category.HUD);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        AtomicDouble nW = new AtomicDouble(width);
        AtomicDouble nH = new AtomicDouble(height);
        FontRenderer text = RenderUtil.textRenderer;

        double playerX = Util.round(mc.player.getX(), 1);
        double playerY = Util.round(mc.player.getY(), 1);
        double playerZ = Util.round(mc.player.getZ(), 1);
        String xStr = "%s".formatted(playerX) + (nether.isEnabled() ? " (%s) ".formatted(Util.round(playerX / 8, 1)) : " ");
        String yStr = "%s".formatted(playerY) + (nether.isEnabled() ? " (%s) ".formatted(Util.round(playerY / 8, 1)) : " ");
        String zStr = "%s".formatted(playerZ) + (nether.isEnabled() ? " (%s)".formatted(Util.round(playerZ / 8, 1)) : "");

        if(multiline.isEnabled()) {
            float xW = text.getWidth("X: " + xStr);
            float yW = text.getWidth("Y: " + yStr);
            float zW = text.getWidth("Z: " + zStr);

            float xH = text.getHeight("X: " + xStr);
            float yH = text.getHeight("Y: " + yStr);
            float zH = text.getHeight("Z: " + zStr);

            width = Math.max(zW, Math.max(xW, yW)) + 1;
            height = xH + yH + zH + 6;

            Pulse2D.drawHudBase(context.matrixStack(), (float) x, (float) y, (float) width, (float) height, Pulse2D.cornerRad, 0.85f);
            text.drawString(context.matrixStack(), xStr, x + 1, y + 1 + RenderUtil.fontOffsetY, ThemeInfo.COLORSCHEME.TEXT().getRGB());
            text.drawString(context.matrixStack(), yStr, x + 1, y + xH + 3 + RenderUtil.fontOffsetY, ThemeInfo.COLORSCHEME.TEXT().getRGB());
            text.drawString(context.matrixStack(), zStr, x + 1, y + xH + yH + 5 + RenderUtil.fontOffsetY, ThemeInfo.COLORSCHEME.TEXT().getRGB());
        } else {
            FontRenderer.ColoredString string = FontRenderer.ColoredString.of("X: ", context.colorScheme().getLabelColor());
            string.add(xStr, context.colorScheme().MUTED_TEXT());
            string.add("Y: ", context.colorScheme().getLabelColor());
            string.add(yStr, context.colorScheme().MUTED_TEXT());
            string.add("Z: ", context.colorScheme().getLabelColor());
            string.add(zStr, context.colorScheme().MUTED_TEXT());

            Pulse2D.drawTextHudBase(context, (float) x, (float) y, nW, nH, string);
            width = nW.get();
            height = nH.get();
        }
    }
}
