package xyz.qweru.pulse.client.render.ui.color;

import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ClickGUI;
import xyz.qweru.pulse.client.systems.modules.impl.setting.HudEditor;

import java.awt.*;

public record ColorScheme(Color PRIMARY, Color SECONDARY, Color ACCENT, Color TEXT, Color MUTED_TEXT, String NAME, boolean TEXT_SHADOW, int CORNER_RADIUS, boolean light) {
    public Color getBorderColor() {
        switch (ClickGUI.borderMode.getCurrent().toLowerCase()) {
            case "none" -> {
                return PRIMARY;
            }
            case "accent" -> {
                return ACCENT;
            }
            case "secondary" -> {
                return SECONDARY;
            }
        }
        return PRIMARY;
    }

    public Color getLabelColor() {
        switch (HudEditor.textColor.getCurrent()) {
            case "Text" -> {
                return TEXT;
            }

            case "Accent" -> {
                return ACCENT;
            }

            case "Secondary" -> {
                return SECONDARY;
            }

            case "Rainbow" -> {
                return Pulse2D.rainbow(10d, 0.4f, 1f, 0.5f);
            }
        }

        return TEXT;
    }
}
