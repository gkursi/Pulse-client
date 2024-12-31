package xyz.qweru.pulse.client.utils;

import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import java.beans.Expression;

public class QueueUtil {

    private static int onWorldLoadIndex = 0;
    private static int waitIndex = 0;

    public static void onWorldLoad(Runnable runnable) {
        new Thread(() -> {
            Util.waitForWorld();
            runnable.run();
        }, "PulseWorldLoadThread-" + onWorldLoadIndex).start();
        onWorldLoadIndex++;
    }

    public static void runOn(Expression expression, Runnable runnable) {
        ThreadManager.cachedPool.submit(() -> {
            while(!expression.check());
            runnable.run();
        });
    }

    public static interface Expression {
        boolean check();
    }
}
