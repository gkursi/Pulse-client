package xyz.qweru.pulse.client.utils;

import net.minecraft.client.network.PlayerListEntry;
import oshi.SystemInfo;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

/**
 * Manager for variables, such as ping, fps, time, etc.
 */
public class VarManger {
    public int PING = -1;
    public int FPS = -1;
    public String SERVER_NAME = "unknown";
    public String TIME$DATE_MONTH_YEAR = "??/??/????";
    public String TIME$MINUTE_HOUR_DATE_MONTH = "??:?? ??. ??";
    public String TIME$FULL = "??:?? ??/??/????";
    public String TIME$MINUTE_HOUR = "??:??";
    public String SONGDATA = "unsupported";
    public String SONGDATA$SONG = "unsupported";
    public String SONGDATA$ARTIST = "unsupported";

    boolean lock = false;
    public void update() {
        if(lock) return;
        ThreadManager.fixedPool.submit(() -> {
            lock = true;
            FPS = mc.getCurrentFps();
            PING = ping();
            SERVER_NAME = serverName();
            TIME$MINUTE_HOUR = getTime$minuteHour();
            TIME$FULL = getTime$full();
            TIME$DATE_MONTH_YEAR = getTime$dateMonthYear();
            TIME$MINUTE_HOUR_DATE_MONTH = getTime$minuteHourDateMonth();
            SONGDATA = song();
            lock = false;
        });
    }

    private String getTime$minuteHour() {
        return getHour() + ":" + getMin();
    }

    private String getTime$full() {
        return getHour() + ":" + getMin() + " " + getTime().getDayOfMonth() + "/" + getTime().getMonthValue() + "/" + getTime().getYear();
    }

    private String getMin() {
        return String.format("%02d", getTime().getMinute());
    }

    private String getHour() {
        return String.format("%02d", getTime().getHour());
    }

    private String getTime$minuteHourDateMonth() {
        return getHour() + ":" + getMin() + " " + getTime().getDayOfMonth() + ". " + getTime().getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }

    private String getTime$dateMonthYear() {
        return getTime().getDayOfMonth() + "/" + getTime().getMonthValue() + "/" + getTime().getYear();
    }

    private LocalDateTime getTime() {
        return LocalDateTime.now();
    }

    private String serverName() {
        if(networkNullCheck()) return "unknown";
        return mc.getNetworkHandler().getConnection().getAddressAsString(true);
    }

    private boolean networkNullCheck() {
        return mc.getNetworkHandler() == null || mc.player == null || mc.getNetworkHandler().getConnection() == null;
    }

    private int ping() {
        if (networkNullCheck()) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return playerListEntry != null ? playerListEntry.getLatency() : 0;
    }

    int counter = 0;
    private String song() {
        // only check every 10 ticks
        if(counter > 0) {
            counter--;
            return SONGDATA;
        } else {
            counter = 10;
        }

        String title = "{{title}}";
        String artist = "{{artist}}";
        String arResult = playerCTL(artist);
        String tResult = playerCTL(title);
        SONGDATA$SONG = tResult;
        SONGDATA$ARTIST = arResult;
        //        LOGGER.debug("Song Result: {}", result);
        return "Now playing " + tResult + " by " + arResult;
    }

    SystemInfo info = null;
    private boolean isSpyware() {
        if(info == null) info = new SystemInfo();
        return info.getOperatingSystem().getFamily().contains("win");
    }

    private String playerCTL(String format) {
        if(isSpyware()) {
            return "unsupported";
        }
        String command = "playerctl metadata --format \"" + format + "\"";
        return Util.execCmd(command).replace("\n", "").replace("\"", "");
    }
}
