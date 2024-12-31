package xyz.qweru.pulse.client.utils.render.font;

import net.minecraft.util.Identifier;

import java.io.File;

public class Font {
    private final String name;
    private final float size;
    private float offsetY;

    public FontRenderer get() {
        return renderer;
    }

    public float getSize() {
        return size;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public String getName() {
        return name;
    }

    private final FontRenderer renderer;

    public Font(String name, float size) {
        this.renderer = new FontRenderer(name, Identifier.of("pulse", "fonts"), size);
        this.name = name;
        this.size = size;
        this.offsetY = 0;
    }

    public Font(String name, float size, float offsetY) {
        this.renderer = new FontRenderer(name, Identifier.of("pulse", "fonts"), size);
        this.name = name;
        this.size = size;
        this.offsetY = offsetY;
    }

    /**
     * Verdana
     * @param size size
     */
    public Font(float size) {
        this(null, size, 0);
    }
}
