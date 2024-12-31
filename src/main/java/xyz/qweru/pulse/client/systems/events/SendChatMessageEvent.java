package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;

public class SendChatMessageEvent implements ICancellable {
    private boolean cancelled = false;
    private String message;

    public SendChatMessageEvent(String message) {
        this.message = message;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
