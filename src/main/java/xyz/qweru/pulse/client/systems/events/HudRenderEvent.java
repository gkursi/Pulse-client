package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.client.gui.DrawContext;

public class HudRenderEvent {
    public static class Potion implements ICancellable {
        boolean cancelled = false;

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }

    public static class Vignette implements ICancellable {
        boolean cancelled = false;

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }

    public static class WallOverlay implements ICancellable {
        boolean cancelled = false;

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }

    public static class Hud implements ICancellable {
        public DrawContext getContext() {
            return context;
        }

        private final DrawContext context;
        private final float td;
        boolean cancelled = false;

        public float getTickDelta() {
            return td;
        }

        public Hud(DrawContext context, float td) {
            this.context = context;
            this.td = td;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }
    }
}
