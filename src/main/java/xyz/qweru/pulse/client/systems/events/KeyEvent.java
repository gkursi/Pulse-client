package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;

public class KeyEvent implements ICancellable {
    private boolean cancelled;
    private int key;
    private int action;

    public KeyEvent(int key, int action) {
        this.key = key;
        this.action = action;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public int getKey() { return key; }
    public int getAction() { return action; }
}
