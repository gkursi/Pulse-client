package xyz.qweru.pulse.client.utils.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import xyz.qweru.pulse.client.utils.annotations.Status;

@Status.MarkedForCleanup // make it an orbit event
public interface WindowResizeCallback {
    Event<WindowResizeCallback> EVENT = EventFactory.createArrayBacked(WindowResizeCallback.class, callbacks -> (client, window) -> {
        for (var callback : callbacks) {
            callback.onResized(client, window);
        }
    });

    void onResized(MinecraftClient client, Window window);
}
