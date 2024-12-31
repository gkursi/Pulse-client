package xyz.qweru.pulse.client.render.ui.gui;

import me.x150.renderer.render.MSAAFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.IGameRenderer;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ColorScheme;
import xyz.qweru.pulse.client.render.ui.color.Colors;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ClickGUI;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.render.AnimationUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class PulseScreen extends Screen {

    protected RenderContext renderContext;
    protected ColorScheme colorScheme = Colors.DEFAULT;

    public String pulse$getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title = "";
    protected boolean blur = true;

    public void setColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public PulseScreen(String title) {
        super(Text.of(""));
        this.title = title;
    }

    protected void addWidget(Widget widget) {
        widgetList.add(widget);
    }

    protected PulseScreen(Text title) {
        super(title);
    }
    /**
     * All widget adding logic should be in the init method, otherwise the screen will break due to how the wm is made
     */
    @Override
    protected void init() {
        widgetList.clear();
    }

    protected static List<Widget> widgetList = new ArrayList<>();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if(ClickGUI.blur.isEnabled() && blur) {
            ((IGameRenderer) mc.gameRenderer).pulse$renderBlur(delta, ClickGUI.blurStrength.getValue(), ClickGUI.blurDirection.getValue());
            mc.getFramebuffer().beginWrite(false);
        }
        renderContext = new RenderContext(context.getMatrices(), context, mouseX, mouseY, context.getScaledWindowWidth(), context.getScaledWindowHeight(), delta, modify(colorScheme), this);

        try {
            if(ClickGUI.MSAASamples.is("Disabled")) {
                widgetList.forEach(widget -> widget.render(renderContext));
                if(ClickGUI.ICON_MSAA.isEnabled()) {
                    MSAAFramebuffer.use(16, Pulse2D.Icons::renderQueue);
                } else Pulse2D.Icons.renderQueue();
            }
            else MSAAFramebuffer.use(Integer.parseInt(ClickGUI.MSAASamples.getCurrent()), () -> {
                widgetList.forEach(widget -> widget.render(renderContext));
                Pulse2D.Icons.renderQueue();
            });
        } catch (Exception e) {
            ClickGUI.MSAASamples.setMode("Disabled");
            ClickGUI.ICON_MSAA.setState(false);
            throw new RuntimeException(e);
        }
    }

    AnimationUtil fadeIn = new AnimationUtil(0, 750);
    ColorScheme modify(ColorScheme s) {
        int alpha = fadeIn.getInt(0, 255);
        return new ColorScheme(
                Pulse2D.injectAlpha(s.PRIMARY(), alpha),
                Pulse2D.injectAlpha(s.SECONDARY(), alpha),
                Pulse2D.injectAlpha(s.ACCENT(), alpha),
                Pulse2D.injectAlpha(s.TEXT(), alpha),
                Pulse2D.injectAlpha(s.MUTED_TEXT(), alpha),
                "(ANIM) " + s.NAME(),
                s.TEXT_SHADOW(),
                s.CORNER_RADIUS(),
                s.light()
        );
    }

    public void reset() {
        widgetList.clear();
        init();
        Util.cloneArray(widgetList).forEach(Widget::open);
        fadeIn.reset();
    }

    @Override
    protected void applyBlur(float delta) {}

    public static class QuickRender {
        Class<? extends PulseScreen> aClass;
        PulseScreen object = null;

        public QuickRender(Class<? extends PulseScreen> clazz) {
            aClass = clazz;
        }

        /**
         * Initial mouseX, mouseY
         */
        int iMouseX, iMouseY;
        /**
         * Initial blur status
         */
        boolean iBlur;

        public void begin(int mouseX, int mouseY) {
            iMouseX = mouseX;
            iMouseY = mouseY;
            try {
                object = PulseClient.INSTANCE.windowManager.getItemByClass(aClass);
                iBlur = object.blur;
                object.blur = false;
            } catch (Exception e) {
                PulseClient.LOGGER.warn("QuickRender > Failed begin call!!");
                PulseClient.throwException(e);
            }
        }

        public void render(DrawContext drawContext, float delta) {
            if(object == null) return;
            object.render(drawContext, iMouseX, iMouseY, delta);
            object.applyBlur(delta);
        }

        public void end() {
            if(object == null) return;
            object.blur = iBlur;
        }

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Iterator<Widget> iterator = Util.cloneArray(widgetList).iterator();
        while (iterator.hasNext()) {
            iterator.next().input(keyCode, scanCode, GLFW.GLFW_PRESS);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        Iterator<Widget> iterator = Util.cloneArray(widgetList).iterator();
        while (iterator.hasNext()) {
            iterator.next().input(keyCode, scanCode, GLFW.GLFW_RELEASE);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Iterator<Widget> iterator = Util.cloneArray(widgetList).iterator();
        while (iterator.hasNext()) {
            iterator.next().mouseInput(button, GLFW.GLFW_PRESS, mouseX, mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Iterator<Widget> iterator = Util.cloneArray(widgetList).iterator();
        while (iterator.hasNext()) {
            iterator.next().mouseInput(button, GLFW.GLFW_RELEASE, mouseX, mouseY);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected boolean shouldClose = true;
    @Override
    public void close() {
        Iterator<Widget> iterator = Util.cloneArray(widgetList).iterator();
        while (iterator.hasNext()) {
            if(!iterator.next().close()) return;
        }
        if(shouldClose) super.close();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        Iterator<Widget> iterator = Util.cloneArray(widgetList).iterator();
        while (iterator.hasNext()) {
            iterator.next().mouseMoved(mouseX, mouseY);
        }
    }
}
