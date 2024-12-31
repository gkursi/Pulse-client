package xyz.qweru.pulse.client.managers.impl;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Manager;
import xyz.qweru.pulse.client.render.ui.color.ColorScheme;
import xyz.qweru.pulse.client.render.ui.color.Colors;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

public class ThemeManager extends Manager<ColorScheme> {

    ColorScheme activeTheme = Colors.DEFAULT;

    public ThemeManager() {
        super("ThemeManager");

        addItem(Colors.GRUVBOX_DARK_ORANGE);
        addItem(Colors.GRUVBOX_DARK_BLUE);
        addItem(Colors.GRUVBOX_DARK_GREEN);
        addItem(Colors.DARK_GREEN);
        addItem(Colors.CATPPUCCIN_LATTE_BLUE);
        addItem(Colors.PEACH_GREY_BROWN);
        addItem(Colors.PURPLE_YELLOW);
        addItem(Colors.YELLOW_PEACH);
        addItem(Colors.DARKER_MONO);
        addItem(Colors.DARKER_BLUE);
        addItem(Colors.DARKER_PINK);
        addItem(Colors.DARKER_RED);
    }

    public void setTheme(String name) {
        for(ColorScheme scheme : itemList) {
            if(scheme.NAME().equalsIgnoreCase(name)) {
                activeTheme = scheme;
                ThreadManager.fixedPool.submit(() -> {
                    while (PulseClient.INSTANCE == null);
                    PulseClient.INSTANCE.windowManager.applyTheme(activeTheme);
                    ThemeInfo.COLORSCHEME = activeTheme;
                });
                return;
            }
        }
    }

    public ColorScheme getActiveTheme() {
        return activeTheme;
    }
}
