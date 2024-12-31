package xyz.qweru.pulse.client.utils;

import xyz.qweru.pulse.client.PulseClient;

public class ExceptionHandler {

    static PulseUncaughtExceptionHandler exceptionHandler = null;

    public static PulseUncaughtExceptionHandler getExceptionHandler() {
        if(exceptionHandler == null) {
            exceptionHandler = new PulseUncaughtExceptionHandler();
        }

        return exceptionHandler;
    }

    public static class PulseUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            PulseClient.LOGGER.getBase().warn("Exception in {}!", thread.getName());
            Util.logFormattedException(throwable);
        }

    }
}
