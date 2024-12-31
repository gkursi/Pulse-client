package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;

public class InstamineEvent {
    public static class Pre implements ICancellable {
        boolean c = false;

        @Override
        public void setCancelled(boolean b) {
            c = b;
        }

        @Override
        public boolean isCancelled() {
            return c;
        }
    }

    public static class Post implements ICancellable {
        boolean c = false;

        @Override
        public void setCancelled(boolean b) {
            c = b;
        }

        @Override
        public boolean isCancelled() {
            return c;
        }
    }
}
