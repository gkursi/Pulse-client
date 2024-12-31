package xyz.qweru.pulse.client.systems.modules.impl.world;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;
import xyz.qweru.pulse.client.utils.player.SlotUtil;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import java.util.ArrayList;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class ReverseNuker extends ClientModule {

    NumberSetting bpt = numberSetting()
            .name("Blocks per tick")
            .description("Blocks per tick")
            .range(0, 10)
            .defaultValue(1)
            .stepFullNumbers()
            .build();

    NumberSetting range = numberSetting()
            .name("Range")
            .description("place range")
            .range(0, 10)
            .defaultValue(1)
            .stepFullNumbers()
            .build();

    BooleanSetting select = booleanSetting()
            .name("Select")
            .description("automatically select item")
            .build();

    BooleanSetting tower = booleanSetting()
            .name("Tower")
            .description("automatically vclip after finishing block placements")
            .build();


    public ReverseNuker() {
        builder()
                .name("Reverse Nuker")
                .description("Fill air with blocks")
                .settings(select, bpt, range, tower)
                .category(Category.WORLD);
    }

    boolean fin = false;
    @EventHandler
    void t(WorldTickEvent.Post e) {
        List<BlockPos> posA = getBlocks();
        for (int i = 0; i < bpt.getValueInt(); i++) {
            if(posA.isEmpty()) {
                if(!fin) {
                    if(tower.isEnabled()) {
                        PacketUtil.sendMove(mc.player.getPos().add(0, range.getValue() + 1, 0));
                        mc.player.setPosition(mc.player.getPos().add(0, range.getValue() + 1, 0));
                    }
                    fin = true;
                }
                return;
            }
            fin = false;
            BlockPos pos = posA.remove(0);
            if(select.isEnabled()) SlotUtil.runWithItemFilter((slot, inventory) -> {
                PlayerUtil.placeBlock(new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
            }, stack -> stack.getItem() instanceof BlockItem, true);
            else
                PlayerUtil.placeBlock(new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
        }
    }

    ArrayList<BlockPos> getBlocks() {
        ArrayList<BlockPos> poses = new ArrayList<>();
        BlockUtil.forBlocksInRange((px, py, pz, blockPos) -> {
            if(blockPos.equals(mc.player.getBlockPos()) || blockPos.equals(mc.player.getBlockPos().add(0, 1, 0))) return;
            if(BlockUtil.getBlockAt(blockPos).equals(Blocks.AIR)) poses.add(blockPos);
        }, range.getValue(), mc.player.getPos());
        return poses;
    }

    @Override
    public void enable() {
        super.enable();
        fin = false;
    }
}
