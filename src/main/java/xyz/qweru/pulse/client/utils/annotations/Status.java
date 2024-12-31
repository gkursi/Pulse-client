package xyz.qweru.pulse.client.utils.annotations;

// todo: do all the marked things
public class Status {
    public @interface MarkedForCleanup {}
    public @interface MarkedForRemoval {}
    public @interface MarkedForIntegration {
        Class value() default Object.class;
    }
    public @interface Fixme {}
    public @interface MarkedForUpgrade {}

}
