package xyz.qweru.pulse.client.utils.timer;

import meteordevelopment.orbit.EventHandler;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.events.TickEvent;

import java.util.HashMap;

public class TickTask {
    public TickTask() {

    }

    HashMap<Integer, TickRunnable> actions = new HashMap<>();
    long lastActionMS = 0;
    int progress = 0;
    int nextActionTick = 0;

    boolean start = false;
    @EventHandler
    void onTick(TickEvent.Post a) {
        if(!start) return;
        if(progress == nextActionTick) {
            if(!actions.values().iterator().hasNext()) {
                PulseClient.Events.unsubscribe(this);
                start = false;
            } else {
                actions.values().iterator().next().run(lastActionMS - System.currentTimeMillis(), progress);
                progress = 0;
                lastActionMS = System.currentTimeMillis();
                nextActionTick = actions.keySet().iterator().next();
                PulseClient.LOGGER.info("Finished action with delay of {} ticks.", actions.remove(actions.keySet().iterator().next()));
            }
        } else {
            progress++;
        }
    }

    public void addAction(int delay, TickRunnable action) {
        actions.put(delay, action);
    }

    public void begin() {
        PulseClient.Events.subscribe(this);
        lastActionMS = System.currentTimeMillis();
        start = true;
    }

    public static interface TickRunnable {
        void run(long msDelta, int tickDelta);
    }
}
