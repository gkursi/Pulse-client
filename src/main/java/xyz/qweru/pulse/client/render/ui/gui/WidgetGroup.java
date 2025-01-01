package xyz.qweru.pulse.client.render.ui.gui;

import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;

public class WidgetGroup extends Widget{
    public List<Widget> widgets = new ArrayList<>();
    private final boolean draggable;

    public WidgetGroup(boolean draggable) {
        super(0, 0, 0, 0);
        this.draggable = draggable;
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

    boolean ctrlPressed = false;
    @Override
    public void input(int keycode, int scancode, int action) {
        super.input(keycode, scancode, action);
        if(keycode == InputUtil.KEY_LEFT_CONTROL) ctrlPressed = action == GLFW.GLFW_PRESS;
        widgets.forEach(widget -> widget.input(keycode, scancode, action));
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if(dragging) this.move(mouseX + initialOffsetX, mouseY + initialOffsetY);
        widgets.forEach(widget -> widget.mouseMoved(mouseX, mouseY));
    }

    boolean dragging = false;
    double initialOffsetX = 0;
    double initialOffsetY = 0;
    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        if(draggable && key == InputUtil.MOUSE_BUTTON_1) {
            if(ctrlPressed && action == GLFW.GLFW_PRESS && RenderUtil.isInside(mouseX, mouseY, x, y, x+w, y+h)) {
                dragging = true;
                initialOffsetX = this.x - mouseX;
                initialOffsetY = this.y - mouseY;
                return;
            }
            else if(dragging && action == GLFW.GLFW_RELEASE) {
                dragging = false;
                return;
            }
        }
        super.mouseInput(key, action, mouseX, mouseY);
        widgets.forEach(widget -> widget.mouseInput(key, action, mouseX, mouseY));
    }

    public void move(double x, double y) {
        widgets.forEach(widget -> {
            double offsetX = widget.x - this.x;
            double offsetY = widget.y - this.y;
            widget.x = (float) (x + offsetX);
            widget.y = (float) (y + offsetY);
        });
        this.x = (float) x;
        this.y = (float) y;
    }
}
