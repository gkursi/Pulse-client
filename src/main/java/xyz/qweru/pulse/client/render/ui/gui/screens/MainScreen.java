package xyz.qweru.pulse.client.render.ui.gui.screens;

import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.gui.PulseScreen;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.render.ui.gui.WidgetGroup;
import xyz.qweru.pulse.client.render.ui.gui.widgets.CategoryBackgroundWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.CategoryTitleWidget;
import xyz.qweru.pulse.client.render.ui.gui.widgets.ModuleWidget;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;

public class MainScreen extends PulseScreen {
    public MainScreen() {
        super("pulse-main");
    }

    float cellW = 82;
    float cellH = 13;
    float cellSeparator = 2;
    float categorySeparator = 4;

    @Override
    protected void init() {
        super.init();
        float x = 10;
        float y = 10;

        for (Category category : Category.values()) {
            WidgetGroup group = new WidgetGroup();
            CategoryBackgroundWidget bw = new CategoryBackgroundWidget(x, y, cellW, cellH);
            group.add(bw);
            group.add(new CategoryTitleWidget(x, y, cellW, cellH, category));

            y += cellH + Pulse2D.borderWidth;
            for (ClientModule clientModule : ModuleManager.INSTANCE.getModulesByCategory(category)) {
                group.add(new ModuleWidget(x, y, cellW, cellH, clientModule));
                y += cellH;
            }
            y += 1;

            bw.resizeTo(y);
            y = 10;
            x += cellW + categorySeparator;

            widgetList.add(group);
        }
    }

    @Override
    public void close() {
        for (Widget widget : widgetList) {
            if(widget instanceof ModuleWidget w && w.listening) return;
        }
        super.close();
    }
}
