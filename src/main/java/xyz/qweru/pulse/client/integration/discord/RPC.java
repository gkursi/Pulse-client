package xyz.qweru.pulse.client.integration.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.utils.annotations.Status;

import java.time.Instant;

/**
 * fixme
 */
@Status.Fixme
public class RPC {

    public static Core CORE = null;

    public static void thread_rpc() {
        PulseClient.LOGGER.info("Loading discord RPC");
        try(CreateParams params = new CreateParams())
        {
            params.setClientID(1262743759012106332L);
            params.setFlags(CreateParams.getDefaultFlags());
            PulseClient.LOGGER.info("Set parameters");

            try(Core core = new Core(params))
            {
                CORE = core;
                try(Activity activity = new Activity())
                {
                    activity.setDetails("%s %s".formatted(PulseClient.NAME, PulseClient.VERSION));
                    activity.setState(":3");

                    // Setting a start time causes an "elapsed" field to appear
                    activity.timestamps().setStart(Instant.now());

                    activity.party().size().setMaxSize(9);
                    activity.party().size().setCurrentSize(6);

                    // Make a "cool" image show up
                    activity.assets().setLargeImage("pulse_icon");

                    // Finally, update the current activity to our activity
                    core.activityManager().updateActivity(activity);
                    PulseClient.LOGGER.info("Created and updated activity");
                } catch (Exception e) {
                    PulseClient.LOGGER.error("[stage: ACTIVITY] Error while running discord RPC!! {}: {}", e.getCause(), e.getMessage());
                }
                PulseClient.LOGGER.info("Finished login, started callback loop.");

                while(true)
                {
                    core.runCallbacks();
                    try
                    {
                        // Sleep a bit to save CPU
                        Thread.sleep(16);
                    }
                    catch(InterruptedException e)
                    {
                        PulseClient.throwException(e);
                    }
                }
            } catch (Exception e) {
                PulseClient.LOGGER.error("[stage: CORE] Error while running discord RPC!! {}: {}", e.getCause(), e.getMessage());
            }
        } catch (Exception e) {
            PulseClient.LOGGER.error("[stage: PARAMS] Error while running discord RPC!! {}: {}", e.getCause(), e.getMessage());
        }
        PulseClient.LOGGER.error("[stage: ???] Error while running discord RPC!! (stopped?)");
    }

    public static void second() {

    }
}
