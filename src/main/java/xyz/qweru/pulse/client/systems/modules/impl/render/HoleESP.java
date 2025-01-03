package xyz.qweru.pulse.client.systems.modules.impl.render;

import me.x150.renderer.render.Renderer3d;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.injection.Inject;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ColorSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.HoleUtil;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class HoleESP extends ClientModule {

    BooleanSetting renderHoles = booleanSetting()
            .name("Render holes")
            .description("Should render safe holes")
            .defaultValue(true)
            .shouldShow(true)
            .build();

    ModeSetting holeRenderMode = modeSetting()
            .name("Hole render mode")
            .description("How should holes be rendered")
            .shouldShow(true)
            .defaultMode("Block face")
            .mode("Full block")
            .mode("Block face")
            .build();

    NumberSetting holeOpacity = numberSetting()
            .name("Hole opacity")
            .description("Burrow color fill opacity")
            .defaultValue(15)
            .min(0)
            .max(255)
            .shouldShow(true)
            .build();

    BooleanSetting renderBurrowBlocks = booleanSetting()
            .name("Render burrow blocks")
            .description("Should render safe burrow blocks")
            .defaultValue(false)
            .shouldShow(true)
            .build();

    ModeSetting burrowBlockRenderMode = modeSetting()
            .name("Burrow render mode")
            .description("How should burrow blocks be rendered")
            .shouldShow(false)
            .defaultMode("Full block")
            .mode("Block face")
            .mode("Full block")
            .build();

    NumberSetting burrowRange = numberSetting()
            .name("Burrow range")
            .description("Search range")
            .defaultValue(4)
            .min(0)
            .max(10)
            .shouldShow(true)
            .build();

    NumberSetting holeRange = numberSetting()
            .name("Hole range")
            .description("Search range")
            .defaultValue(4)
            .min(0)
            .max(15)
            .shouldShow(true)
            .build();

    NumberSetting burrowOpacity = numberSetting()
            .name("Burrow opacity")
            .description("Burrow color fill opacity")
            .defaultValue(0)
            .min(0)
            .max(255)
            .shouldShow(true)
            .build();

    ColorSetting unbreakable = new ColorSettingBuilder()
            .setName("Full bedrock color")
            .setDescription("Color used for full bedrock holes / burrow blocks")
            .setColor(Color.GREEN)
            .build();

    ColorSetting mixed = new ColorSettingBuilder()
            .setName("Mixed color")
            .setDescription("Color used for mixed holes / burrow blocks")
            .setColor(Color.ORANGE)
            .build();

    ColorSetting breakable = new ColorSettingBuilder()
            .setName("Full obsidian color")
            .setDescription("Color used for full obsidian holes / burrow blocks")
            .setColor(Color.RED)
            .build();

    BooleanSetting showFull = booleanSetting()
            .name("Render bedrock holes")
            .description("Should render full bedrock holes")
            .defaultValue(true)
            .build();

    BooleanSetting showMixed = booleanSetting()
            .name("Render mixes holes")
            .description("Should render full bedrock holes")
            .defaultValue(true)
            .build();

    BooleanSetting showObby = booleanSetting()
            .name("Render obsidian holes")
            .description("Should render full bedrock holes")
            .defaultValue(true)
            .build();

    public HoleESP() {
        builder(this)
                .name("SafeESP")
                .description("Renders safe holes / burrow blocks")
                .category(Category.RENDER)
                .settings("Holes", renderHoles, holeRenderMode, holeOpacity, holeRange, showFull, showMixed, showObby)
                .settings("Burrow", renderBurrowBlocks, burrowBlockRenderMode, burrowOpacity, burrowRange)
                .settings("Color", unbreakable, mixed, breakable);

        renderHoles.addOnToggle(() -> {
            holeRenderMode.setShouldShow(renderHoles.isEnabled());
        });
        renderBurrowBlocks.addOnToggle(() -> {
            burrowBlockRenderMode.setShouldShow(renderBurrowBlocks.isEnabled());
        });
    }

    int safeBlockLevel(BlockPos pos) {
        if(BlockUtil.getBlockAt(pos).equals(Blocks.BEDROCK) || BlockUtil.getBlockAt(pos).equals(Blocks.REINFORCED_DEEPSLATE)) {
            return 2;
        } else if(BlockUtil.getBlockAt(pos).equals(Blocks.OBSIDIAN)) {
            return 1;
        } else return 0;
    }

    @EventHandler
    private void render3d(Render3DEvent event) {
        Renderer3d.renderThroughWalls();
        if(renderBurrowBlocks.isEnabled()) {
            BlockUtil.forBlocksInRange((x, y, z, pos) -> {
                int safetyLevel = safeBlockLevel(pos);
                int safetyLevelBelow = safeBlockLevel(pos.subtract(new Vec3i(0, 1, 0)));
                int opacity = (int) this.burrowOpacity.getValue();
                if(safetyLevel > 0 && safetyLevelBelow > 0) {
                    final Color colorFill = safetyLevel + safetyLevelBelow > 3 ? Pulse2D.injectAlpha(unbreakable.getJavaColor(), opacity) :
                            (safetyLevelBelow + safetyLevel == 2 ? Pulse2D.injectAlpha(breakable.getJavaColor(), opacity) : Pulse2D.injectAlpha(mixed.getJavaColor(), opacity));
                    final Color colorEdge = Pulse2D.injectAlpha(colorFill.darker(), 255);
                    switch (burrowBlockRenderMode.getCurrent().toLowerCase()) {
                        case "full block" -> {
                            Renderer3d.renderEdged(event.getMatrixStack(),
                                    colorFill,
                                    colorEdge,
                                    new Vec3d(pos.getX(), pos.getY(), pos.getZ()),
                                    new Vec3d(1, 1, 1)
                            );
                        }
                        case "block face" -> {
                            Renderer3d.renderEdged(event.getMatrixStack(),
                                    colorFill,
                                    colorEdge,
                                    new Vec3d(pos.getX(), pos.getY() + 0.99, pos.getZ()),
                                    new Vec3d(1, 0.01, 1));
                        }
                    }
                }
            }, ((int) burrowRange.getValue()));
        }

        if(renderHoles.isEnabled()) {
            for (HoleUtil.Hole hole : HoleUtil.holes) {
                if(hole.safety() == HoleUtil.HoleSafety.UNSAFE || Vec3d.of(hole.air().get(0)).distanceTo(mc.player.getPos()) > holeRange.getValue()) continue;
                Color fill = switch (hole.safety()) {
                    case UNBREAKABLE -> unbreakable.getJavaColor();
                    case PARTIALLY_UNBREAKABLE -> mixed.getJavaColor();
                    case BREAKABLE -> breakable.getJavaColor();
                    default -> throw new IllegalStateException("Unexpected value: " + hole.safety());
                };

                for (BlockPos blockPos : hole.air()) {
                    switch (holeRenderMode.getCurrent().toLowerCase()) {
                        case "full block" -> Renderer3d.renderEdged(event.getMatrixStack(),
                                fill,
                                Pulse2D.injectAlpha(fill.darker(), 255),
                                new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                                new Vec3d(1, 1, 1)
                        );
                        case "block face" -> Renderer3d.renderEdged(event.getMatrixStack(),
                                fill,
                                Pulse2D.injectAlpha(fill.darker(), 255),
                                new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()),
                                new Vec3d(1, 0.01, 1));
                    }
                }
            }
        }
    }

    void drawHoleAt(BlockPos pos, int safetyLevel, MatrixStack stack) {
        if(!BlockUtil.getBlockAt(pos.add(0, 1, 0)).equals(Blocks.AIR)) return;

        final Color colorFill = safetyLevel == 3 ? new Color(0, 255, 0, ((int) holeOpacity.getValue())) :
                (safetyLevel <= 1 ? new Color(255, 0, 0, ((int) holeOpacity.getValue())) : new Color(255, 255, 0, ((int) holeOpacity.getValue())));
        final Color colorEdge = safetyLevel == 3 ? Color.GREEN.darker() :
                (safetyLevel <= 1 ? Color.RED.darker() : Color.YELLOW.darker());
        switch (holeRenderMode.getCurrent().toLowerCase()) {
            case "full block" -> Renderer3d.renderEdged(stack,
                    colorFill,
                    colorEdge,
                    new Vec3d(pos.getX(), pos.getY(), pos.getZ()),
                    new Vec3d(1, 1, 1)
            );
            case "block face" -> Renderer3d.renderEdged(stack,
                    colorFill,
                    colorEdge,
                    new Vec3d(pos.getX(), pos.toBottomCenterPos().getY(), pos.getZ()),
                    new Vec3d(1, 0.01, 1));
        }
    }

}
