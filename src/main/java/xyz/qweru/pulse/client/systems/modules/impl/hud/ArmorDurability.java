package xyz.qweru.pulse.client.systems.modules.impl.hud;

import it.unimi.dsi.fastutil.booleans.BooleanSet;
import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.impl.setting.HudEditor;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import java.util.concurrent.atomic.AtomicInteger;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class ArmorDurability extends HudModule {

    BooleanSetting minimal = booleanSetting()
            .name("Small")
            .description("Smaller design")
            .build();

    public ArmorDurability() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(65, 15)
                .getBuilder()
                .name("Armor")
                .description("Show info about your armor")
                .settings(minimal)
                .category(Category.HUD);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        int i = 0;
        float dimensions = 15;

        AtomicInteger trueSize = new AtomicInteger();
        mc.player.getInventory().armor.forEach(itemStack -> {
            if(!itemStack.isEmpty())
                trueSize.addAndGet(1);
        });

        float bW = trueSize.get() * (dimensions + 2) - 2;
        if(trueSize.get() != 0) Pulse2D.drawHudBase(context.getMatrices(), (float) (x - 2), (float) (y - 1), bW + 5,
                (float) (minimal.isEnabled() ? dimensions + 3 : (height + 4 + RenderUtil.textRenderer.getStringHeight("99", false))), Pulse2D.cornerRad, 0.85f);

        for (ItemStack itemStack : mc.player.getInventory().armor) {
            if(itemStack.isEmpty()) continue;
            String itemId = getItemId(itemStack);

            Renderer2d.renderTexture(context.getMatrices(), Identifier.of("minecraft", "textures/item/" + itemId), x + dimensions * i + 3 * i, y + 1, dimensions, dimensions);

            float damage = (float) itemStack.getDamage() / (itemStack.getMaxDamage() == 0 ? itemStack.getDamage() : itemStack.getMaxDamage());
            double percent = Util.round(Math.abs(1 - damage), 1);


            if(minimal.isEnabled()) {
                RenderUtil.textRenderer.drawString(context.getMatrices(),
                        percent + "",
                        x + dimensions * i + 3 * i + 1, y + (dimensions - RenderUtil.textRenderer.getHeight(percent + "")) / 2 + RenderUtil.fontOffsetY,
                        ThemeInfo.COLORSCHEME.TEXT().getRGB());
            } else {
                RenderUtil.textRenderer.drawString(context.getMatrices(),
                        percent + "",
                        x + dimensions * i + 3 * i, y + dimensions + 2 + RenderUtil.fontOffsetY,
                        ThemeInfo.COLORSCHEME.TEXT().getRGB());
            }
            i++;
        }
    }

    private static @NotNull String getItemId(ItemStack itemStack) {
        String itemId = "torch.png"; // no texture
        Item item = itemStack.getItem();
        if(item.equals(Items.NETHERITE_HELMET)) {
            itemId = "netherite_helmet.png";
        } else if(item.equals(Items.NETHERITE_CHESTPLATE)) {
            itemId = "netherite_chestplate.png";
        } else if(item.equals(Items.NETHERITE_LEGGINGS)) {
            itemId = "netherite_leggings.png";
        } else if(item.equals(Items.NETHERITE_BOOTS)) {
            itemId = "netherite_boots.png";
        } else if(item.equals(Items.DIAMOND_CHESTPLATE)) {
            itemId = "diamond_chestplate.png";
        } else if(item.equals(Items.DIAMOND_HELMET)) {
            itemId = "diamond_helmet.png";
        } else if(item.equals(Items.DIAMOND_LEGGINGS)) {
            itemId = "diamond_leggings.png";
        } else if(item.equals(Items.DIAMOND_BOOTS)) {
            itemId = "diamond_boots.png";
        } else if(item.equals(Items.IRON_HELMET)) {
            itemId = "iron_helmet.png";
        } else if(item.equals(Items.IRON_CHESTPLATE)) {
            itemId = "iron_chestplate.png";
        } else if(item.equals(Items.IRON_LEGGINGS)) {
            itemId = "iron_leggings.png";
        } else if(item.equals(Items.IRON_BOOTS)) {
            itemId = "iron_boots.png";
        } else if(item.equals(Items.GOLDEN_HELMET)) {
            itemId = "golden_helmet.png";
        } else if(item.equals(Items.GOLDEN_CHESTPLATE)) {
            itemId = "golden_chestplate.png";
        } else if(item.equals(Items.GOLDEN_LEGGINGS)) {
            itemId = "golden_leggings.png";
        } else if(item.equals(Items.GOLDEN_BOOTS)) {
            itemId = "golden_boots.png";
        } else if(item.equals(Items.LEATHER_HELMET)) {
            itemId = "leather_helmet.png";
        } else if(item.equals(Items.LEATHER_CHESTPLATE)) {
            itemId = "leather_chestplate.png";
        } else if(item.equals(Items.LEATHER_LEGGINGS)) {
            itemId = "leather_leggings.png";
        } else if(item.equals(Items.LEATHER_BOOTS)) {
            itemId = "leather_boots.png";
        } else if(item.equals(Items.CHAINMAIL_HELMET)) {
            itemId = "chainmail_helmet.png";
        } else if(item.equals(Items.CHAINMAIL_CHESTPLATE)) {
            itemId = "chainmail_chestplate.png";
        } else if(item.equals(Items.CHAINMAIL_LEGGINGS)) {
            itemId = "chainmail_leggings.png";
        } else if(item.equals(Items.CHAINMAIL_BOOTS)) {
            itemId = "chainmail_boots.png";
        }

        return itemId;
    }
}
