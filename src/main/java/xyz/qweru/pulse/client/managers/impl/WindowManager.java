package xyz.qweru.pulse.client.managers.impl;

import org.jetbrains.annotations.Nullable;
import xyz.qweru.pulse.client.managers.Manager;
import xyz.qweru.pulse.client.render.ui.color.ColorScheme;
import xyz.qweru.pulse.client.render.ui.gui.screens.ColorScreen;
import xyz.qweru.pulse.client.render.ui.gui.screens.HudConfigScreen;
import xyz.qweru.pulse.client.render.ui.gui.PulseScreen;
import xyz.qweru.pulse.client.render.ui.gui.screens.MainScreen;
import xyz.qweru.pulse.client.render.ui.gui.screens.ModuleScreen;

public class WindowManager extends Manager<PulseScreen> {

    public WindowManager() {
        super("WindowManager");

        addItem(new PulseScreen("a"));
        addItem(new MainScreen());
        addItem(new ModuleScreen());
        addItem(new HudConfigScreen());
        addItem(new ColorScreen(null, null));
    }

    public void applyTheme(ColorScheme theme) {
        for (PulseScreen pulseWindow : itemList) {
            pulseWindow.setColorScheme(theme);
        }
    }

    public void apply(WindowAction action) {
        for (PulseScreen pulseWindow : itemList) {
            action.run(pulseWindow);
        }
    }

    public interface WindowAction {
        void run(PulseScreen window);
    }

    @Override
    public @Nullable PulseScreen getItemByClass(Class<? extends PulseScreen> clazz) {
        @Nullable PulseScreen a = super.getItemByClass(clazz);
        if(a != null) a.reset();
        return a;
    }
}
