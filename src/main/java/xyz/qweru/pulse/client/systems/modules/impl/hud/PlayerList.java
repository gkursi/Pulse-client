package xyz.qweru.pulse.client.systems.modules.impl.hud;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.render.ui.gui.screens.HudConfigScreen;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.FakePlayer;
import xyz.qweru.pulse.client.utils.render.RenderUtil;
import xyz.qweru.pulse.client.utils.render.font.FontRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.ibm.icu.text.PluralRules.Operand.i;
import static xyz.qweru.pulse.client.PulseClient.COLOR;
import static xyz.qweru.pulse.client.PulseClient.mc;

public class PlayerList extends HudModule {

    BooleanSetting hp = booleanSetting()
            .name("Show health")
            .description("Show health")
            .build();

    BooleanSetting distance = booleanSetting()
            .name("Show distance")
            .description("Show distance")
            .build();

    ModeSetting sortBy = modeSetting()
            .name("Sort by")
            .description("How to sort list")
            .defaultMode("Distance")
            .mode("Health")
            .mode("Name length")
            .mode("None")
            .mode("Distance")
            .build();

    NumberSetting maxSize = numberSetting()
            .name("Max size")
            .description("Max amount of players that can be shown on the list (0 = infinite)")
            .range(0, 50)
            .defaultValue(10)
            .stepFullNumbers()
            .build();

    public PlayerList() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(75, 75)
                .getBuilder()
                .name("PlayerList")
                .description("Shows a list of players and some info about them")
                .settings(hp, distance, sortBy, maxSize)
                .category(Category.HUD);
    }

    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        if(mc.currentScreen instanceof HudConfigScreen) {
            drawContext.drawBorder((int) x, (int) y, (int) width, (int) height, ThemeInfo.COLORSCHEME.getBorderColor().getRGB());
        }

        FontRenderer text = RenderUtil.textRenderer;

        List<PlayerData> players = new ArrayList<>();
        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if(player == mc.player) continue;
            players.add(new PlayerData(player.getGameProfile().getName(), player.getHealth(), mc.player.getPos().distanceTo(player.getPos()), player));
        }

        if(players.size() > maxSize.getValueInt()) {
            players.subList(0, maxSize.getValueInt());
        }

        if(sortBy.is("Distance")) players.sort(Comparator.comparingDouble((PlayerData::distance)));
        else if(sortBy.is("Health")) players.sort(Comparator.comparingDouble((PlayerData::health)));
        else if(sortBy.is("Name length")) players.sort(Comparator.comparingInt(value -> -value.name.length()));

        int i = 0;
        for (PlayerData player : players) {
            FontRenderer.ColoredString string = FontRenderer.ColoredString.of((player.entity instanceof FakePlayer ? "[FAKE] " : "") +player.name, context.colorScheme().TEXT());
            if(hp.isEnabled()) string.add(" %sHP ".formatted(Util.round(player.health, 1)), player.health > 10 ? Color.GREEN : (player.health > 5 ? Color.ORANGE : Color.RED));
            if(distance.isEnabled()) string.add(" %sM ".formatted(Util.round(player.distance, 1)), player.distance > 100 ? context.colorScheme().MUTED_TEXT() : (player.distance > 50 ? Color.ORANGE : Color.RED));

            text.drawColoredString(context.getMatrices(), string, (float) x + 1, (float) (y + (text.getHeight("AA") + 2) * i) + 1 + RenderUtil.fontOffsetY);
            i++;
        }

        width = i * (text.getHeight("AA") + 2);
    }

    record PlayerData(String name, double health, double distance, PlayerEntity entity) {}

}
