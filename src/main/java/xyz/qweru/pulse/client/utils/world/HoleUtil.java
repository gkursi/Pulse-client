package xyz.qweru.pulse.client.utils.world;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import xyz.qweru.pulse.client.utils.Util;

import java.util.ArrayList;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public final class HoleUtil {

    public static @Nullable Hole getHole(BlockPos pos) {
        HoleType type = HoleType.SINGLE;
        List<BlockPos> airList = new ArrayList<>();
        int obsidian = 0;
        int bedrock = 0;
        int air = 0;
        int other = 0;

        if(!isAir(pos) || !(isObby(pos.add(0, -1, 0)) || isBedrock(pos.add(0, -1, 0)))) return null;
        else airList.add(pos);

        for (Direction direction : Direction.values()) {
            BlockPos offsetPos = pos.add(direction.getVector());
            if(direction == Direction.UP) {
                if(!isAir(offsetPos)) return null;
                continue;
            }
            if(isObby(offsetPos)) obsidian++;
            else if(isBedrock(offsetPos)) bedrock++;
            else if(isAir(offsetPos)) {
                if(air != 0 || direction == Direction.DOWN) return null;
                else {
                    air++;
                    int obOrBed = 0;
                    for (Direction dir2 : Direction.values()) {
                        if(dir2 == Direction.UP) {
                            if(!isAir(offsetPos)) return null;
                            continue;
                        }
                        BlockPos offsetPos2 = pos.add(direction.getVector());
                        if(isObby(offsetPos2)) {
                            obsidian++;
                            obOrBed++;
                        } else if(isBedrock(offsetPos2)) {
                            bedrock++;
                            obOrBed++;
                        } else if(!isAir(offsetPos2)) {
                            other++;
                            return null;
                        }
                    }
                    if(obOrBed == 4)  {
                        type = HoleType.DOUBLE;
                        airList.add(offsetPos);
                    }
                }
            } else other++;
        }

        HoleSafety safety;
        if(((air > 0 && type == HoleType.SINGLE) || other > 0) && airList.size() < 2) safety = HoleSafety.UNSAFE;
        else if(obsidian > 0 || bedrock > 0) {
            if(obsidian > 0) {
                if(bedrock > 0) safety = HoleSafety.PARTIALLY_UNBREAKABLE;
                else safety = HoleSafety.BREAKABLE;
            } else safety = HoleSafety.UNBREAKABLE;
        } else safety = HoleSafety.UNSAFE;

        return new Hole(airList, type, safety);
    }

    static boolean isAir(BlockPos p) {
        return mc.world.getBlockState(p).isReplaceable();
    }

    static boolean isObby(BlockPos p) {
        return BlockUtil.getBlockAt(p).equals(Blocks.OBSIDIAN);
    }

    static boolean isBedrock(BlockPos p) {
        return BlockUtil.getBlockAt(p).equals(Blocks.BEDROCK);
    }

    public record Hole(List<BlockPos> air, HoleType type, HoleSafety safety) {}

    public enum HoleType {
        SINGLE,
        DOUBLE,
        QUAD
    }

    public enum HoleSafety {
        UNBREAKABLE,
        PARTIALLY_UNBREAKABLE,
        BREAKABLE,
        UNSAFE // no obsidian or bedrock
    }

    public static List<HoleUtil.Hole> holes = new ArrayList<>();
    public static List<BlockPos> checked = new ArrayList<>();
    public static void tick() {
        if(Util.nullCheck()) return;
        List<BlockPos> checked = new ArrayList<>();
        List<HoleUtil.Hole> holes = new ArrayList<>();
        int range = 15;
        BlockPos initPos = mc.player.getBlockPos();

        for (int x = -range; x < range; x++) {
            for (int y = -range; y < range; y++) {
                for (int z = -range; z < range; z++) {
                    BlockPos pos = initPos.add(x, y, z);
                    if(checked.contains(pos)) continue;
                    HoleUtil.Hole hole = HoleUtil.getHole(pos);
                    if(hole == null) continue;
                    holes.add(hole);
                    checked.addAll(hole.air());
                }
            }
        }

        HoleUtil.holes = holes;
        HoleUtil.checked = checked;
    }


}
