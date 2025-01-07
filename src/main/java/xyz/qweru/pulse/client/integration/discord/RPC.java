package xyz.qweru.pulse.client.integration.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.utils.annotations.Status;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

@Status.Fixme
public class RPC {
    static Core lib = null;
    static long applicationId = 1262743759012106332L;
    static Long start_time = System.currentTimeMillis() / 1000;
    static MinecraftClient mc = MinecraftClient.getInstance();
    static Timer t = new Timer();

    public static void init() {
        // Initialize the Core
        try {
            Core.initDownload();
            PulseClient.LOGGER.info("Initialized RPC!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CreateParams params = new CreateParams();
        params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
        params.setClientID(applicationId);
        // Create the Core
        try(Core core = new Core(params))
        {
            lib = core;
        }

        updatePresence();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updatePresence();
            }
        }, 1500, 1500);
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                {
                    lib.runCallbacks();
                }
            }
        }, 0, 20);
    }

    private static void updatePresence() {
        PulseClient.LOGGER.debug("Updated rpc!");
        try(Activity activity = new Activity())
        {
            activity.timestamps().setStart(Instant.ofEpochMilli(start_time));
            activity.assets().setLargeImage("pulse_client");
            activity.assets().setLargeText("Pulse Client");
            if (mc.world != null) {
                boolean isSinglePlayer = mc.isInSingleplayer();
                DimensionType dimType = mc.world.getDimension();
                Identifier dimKey = mc.world.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).getId(dimType);
                activity.setInstance(true);
                if (!isSinglePlayer) {
                    activity.setState("Multiplayer");
                    if(mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null && mc.getNetworkHandler().getServerInfo().players != null) {
                        activity.party().size().setMaxSize(mc.getNetworkHandler().getServerInfo().players.max());
                        activity.party().size().setCurrentSize(mc.getNetworkHandler().getServerInfo().players.online());
                    }
                } else {
                    activity.setDetails("Singleplayer");
                    activity.party().size().setMaxSize(1);
                    activity.party().size().setCurrentSize(1);
                }
                if (DimensionTypes.THE_NETHER_ID.equals(dimKey)) {
                    activity.setState("In The Nether");
                } else if (DimensionTypes.THE_END_ID.equals(dimKey)) {
                    activity.setState("In The End");
                } else {
                    activity.setState("In The Overworld");
                }

            } else {
                activity.setDetails("In the main menu");
            }

            // Finally, update the current activity to our activity
            if(lib == null) return;
            lib.activityManager().updateActivity(activity);
        }
    }

    public static void shutdown() {}
}