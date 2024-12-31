package xyz.qweru.pulse.client.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadManager {
    /**
     * For meteor's microsoft auth
     */
    public static ExecutorService executor;

    /**
     * Use this for shorter tasks (e.g. crystal aura place)
     */
    public static ExecutorService cachedPool = Executors.newCachedThreadPool();

    /**
     * Use this for longer tasks (e.g. Discord RPC)
     */
    public static ExecutorService fixedPool = Executors.newFixedThreadPool(6);

    public static void stopAll() {
        cachedPool.shutdown();
        fixedPool.shutdown();
    }

    // meteor skid
    static {
        AtomicInteger threadNumber = new AtomicInteger(1);

        executor = Executors.newCachedThreadPool((task) -> {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.setName("Pulse-Executor-" + threadNumber.getAndIncrement());
            return thread;
        });
    }
}
