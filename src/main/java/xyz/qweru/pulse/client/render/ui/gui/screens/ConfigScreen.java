package xyz.qweru.pulse.client.render.ui.gui.screens;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.gui.PulseScreen;
import xyz.qweru.pulse.client.render.ui.gui.WidgetGroup;
import xyz.qweru.pulse.client.render.ui.gui.widgets.CategoryBackgroundWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.CategoryTitleWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.ModuleWidget;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;

import java.util.List;

public class ConfigScreen extends PulseScreen {
    public ConfigScreen() {
        super("setting screen");
    }

    @Override
    protected void init() {
        super.init();
        Category category = Category.SETTING;
        List<ClientModule> modules = ModuleManager.INSTANCE.getModulesByCategory(category);
        float cellW = 82;
        float cellH = 13;
        float totalH = cellH;
        for (ClientModule ignored : modules) {
            totalH += cellH;
        }

        float x = (width - cellW) / 2;
        float y = (height - totalH) / 2;

        WidgetGroup group = new WidgetGroup(true);
        CategoryBackgroundWidget bw = new CategoryBackgroundWidget(x, y, cellW, cellH);
        group.add(bw);
        group.add(new CategoryTitleWidget(x, y, cellW, cellH, category));

        y += cellH + Pulse2D.borderWidth;
        for (ClientModule clientModule : modules) {
            group.add(new ModuleWidget(x, y, cellW, cellH, clientModule));
            y += cellH;
        }
        y += 1;

        bw.resizeTo(y);

        widgetList.add(group);
    }

    @Override
    public void close() {
        this.client.setScreen(PulseClient.INSTANCE.windowManager.getItemByClass(MainScreen.class));
    }
}
