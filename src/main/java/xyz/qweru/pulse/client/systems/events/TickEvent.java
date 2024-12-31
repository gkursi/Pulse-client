package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;

public class TickEvent {
    public static class Pre implements ICancellable {
        private boolean cancelled;

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }
    }

    public static class Post implements ICancellable {
        private boolean cancelled;

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }
    }
}
