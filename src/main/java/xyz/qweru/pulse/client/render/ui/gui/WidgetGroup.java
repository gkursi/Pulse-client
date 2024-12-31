package xyz.qweru.pulse.client.render.ui.gui;

import xyz.qweru.pulse.client.render.renderer.RenderContext;

import java.util.ArrayList;
import java.util.List;

public class WidgetGroup extends Widget{
    public List<Widget> widgets = new ArrayList<>();

    public WidgetGroup() {
        super(0, 0, 0, 0);
    }

    public void add(Widget widget) {
        if(widgets.isEmpty()) {
            x = widget.x;
            y = widget.y;
            w = widget.w;
            h = widget.h;
        } else {
            x = Math.min(x, widget.x);
            y = Math.min(y, widget.y);
            w = Math.max(w, widget.w);
            h = Math.max(h, widget.h);
        }
        widgets.add(widget);
    }

    @Override
    public void render(RenderContext context) {
        super.render(context);
        widgets.forEach(widget -> widget.render(context));
    }

    @Override
    public void input(int keycode, int scancode, int action) {
        super.input(keycode, scancode, action);
        widgets.forEach(widget -> widget.input(keycode, scancode, action));
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        widgets.forEach(widget -> widget.mouseMoved(mouseX, mouseY));
    }

    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        super.mouseInput(key, action, mouseX, mouseY);
        widgets.forEach(widget -> widget.mouseInput(key, action, mouseX, mouseY));
    }
}
