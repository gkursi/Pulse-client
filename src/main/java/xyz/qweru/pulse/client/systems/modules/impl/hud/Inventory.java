package xyz.qweru.pulse.client.systems.modules.impl.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Inventory extends HudModule {

    static float size = 16;

    BooleanSetting overlay = booleanSetting()
            .name("Overlay")
            .description(".")
            .build();

    public Inventory() {
        hudBuilderOf(this)
                .area(2 + (size + 1) * 9, 2 + (size + 1) * 3)
                .getBuilder()
                .name("Inventory")
                .description("Shows your inventory")
                .settings(overlay)
                .category(Category.HUD);
    }

    float scale = 1f;
    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        float spacing = 1;

        Pulse2D.drawHudBase(context.getMatrices(), (float) x, (float) y, (float) width, (float) height, Pulse2D.cornerRad, 0.85f);
        int slot = 9;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                float x1 = (float) (x + j * size * scale + j * spacing * scale + 1);
                float y1 = (float) (y + i * size * scale + i * spacing * scale + 1);
                ItemStack stack = mc.player.getInventory().getStack(slot);
                RenderUtil.drawItem(drawContext, stack, (int) x1, (int) y1, scale, overlay.isEnabled());
                slot++;
            }
        }
    }
}
