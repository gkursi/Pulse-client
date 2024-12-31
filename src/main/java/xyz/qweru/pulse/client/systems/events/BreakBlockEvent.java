package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.util.math.BlockPos;

public class BreakBlockEvent implements ICancellable {
    public BlockPos getPos() {
        return pos;
    }

    private final BlockPos pos;
    boolean c = false;

    public BreakBlockEvent(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        c = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return c;
    }
}
