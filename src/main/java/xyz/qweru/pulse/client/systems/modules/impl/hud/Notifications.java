package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.render.ui.gui.screens.HudConfigScreen;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;

import java.awt.*;
import java.util.Iterator;
import java.util.Stack;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Notifications extends HudModule {

    public Notifications() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(100, 100)
                .getBuilder()
                .name("Notifications")
                .description("Show client notifications in the hud instead of chat")
                .category(Category.HUD);
    }

    Stack<Notification> notifications = new Stack<>();

    int MAX = 4;
    float MAX_LIFE = 3000;

    TimerUtil t = new TimerUtil();
    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        float td = t.getFromLast();
        t.reset();

        if(mc.currentScreen instanceof HudConfigScreen) {
            drawContext.drawBorder((int) x, (int) y, (int) width, (int) height, ThemeInfo.COLORSCHEME.getBorderColor().getRGB());
        }

        if(notifications.size() > MAX) notifications.setSize(3);

        Iterator<Notification> iterator = notifications.iterator();

        int i = 0;
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            notification.life.addAndGet(td);
            if(notification.life.get() > MAX_LIFE) iterator.remove();
            else {
                double nX = notification.life.get() >= (0.2 * MAX_LIFE) ? x : (x > (double) context.screenWidth() / 2 ? (x + width -  width * (notification.life.get() / (0.2 * MAX_LIFE))) : (x - width * (notification.life.get() / (0.2 * MAX_LIFE))));
                double nH = (height / MAX) - 2;
                double nW = width;
                double nY = y + i * nH + 2 * i;

                Pulse2D.drawRound(context.matrixStack(), (float) nX, (float) nY, (float) nW, (float) nH, Pulse2D.cornerRad, ThemeInfo.COLORSCHEME.getBorderColor());

                if(notification.life.get() >= (0.2 * MAX_LIFE) ) {
                    Pulse2D.drawRound(context.matrixStack(), (float) nX, (float) nY, (float) (
                            ((notification.life.get() - MAX_LIFE * 0.2) / (MAX_LIFE * 0.8)) * nW
                    ), (float) 8, Pulse2D.cornerRad, switch (notification.type) {
                        case INFO -> Color.BLUE;
                        case WARN -> Color.ORANGE;
                        case ERROR -> Color.RED;
                    });
                }

                Pulse2D.drawRound(context.matrixStack(), (float) nX + Pulse2D.borderWidth, (float) nY + Pulse2D.borderWidth,
                        (float) nW - Pulse2D.borderWidth * 2, (float) nH - Pulse2D.borderWidth * 2, Pulse2D.cornerRad, ThemeInfo.COLORSCHEME.PRIMARY());

                RenderUtil.textRenderer.drawString(context.matrixStack(), notification.title, nX + Pulse2D.borderWidth + 2,
                        nY + Pulse2D.borderWidth + 0.5 + RenderUtil.fontOffsetY, ThemeInfo.COLORSCHEME.TEXT().getRGB());

                RenderUtil.textRenderer.drawString(context.matrixStack(), notification.text, nX + Pulse2D.borderWidth + 1,
                        nY + Pulse2D.borderWidth + RenderUtil.textRenderer.getHeight(notification.title) + RenderUtil.fontOffsetY - 1, ThemeInfo.COLORSCHEME.MUTED_TEXT().getRGB());

                i++;
            }
        }

    }

    public static boolean notify(String title, String message, Type type) {
        Notifications n = ((Notifications) ModuleManager.INSTANCE.getItemByClass(Notifications.class));

        if(!n.isEnabled()) return false;

        n.notifications.push(new Notification(new AtomicDouble(0), title, message, type));

        return true;
    }

    record Notification(AtomicDouble life, String title, String text, Type type) {}
    public enum Type {
        INFO,
        WARN,
        ERROR
    }
}
