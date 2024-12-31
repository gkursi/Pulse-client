package xyz.qweru.pulse.client.systems.modules.impl.hud;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.WorldEvents;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

@ExcludeModule
public class EnderChest extends HudModule {

    static float size = 16;

    public static Inventory ENDER_CHEST = null;

    BooleanSetting overlay = booleanSetting()
            .name("Overlay")
            .description(".")
            .build();

    public EnderChest() {
        hudBuilderOf(this)
                .area(2 + (size + 1) * 9, 2 + (size + 1) * 3)
                .getBuilder()
                .name("EChest")
                .description("Shows your ender chest")
                .category(Category.HUD);
    }

    float scale = 1f;
    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        float spacing = 1;

        Pulse2D.drawHudBase(context.getMatrices(), (float) x, (float) y, (float) width, (float) height, Pulse2D.cornerRad, 0.85f);
        if(ENDER_CHEST == null) return;
        int slot = 9;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                float x1 = (float) (x + j * size * scale + j * spacing * scale + 1);
                float y1 = (float) (y + i * size * scale + i * spacing * scale + 1);
                ItemStack stack = ENDER_CHEST.getStack(slot);
                RenderUtil.drawItem(drawContext, stack, (int) x1, (int) y1, scale, overlay.isEnabled());
                slot++;
            }
        }
    }
}
