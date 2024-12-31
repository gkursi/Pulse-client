package xyz.qweru.pulse.client.render.ui.gui;

import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

public abstract class Widget {
    public float x;
    public float y;
    public float w;
    public float h;
    public boolean hovered;

    public Widget(float wx, float wy, float ww, float wh) {
        x=wx;
        y=wy;
        w=ww;
        h=wh;
        hovered = false;
    }

    public void render(RenderContext context) {}
    public void input(int keycode, int scancode, int action) {}
    public void mouseMoved(double mouseX, double mouseY) {
        if(RenderUtil.isInside(mouseX, mouseY, x, y, x+w, y+h)) {
            hovered(mouseX, mouseY);
            hovered = true;
        } else hovered = false;
    }
    public void hovered(double mouseX, double mouseY) {}
    public void mouseInput(int key, int action, double mouseX, double mouseY) {}
    public void addX(float x) {
        this.x += x;
    }
    public void addY(float y) {
        this.y += y;
    }
    public boolean close() {return true;}
    public void open() {}

}
