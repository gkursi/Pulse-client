package xyz.qweru.pulse.client.utils.thread;

import xyz.qweru.pulse.client.PulseClient;

import java.util.ArrayList;
import java.util.List;

public class ThreadStack {
    List<PulseThread> threadList = new ArrayList<>();
    List<Runnable> taskQueue = new ArrayList<>();

    public ThreadStack(int size, String prefix) {
        for(int i = 0; i<=size; i++) {
            threadList.add(new PulseThread(this));
        }
        int i = 0;
        for (PulseThread pulseThread : threadList) {
            pulseThread.begin(i, prefix);
            i++;
        }
    }

    int recDepth = 0;

    public void queue(Runnable task) {
        if(recDepth > 10) {
            PulseClient.LOGGER.warn("Recursion depth is larger than 10, terminating immediate queue attempt!");
            taskQueue.add(task);
            recDepth = 0;
            return;
        }
        List<PulseThread> idle = getIdleThreads();
        if(idle.isEmpty()) {
            taskQueue.add(task);
            recDepth = 0;
        }
        else try {
            idle.remove(0).giveTask(task);
            recDepth = 0;
        } catch (Exception ignored) {
            recDepth++;
            queue(task);
        }
    }

    /**
     * PulseThread internal callback
     */
    public void callback() {
        List<PulseThread> idle = getIdleThreads();
        if(idle.isEmpty() || taskQueue.isEmpty()) return;
        idle.remove(0).giveTask(taskQueue.remove(0));
    }

    public List<PulseThread> getIdleThreads() {
        List<PulseThread> idleThreads = new ArrayList<>();
        for (PulseThread pulseThread : threadList) {
            if(!pulseThread.locked) idleThreads.add(pulseThread);
        }
        return idleThreads;
    }

    public void stopAll() {
        for (PulseThread pulseThread : threadList) {
            pulseThread.terminate();
        }
    }
}
