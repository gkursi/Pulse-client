package xyz.qweru.pulse.client.render.ui.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.gui.PulseScreen;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.render.ui.gui.WidgetGroup;
import xyz.qweru.pulse.client.render.ui.gui.widgets.CategoryBackgroundWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.CategoryTitleWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.IconButtonWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.ModuleWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.settings.TextSettingWidget;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.impl.setting.HudEditor;
import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class MainScreen extends PulseScreen {
    public MainScreen() {
        super("pulse-main");
    }

    float cellW = 82;
    float cellH = 13;
    float cellSeparator = 2;
    float categorySeparator = 4;
    TextSetting searchAccessor = null;

    @Override
    protected void init() {
        super.init();
        searchAccessor = new TextSetting("Search", "Module search", "", true);

        // categories
        float x = 10;
        float y = 10;

        for (Category category : Category.values()) {
            if(category == Category.HUD || category == Category.SETTING) continue;
            WidgetGroup group = new WidgetGroup(true);
            CategoryBackgroundWidget bw = new CategoryBackgroundWidget(x, y, cellW, cellH);
            group.add(bw);
            group.add(new CategoryTitleWidget(x, y, cellW, cellH, category));

            y += cellH + Pulse2D.borderWidth;
            for (ClientModule clientModule : ModuleManager.INSTANCE.getModulesByCategory(category)) {
                group.add(new ModuleWidget(x, y, cellW, cellH, clientModule, searchAccessor));
                y += cellH;
            }
            y += 1;

            bw.resizeTo(y);
            y = 10;
            x += cellW + categorySeparator;

            widgetList.add(group);
        }

        // utility bar
        float spacing = 1;
        float searchW = 100;
        float iconW = 12;
        float barW = searchW + iconW * 2 + spacing * 4; // search + 2 icon + 4 spacing
        float barH = 12 + spacing * 2; // 12 + 2 spacing

        float barX = (width - barW) / 2;
        float barY = height - barH - spacing * 2;

        CategoryBackgroundWidget background = new CategoryBackgroundWidget(barX, barY, barW, barH);
        TextSettingWidget textInput = new TextSettingWidget(barX + spacing, barY + spacing, searchW, barH - spacing * 2, searchAccessor);
        IconButtonWidget hudBtn = new IconButtonWidget(barX + searchW + spacing, barY + spacing, iconW, barH - spacing * 2, Identifier.of("pulse", "icons/hud.png"),
                () -> Managers.MODULE.getItemByClass(HudEditor.class).setEnabled(true));
        IconButtonWidget settingBtn = new IconButtonWidget(barX + searchW + spacing * 2 + iconW, barY + spacing, iconW, barH - spacing * 2, Identifier.of("pulse", "icons/config.png"),
                () -> mc.setScreen(PulseClient.INSTANCE.windowManager.getItemByClass(ConfigScreen.class)));

        addWidget(background);
        addWidget(textInput);
        addWidget(hudBtn);
        addWidget(settingBtn);
    }

    @Override
    public void close() {
        for (Widget widget : widgetList) {
            if(widget instanceof WidgetGroup wg) {
                for (Widget widget1 : wg.widgets) {
                    if(widget1 instanceof ModuleWidget w && w.listening) return;
                }
            }
        }
        super.close();
    }
}
