package xyz.qweru.pulse.client.mixin.iinterface;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.player.BlockBreakingInfo;

public interface IWorldRenderer {

    Int2ObjectMap<BlockBreakingInfo> pulse$getBlockBreakingInfos();

}
