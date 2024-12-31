package xyz.qweru.pulse.client.systems.modules.impl.render;

import me.x150.renderer.render.Renderer3d;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.world.BlockUtil;

import java.awt.*;

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
            .max(20)
            .shouldShow(true)
            .build();

    NumberSetting holeRange = numberSetting()
            .name("Hole range")
            .description("Search range")
            .defaultValue(4)
            .min(0)
            .max(20)
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

    public HoleESP() {
        builder(this)
                .name("SafeESP")
                .description("Renders safe holes / burrow blocks")
                .category(Category.RENDER)
                .settings("Holes", renderHoles, holeRenderMode, holeOpacity, holeRange)
                .settings("Burrow", renderBurrowBlocks, burrowBlockRenderMode, burrowOpacity, burrowRange);

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
                    final Color colorFill = safetyLevel + safetyLevelBelow > 3 ? new Color(0, 255, 0, opacity) :
                            (safetyLevelBelow + safetyLevel == 2 ? new Color(255, 0, 0, opacity) : new Color(255, 255, 0, opacity));
                    final Color colorEdge = safetyLevel + safetyLevelBelow > 3 ? Color.GREEN.darker() :
                            (safetyLevelBelow + safetyLevel == 2 ? Color.RED.darker() : Color.YELLOW.darker());
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
            }, ((int) holeRange.getValue()));

        }

        // todo: clean up (tf was i smoking when i wrote this)
        if(renderHoles.isEnabled()) {
            BlockUtil.forBlocksInRange((x, y, z, pos) -> {
                int safety = safeBlockLevel(pos);

                if(safety <= 0) return;

                // todo ????
                // one y level below
                int bottomSafetyMX = safeBlockLevel(pos.add(-1, -1, 0));
                int bottomSafetyMZ = safeBlockLevel(pos.add(0, -1, -1));
                int bottomSafetyPX = safeBlockLevel(pos.add(1, -1, 0));
                int bottomSafetyPZ = safeBlockLevel(pos.add(0, -1, 1));

                if(!(bottomSafetyPX > 0 || bottomSafetyMX > 0 || bottomSafetyPZ > 0 || bottomSafetyMZ > 0)) return; // no point in checking the rest of the blocks if these aren't safe

                // same y level as block
                int safetyPXZ = safeBlockLevel(pos.add(1, 0, 1));
                int safetyMXZ = safeBlockLevel(pos.add(-1, 0, -1));
                int safetyPXMZ = safeBlockLevel(pos.add(1, 0, -1));
                int safetyMXPZ = safeBlockLevel(pos.add(-1, 0, 1));
                int safetyP2X = safeBlockLevel(pos.add(2, 0, 0));
                int safetyP2Z = safeBlockLevel(pos.add(0, 0, 2));
                int safetyM2X = safeBlockLevel(pos.add(-2, 0, 0));
                int safetyM2Z = safeBlockLevel(pos.add(0, 0, -2));

                if(check(safetyMXZ) && check(safetyMXPZ) && check(safetyM2X) && BlockUtil.getBlockAt(pos.add(-1, 0, 0)).equals(Blocks.AIR)) {
                    drawHoleAt(pos.add(-1, 0, 0), safetyMXZ, safetyMXPZ, safetyM2X, safety, bottomSafetyMX, event.getMatrixStack());
                }

                if(check(safetyMXZ) && check(safetyM2Z) && check(safetyPXMZ) && BlockUtil.getBlockAt(pos.add(0, 0, -1)).equals(Blocks.AIR)) {
                    drawHoleAt(pos.add(0, 0, -1), safetyMXZ, safetyM2Z, safetyPXMZ, safety, bottomSafetyMZ, event.getMatrixStack());
                }

                if(check(safetyPXMZ) && check(safetyP2X) && check(safetyPXZ) && BlockUtil.getBlockAt(pos.add(1, 0, 0)).equals(Blocks.AIR)) {
                    drawHoleAt(pos.add(1, 0, 0), safetyPXMZ, safetyP2X, safetyPXZ, safety, bottomSafetyPX, event.getMatrixStack());
                }

                if(check(safetyMXPZ) && check(safetyP2Z) && check(safetyPXZ) && BlockUtil.getBlockAt(pos.add(0, 0, 1)).equals(Blocks.AIR)) {
                    drawHoleAt(pos.add(0, 0, 1), safetyPXMZ, safetyP2X, safetyPXZ, safety, bottomSafetyPZ, event.getMatrixStack());
                }

            }, ((int) burrowRange.getValue()));
        }
    }

    boolean check(int c) {
        return c > 0;
    }

    void drawHoleAt(BlockPos pos, int safetyLevel, MatrixStack stack) {
        if(!BlockUtil.getBlockAt(pos.add(0, 1, 0)).equals(Blocks.AIR)) return;

        final Color colorFill = safetyLevel == 3 ? new Color(0, 255, 0, ((int) holeOpacity.getValue())) :
                (safetyLevel <= 1 ? new Color(255, 0, 0, ((int) holeOpacity.getValue())) : new Color(255, 255, 0, ((int) holeOpacity.getValue())));
        final Color colorEdge = safetyLevel == 3 ? Color.GREEN.darker() :
                (safetyLevel <= 1 ? Color.RED.darker() : Color.YELLOW.darker());
        switch (holeRenderMode.getCurrent().toLowerCase()) {
            case "full block" -> {
                Renderer3d.renderEdged(stack,
                        colorFill,
                        colorEdge,
                        new Vec3d(pos.getX(), pos.getY(), pos.getZ()),
                        new Vec3d(1, 1, 1)
                );
            }
            case "block face" -> {
                Renderer3d.renderEdged(stack,
                        colorFill,
                        colorEdge,
                        new Vec3d(pos.getX(), pos.toBottomCenterPos().getY(), pos.getZ()),
                        new Vec3d(1, 0.01, 1));

            }
        }
    }

    void drawHoleAt(BlockPos pos, int safe1, int safe2, int safe3, int safe4, int safe5, MatrixStack stack) {
        int safeGlobal = (safe1 == 2 && safe2 == 2 && safe3 == 2 && safe4 == 2 && safe5 == 2 ? 3 : (
                    safe1 > 1 || safe2 > 1 || safe3 > 1 || safe4 > 1 || safe5 > 1 ? 2 : 1
                ));
        drawHoleAt(pos, safeGlobal, stack);
    }

}
