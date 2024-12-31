package xyz.qweru.pulse.client.systems.modules.impl.world;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.render.world.blocks.FadeOutBlock;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ColorSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;
import xyz.qweru.pulse.client.utils.player.RotationUtil;
import xyz.qweru.pulse.client.utils.player.SlotUtil;
import xyz.qweru.pulse.client.utils.world.BlockUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Scaffold extends ClientModule {

    BooleanSetting render = new BooleanSetting("Render", "adds visuals", true, true);
    BooleanSetting select = new BooleanSetting("Select", "automatically select blocks from hotbar", true, true);
    BooleanSetting silent = new BooleanSetting("Silent", "Silent switch", false, true);
    BooleanSetting rotate = new BooleanSetting("Rotate", "Rotate towards block", false, true);

    ColorSetting color = new ColorSettingBuilder()
            .setName("Color")
            .setDescription("Render color")
            .build();

    public Scaffold() {
        builder(this)
                .name("Scaffold")
                .description("Place's blocks below your feet")
                .settings(render, select, silent, rotate, color)
                .category(Category.WORLD);

        deferEnableToRotationUnlock(rotate.isEnabled());
        rotate.addOnToggle(() -> deferEnableToRotationUnlock(rotate.isEnabled()));
    }

    @EventHandler
    void t(WorldTickEvent.Pre e)  {
        for(int i = 0; i < 10; i++) {
            r();
        }
    }

    void r() {
        BlockPos pos = BlockPos.ofFloored(mc.player.getPos().add(0, mc.options.sneakKey.isPressed() ? -2 : -1, 0));

        if(Util.equalsAny(BlockUtil.getBlockAt(pos), Blocks.AIR, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.WATER, Blocks.LAVA)) {
            if(rotate.isEnabled()) {
                RotationUtil.addRotation(pos.toCenterPos(), () -> {
                    if(select.isEnabled()) SlotUtil.runWithItemFilter((slot, inventory) -> {
                        PlayerUtil.placeBlock(new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
                    }, stack -> stack.getItem() instanceof BlockItem, silent.isEnabled());
                    else
                        PlayerUtil.placeBlock(new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
                });
            } else {
                if(select.isEnabled()) SlotUtil.runWithItemFilter((slot, inventory) -> {
                    PlayerUtil.placeBlock(new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
                }, stack -> stack.getItem() instanceof BlockItem, silent.isEnabled());
                else
                    PlayerUtil.placeBlock(new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
            }
            if(render.isEnabled()) fades.add(new FadeOutBlock(pos, color.getJavaColor(), ThemeInfo.COLORSCHEME.ACCENT().darker(), 450));
        }
    }

    List<FadeOutBlock> fades = new ArrayList<>();
    @EventHandler
    void render(Render3DEvent e) {
        if(!render.isEnabled()) return;
        Iterator<FadeOutBlock> iterator = fades.iterator();
        while (iterator.hasNext()) {
            FadeOutBlock bl = iterator.next();
            if(bl.hasFaded()) iterator.remove();
            else bl.render(e.getMatrixStack());
        }
    }
}
