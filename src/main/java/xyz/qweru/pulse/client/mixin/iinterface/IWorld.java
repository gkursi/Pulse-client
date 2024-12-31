package xyz.qweru.pulse.client.mixin.iinterface;

import net.minecraft.client.network.PendingUpdateManager;

public interface IWorld {
    public PendingUpdateManager pulse$getPendingUpdateManager();
}
