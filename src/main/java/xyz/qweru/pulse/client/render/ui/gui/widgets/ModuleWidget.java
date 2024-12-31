package xyz.qweru.pulse.client.render.ui.gui.widgets;

import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.gui.Widget;
import xyz.qweru.pulse.client.render.ui.gui.screens.ModuleScreen;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ClickGUI;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.annotations.Status;
import xyz.qweru.pulse.client.utils.render.AnimationUtil;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class ModuleWidget extends Widget {
    public ClientModule module;
    public boolean expanded = false;

    public ModuleWidget(float x, float y, float w, float h, ClientModule module) {
        super(x, y, w, h);
        this.module = module;
    }

    Pulse2D.GradientRect btnColor = Pulse2D.GradientRect.of(Color.WHITE);

    AnimationUtil hoverAnim = new AnimationUtil(0, 450, true);
    boolean animateHover = false;
    boolean prevHover = false;
    boolean prevEnabled = false;

    boolean hoverInit = false;

    @Override
    @Status.MarkedForCleanup
    public void render(RenderContext context) {
        if(prevListening) prevListening = false;
        double mouseX = context.mouseX();
        double mouseY = context.mouseY();

        if(!hoverInit) {
            hoverInit = hoverAnim.hasEnded();
        }

        hovered = RenderUtil.isInside(mouseX, mouseY, x, y, x + w, y + h);
        btnColor = getFill(context);
        Pulse2D.drawRound(context.matrixStack(), x + Pulse2D.borderWidth + 1, y + Pulse2D.borderWidth, w - Pulse2D.borderWidth*2 - 2,
                h - Pulse2D.borderWidth*2, Pulse2D.cornerRad, btnColor);
//        if(hovered && ClickGUI.hoverMode.is("hollow")) Pulse2D.drawRound(context.matrixStack(), x + Pulse2D.borderWidth + 1 + 1, y + Pulse2D.borderWidth + 1, w - Pulse2D.borderWidth*2 - 2 - 2,
//                h - Pulse2D.borderWidth*2 - 2, Pulse2D.cornerRad, getFill(context));

        if(module.isEnabled() || prevEnabled) {
            Pulse2D.drawRound(context.matrixStack(), x + Pulse2D.borderWidth + 1 + 1, y + Pulse2D.borderWidth + 1, w - Pulse2D.borderWidth*2 - 2 - 2,
                    h - Pulse2D.borderWidth*2 - 2, Pulse2D.cornerRad, getFill(context));
        } else if(hovered && hoverInit) {
            Pulse2D.GradientRect rect = new Pulse2D.GradientRect(btnColor.c1(), btnColor.c2(), context.colorScheme().PRIMARY(), Pulse2D.darker(hoverAnim.getColor(context.colorScheme().PRIMARY(), btnColor.c4()), 0.85f));
            Pulse2D.drawRound(context.matrixStack(), x + Pulse2D.borderWidth + 1, y + Pulse2D.borderWidth, w - Pulse2D.borderWidth*2 - 2,
                    h - Pulse2D.borderWidth*2, Pulse2D.cornerRad, rect);
        } else if(animateHover && hoverInit) {
            Pulse2D.GradientRect rect =
                    new Pulse2D.GradientRect(hoverAnim.getColor(btnColor.c1(),
                            context.colorScheme().PRIMARY()), hoverAnim.getColor(btnColor.c2(),
                            context.colorScheme().PRIMARY()), context.colorScheme().PRIMARY(),
                            hoverAnim.getColor(Pulse2D.darker(hoverAnim.getColor(context.colorScheme().PRIMARY(),
                                    btnColor.c1()), 0.85f), context.colorScheme().PRIMARY()));

            Pulse2D.drawRound(context.matrixStack(), x + Pulse2D.borderWidth + 1, y + Pulse2D.borderWidth, w - Pulse2D.borderWidth*2 - 2,
                    h - Pulse2D.borderWidth*2, Pulse2D.cornerRad, rect);
        }

        FontRenderer.ColoredString string;
        if(listening) {
            string = FontRenderer.ColoredString.of("Listening...", context.colorScheme().TEXT());
        } else {
            string = FontRenderer.ColoredString.of(module.getName(), context.colorScheme().TEXT());
            if(module.getBind() != InputUtil.KEY_UNKNOWN) string.add(" [" + InputUtil.getKey(module.getBind()) + "]", context.colorScheme().MUTED_TEXT());
        }

        RenderUtil.textRenderer.drawColoredString(context.matrixStack(), string, x + 2 + 2, y + RenderUtil.fontOffsetY);
        if(hovered) {
            RenderUtil.textRenderer.drawString(context.matrixStack(), module.getDescription(), 2, context.screenHeight() - 2 - RenderUtil.textRenderer.getStringHeight(module.getDescription(), false), context.colorScheme().TEXT().getRGB());
        }

        if(((!prevHover && hovered) || (prevHover && !hovered)) && hoverInit) {
            hoverAnim.reset();
        }

        animateHover = hovered || !hoverAnim.hasEnded();
        prevEnabled = module.isEnabled() || !animation.hasEnded();
        prevHover = hovered;
    }

    public boolean listening = false;
    boolean prevListening = false;
    @Override
    public void mouseInput(int key, int action, double mouseX, double mouseY) {
        if(action == GLFW.GLFW_PRESS && RenderUtil.isInside(mouseX, mouseY, x, y, x + w, y + h)) {
            if(key == GLFW.GLFW_MOUSE_BUTTON_1) {
                module.toggle();
                animation.reset();
            }
            else if(key == GLFW.GLFW_MOUSE_BUTTON_2) {
                mc.setScreen((
                        (ModuleScreen) PulseClient.INSTANCE.windowManager.getItemByClass(ModuleScreen.class)).initModule(module));
//                PulseClient.LOGGER.info("RClick");
//                expanded =! expanded;
            } else if(key == GLFW.GLFW_MOUSE_BUTTON_3) {
                listening = true;
            }
         }
    }

    AnimationUtil animation = new AnimationUtil(0, 450);
    Pulse2D.GradientRect getFill(RenderContext context) {
        Color color = ClickGUI.enabledColor.is("Secondary") ? context.colorScheme().SECONDARY() : context.colorScheme().ACCENT();
        if(!hoverInit && !module.isEnabled()) return Pulse2D.GradientRect.of(context.colorScheme().PRIMARY());
        else if(animateHover) {
            Pulse2D.GradientRect prevRect = new Pulse2D.GradientRect(color, color, context.colorScheme().PRIMARY(), Pulse2D.darker(hoverAnim.getColor(context.colorScheme().PRIMARY(), color), 0.85f));

            if(!module.isEnabled()) {
                return new Pulse2D.GradientRect(
                        animation.getColor(color, prevRect.c1()),
                        animation.getColor(color, prevRect.c2()),
                        animation.getColor(color, prevRect.c3()),
                        animation.getColor(color, prevRect.c4())
                );
            }

            return new Pulse2D.GradientRect(
                    animation.getColor(prevRect.c1(), color),
                    animation.getColor(prevRect.c2(), color),
                    animation.getColor(prevRect.c3(), color),
                    animation.getColor(prevRect.c4(), color)
            );
        } else {
            if(module.isEnabled()) return Pulse2D.GradientRect.of(animation.getColor(context.colorScheme().PRIMARY(), color));
            else return Pulse2D.GradientRect.of(animation.getColor(color, context.colorScheme().PRIMARY()));
        }
    }

    @Override
    public void input(int keycode, int scancode, int action) {
        if(listening) {
            if(keycode == InputUtil.KEY_ESCAPE) module.setBind(-1);
            else module.setBind(keycode);
            listening = false;
            prevListening = true;
        }
        super.input(keycode, scancode, action);
    }

    @Override
    public boolean close() {
        return !prevListening;
    }
}
