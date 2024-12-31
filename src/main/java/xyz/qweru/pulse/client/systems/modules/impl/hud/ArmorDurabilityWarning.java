package xyz.qweru.pulse.client.systems.modules.impl.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.render.ui.gui.screens.HudConfigScreen;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

// todo settings (durability threshold, text), im too lazy to do this rn
public class ArmorDurabilityWarning extends HudModule {
    public ArmorDurabilityWarning() {
        hudBuilderOf(this)
                .area(100, 14)
                .pos(2, 2)
                .getBuilder()
                .name("Armor warning")
                .description("Shows a warning when your armor durability is below 50%")
                .category(Category.HUD);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        if(mc.currentScreen instanceof HudConfigScreen) {
            drawContext.drawBorder((int) x, (int) y, (int) width, (int) height, ThemeInfo.COLORSCHEME.getBorderColor().getRGB());
        }

        float lowestDura = 1f;
        for (ItemStack itemStack : mc.player.getInventory().armor) {
            if(!itemStack.isEmpty() && itemStack.getMaxDamage() != 0) {
                float dura = 1 - (float) itemStack.getDamage() / itemStack.getMaxDamage();
                if(lowestDura > dura) lowestDura = dura;
            }
        }

        if(lowestDura <= 0.5f) {
            RenderUtil.textRenderer.drawString(context.getMatrices(), "Repair your armor!!", x, y, ThemeInfo.COLORSCHEME.TEXT().getRGB());
        }
    }
}
