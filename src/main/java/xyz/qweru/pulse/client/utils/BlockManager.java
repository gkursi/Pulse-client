package xyz.qweru.pulse.client.utils;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.events.HandlePacketEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

/**
 * More effective than modules cheking blockstates every tick
 */
public class BlockManager {
    HashMap<BlockPos, BlockState> blocks = new HashMap<>();

    public static BlockManager INSTANCE = new BlockManager();
    private BlockManager() {}

    public void init() {
        PulseClient.Events.subscribe(this);
    }

    public boolean isAir(BlockPos pos) {
        if(mc.world.getBlockState(pos).isAir()) return true;
        else return blocks.get(pos).isAir();
    }

    List<BlockCallback> once = new ArrayList<>();
    List<BlockCallback> callbacks = new ArrayList<>();

    @EventHandler
    void packet2C(HandlePacketEvent e) {
        // fixme comodification exception
//        if(e.getPacket() instanceof BlockUpdateS2CPacket p) {
//            blocks.put(p.getPos(), p.getState());
//            LOGGER.debug("Running callbacks: {} permanent, {} temporary", callbacks.size(), once.size());
//            callbacks.forEach(callback -> callback.run(p.getPos(), p.getState()));
//            once.forEach(callback -> callback.run(p.getPos(), p.getState()));
//        } else if(e.getPacket() instanceof BlockBreakingProgressS2CPacket p) {
//            if(p.getProgress() >= 10) blocks.put(p.getPos(), Blocks.AIR.getDefaultState());
//        } else {
//            LOGGER.debug("Unknown clientbound packet: {}", e.getPacket().toString());
//        }
    }

    @EventHandler
    void t(WorldTickEvent.Post ignored) {
//        LOGGER.debug("Cleared {} temporary events", once.size());
        once.clear();
    }

    public void registerOnce$onBlockAir(BlockCallback callback) {
        once.add(callback);
    }

    public void register$onBlockAir(BlockCallback callback) {
        callbacks.add(callback);
    }

    @FunctionalInterface
    public interface BlockCallback {
        void run(BlockPos pos, BlockState state);
    }
}
