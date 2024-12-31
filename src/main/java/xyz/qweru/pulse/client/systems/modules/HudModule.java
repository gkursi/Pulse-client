package xyz.qweru.pulse.client.systems.modules;

import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;

@ExcludeModule
public class HudModule extends ClientModule {
    protected double x = 2;
    protected double y = 2;
    protected double width;
    protected double height;
    public boolean dragging = false;
    public int mouseOffsetX = 0;
    public int mouseOffsetY = 0;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public HudModule(String name, String description, int bind, Category category, int x, int y, int width, int height) {
        super(name, description, bind, category);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

    public HudModule() {
        super(null, null, -1, null);
    }

    public void render(DrawContext drawContext, float delta, RenderContext context) {}

    public static HudBuilder hudBuilderOf(HudModule module) { return new HudBuilder(module); }

    public static class HudBuilder {
        HudModule module;
        public HudBuilder() {
            module = new HudModule(null, null, -1, null, 2, 2, -1, -1);
        }

        public HudBuilder(HudModule module) {
            this.module = module;
        }

        public Builder getBuilder() { return builder(module); }
        public HudBuilder pos(int x, int y) { module.x = x; module.y = y; return this; }
        public HudBuilder area(float w, float h) { module.width = w; module.height = h; return this; }

    }
}
