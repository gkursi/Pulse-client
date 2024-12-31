package xyz.qweru.pulse.client.utils.timer;

public class TickTimer {

    TimerUtil timer = new TimerUtil();
    int ticks = 0;
    public TickTimer() {
        timer.reset();
    }

    public void updateTicks() {
        ticks = (int) (timer.getFromLast() / 50);
    }

    public boolean hasReached(float tick) {
        updateTicks();
        return ticks >= tick;
    }

    public int getTicks() {
        updateTicks();
        return ticks;
    }

    public void reset() {
        ticks = 0;
        timer.reset();
    }
}
