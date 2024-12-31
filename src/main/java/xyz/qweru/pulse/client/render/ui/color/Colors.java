package xyz.qweru.pulse.client.render.ui.color;

import xyz.qweru.pulse.client.render.renderer.Pulse2D;

import java.awt.*;

public class Colors {

    public static ColorScheme GRUVBOX_DARK_ORANGE = new ColorScheme(
            Color.decode("#282828"), // primary
            Color.decode("#504945"), // secondary
            Color.decode("#d65d0e"), // accent
            Color.decode("#ebdbb2"), // text
            Color.decode("#a89984"), // muted text
            "Gruvbox Dark (Orange)",
            true,
            0,
            false
    );

    public static ColorScheme GRUVBOX_DARK_GREEN = new ColorScheme(
            Color.decode("#282828"), // primary
            Color.decode("#504945"), // secondary
            Color.decode("#98971a"), // accent
            Color.decode("#ebdbb2"), // text
            Color.decode("#a89984"), // muted text
            "Gruvbox Dark (Green)",
            true,
            0,
            false
    );

    public static ColorScheme GRUVBOX_DARK_BLUE = new ColorScheme(
            Color.decode("#282828"), // primary
            Color.decode("#504945"), // secondary
            Color.decode("#458588"), // accent
            Color.decode("#ebdbb2"), // text
            Color.decode("#a89984"), // muted text
            "Gruvbox Dark (Blue)",
            true,
            0,
            false
    );

    public static ColorScheme CATPPUCCIN_LATTE_BLUE = new ColorScheme(
            Color.decode("#eff1f5"), // primary
            Color.decode("#dce0e8"), // secondary
            Color.decode("#04a5e5"), // accent
            Color.decode("#4c4f69"), // text
            Color.decode("#6c6f85"), // muted text
            "Catppuccin Latte (Blue)",
            false,
            0,
            true
    );

    public static ColorScheme DARK_GREEN = new ColorScheme(
            Color.decode("#181C14"), // primary
            Color.decode("#3C3D37"), // secondary
            Color.decode("#697565"), // accent
            Color.decode("#ECDFCC"), // text
            Pulse2D.darker(Color.decode("#ECDFCC"), 0.8f), // muted text
            "Dark Green",
            false,
            0,
            false
    );

    public static ColorScheme PEACH_GREY_BROWN = new ColorScheme(
            Color.decode("#EDDFE0"), // primary
            Color.decode("#F5F5F7"), // secondary
            Color.decode("#B7B7B7"), // accent
            Color.decode("#705C53"), // text
            Pulse2D.darker(Color.decode("#705C53"), 1.2f), // muted text
            "Peach / Grey / Brown",
            false,
            0,
            true
    );

    public static ColorScheme PURPLE_YELLOW = new ColorScheme(
            Color.decode("#624E88"), // primary
            Color.decode("#8967B3"), // secondary
            Color.decode("#CB80AB"), // accent
            Color.decode("#E6D9A2"), // text
            Pulse2D.darker(Color.decode("#E6D9A2"), 0.8f), // muted text
            "Purple / Yellow",
            false,
            0,
            false
    );

    public static ColorScheme YELLOW_PEACH = new ColorScheme(
            Color.decode("#FFF7D1"), // primary
            Color.decode("#FFECC8"), // secondary
            Color.decode("#FFD09B"), // accent
            Color.decode("#FFB0B0"), // text
            Pulse2D.darker(Color.decode("#FFB0B0"), 1.2f), // muted text
            "Yellow / Peach",
            false,
            0,
            true
    );

    public static ColorScheme DARKER_MONO = new ColorScheme(
            Color.decode("#121212"), // primary
            Pulse2D.darker(Color.decode("#121212"), 2.2f), // secondary
            Pulse2D.darker(Color.decode("#F5F5F5"), 0.7f), // accent
            Color.decode("#F5F5F5"), // text
            Pulse2D.darker(Color.decode("#F5F5F5"), 0.6f), // muted text
            "Darker Mono",
            false,
            0,
            false
    );

    public static ColorScheme DARKER_PINK = new ColorScheme(
            Color.decode("#121212"), // primary
            Pulse2D.darker(Color.decode("#121212"), 2.2f), // secondary
            Pulse2D.darker(Color.decode("#db88cb"), 0.7f), // accent
            Color.decode("#F5F5F5"), // text
            Pulse2D.darker(Color.decode("#F5F5F5"), 0.6f), // muted text
            "Darker Pink",
            false,
            0,
            false
    );

    public static ColorScheme DARKER_BLUE = new ColorScheme(
            Color.decode("#121212"), // primary
            Pulse2D.darker(Color.decode("#121212"), 2.2f), // secondary
            Pulse2D.darker(Color.decode("#85bff2"), 0.7f), // accent
            Color.decode("#F5F5F5"), // text
            Pulse2D.darker(Color.decode("#F5F5F5"), 0.6f), // muted text
            "Darker Blue",
            false,
            0,
            false
    );

    public static ColorScheme DARKER_RED = new ColorScheme(
            Color.decode("#121212"), // primary
            Pulse2D.darker(Color.decode("#121212"), 2.2f), // secondary
            Pulse2D.darker(Color.decode("#ff122a"), 0.7f), // accent
            Color.decode("#F5F5F5"), // text
            Pulse2D.darker(Color.decode("#F5F5F5"), 0.6f), // muted text
            "Darker Blue",
            false,
            0,
            false
    );

    public static ColorScheme DEFAULT = DARKER_PINK;

    public static Color FRIEND = new Color(113, 239, 113);
    public static Color RAGE = new Color(243, 72, 72);
}
