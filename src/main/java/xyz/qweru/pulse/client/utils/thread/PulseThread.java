package xyz.qweru.pulse.client.utils.thread;

import xyz.qweru.pulse.client.PulseClient;

public class PulseThread {
    boolean locked = false;
    Runnable givenTask = null;


    ThreadStack parent;

    Thread selfThread;
    boolean run = true;

    public PulseThread(ThreadStack stack) {
        parent = stack;
    }

    public void giveTask(Runnable givenTask) {
        if(locked){
            throw new RuntimeException("Cannot assign task to a locked thread!");
        }
        this.givenTask = givenTask;
    }

    public void terminate() {
        run = false;
        PulseClient.LOGGER.info("Stopped thread");
    }

    public void begin(int id, String prefix) {
        selfThread = new Thread(() -> {
            PulseClient.LOGGER.info("Started {}Thread-{}", prefix, id);
            while(run) {
                if(givenTask != null) {
                    locked = true;
                    try {
                        givenTask.run();
                    } catch (Exception e) {
                        PulseClient.LOGGER.warn("[id {}] Caught exception while running task!", id);
                        PulseClient.throwException(e);
                    }
                    givenTask = null;
                    locked = false;
                    parent.callback();
                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        selfThread.setName("%sThread-%s".formatted(prefix, id));
        selfThread.start();
    }
}
