package xyz.qweru.pulse.client.utils.timer.matrix;

public interface TimerRunnable {
    void run(long ms, long msFromStart, int sectionIndex);
}
