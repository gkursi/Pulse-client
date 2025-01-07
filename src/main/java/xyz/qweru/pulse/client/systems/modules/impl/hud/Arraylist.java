package xyz.qweru.pulse.client.systems.modules.impl.hud;

import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.render.ui.color.Colors;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Arraylist extends HudModule {
    public Arraylist() {
        super("Arraylist", "Display all enabled modules", -1, Category.HUD, 2, 2, 100, 300);
        builder().settings(up, left, colorOffset);
    }

    BooleanSetting up = new BooleanSetting("Up", "render up", false, true);
    BooleanSetting left = new BooleanSetting("Left", "render left", true, true);
    BooleanSetting colorOffset = new BooleanSetting("Offset color", "adds offset color to make the module look nicer", true, true);

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        boolean up = this.up.isEnabled();
        boolean left = this.left.isEnabled();
        ArrayList<FontRenderer.ColoredString> list = new ArrayList<>();
        for (ClientModule clientModule : Managers.MODULE.getItemList()) {
            if(clientModule.isEnabled()) {
                FontRenderer.ColoredString string = FontRenderer.ColoredString.of(clientModule.getName(), context.colorScheme().getLabelColor());
                if(!clientModule.getState().isBlank()) string.add(" " + clientModule.getState(), context.colorScheme().TEXT());
                list.add(string);
            }
        }

        list.sort(Comparator.comparingDouble(coloredString -> up ? -coloredString.getWidth() : coloredString.getWidth()));

        float textX = (float) (left ? x : x + width);
        float textY = (float) y;
        for (FontRenderer.ColoredString string : list) {
            if(left) {
                if(colorOffset.isEnabled()) Pulse2D.drawRound(context.getMatrices(), textX - 0, textY, string.getWidth() + 1, string.getHeight() + 1, Pulse2D.cornerRad, context.colorScheme().ACCENT());
                Pulse2D.drawRound(context.getMatrices(), textX - 0 - 1, textY - 1, string.getWidth() + 1, string.getHeight() + 1, Pulse2D.cornerRad, context.colorScheme().PRIMARY());
            } else {
                if(colorOffset.isEnabled()) Pulse2D.drawRound(context.getMatrices(), textX - string.getWidth(), textY, string.getWidth() + 1, string.getHeight() + 1, Pulse2D.cornerRad, context.colorScheme().ACCENT());
                Pulse2D.drawRound(context.getMatrices(), textX - string.getWidth() - 1, textY - 1, string.getWidth() + 1, string.getHeight() + 1, Pulse2D.cornerRad, context.colorScheme().PRIMARY());
            }
            textY += string.getHeight();
        }

        textX = (float) (left ? x : x + width);
        textY = (float) y;
        for (FontRenderer.ColoredString string : list) {
            RenderUtil.textRenderer.drawColoredString(context.getMatrices(), string, textX - (left ? 0 : string.getWidth()), textY + RenderUtil.fontOffsetY);
            textY += string.getHeight();
        }
    }
}
