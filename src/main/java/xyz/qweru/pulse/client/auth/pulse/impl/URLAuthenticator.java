package xyz.qweru.pulse.client.auth.pulse.impl;

import com.google.common.hash.Hashing;
import it.unimi.dsi.fastutil.Hash;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.auth.pulse.Authenticator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class URLAuthenticator implements Authenticator {

    static String[] getHWIDS(String url) throws IOException {
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        conn.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            builder.append(inputLine);
        in.close();

        String data = builder.toString();
        return data.split(";");
    }

    String[] hwids = null;
    String hwid = "";
    Boolean isValid = null;
    Integer uid = null;

    @Override
    public boolean checkHWID(String HWID) {
        if(isValid != null) return isValid;
        if(hwids == null) {
            try {
                hwids = getHWIDS("https://qweru.xyz/pulse/hwid");
            } catch (Exception e) {
                PulseClient.throwException(e);
                hwids = new String[]{};
            }
        }

        isValid = Arrays.asList(hwids).contains(HWID);
        return isValid;
    }

    @Override
    public String getHWID() {
        if(!hwid.isBlank()) return hwid;
        StringBuilder hwid = new StringBuilder();

        SystemInfo system = new SystemInfo();
        OperatingSystem os = system.getOperatingSystem();

        String vendor = os.getManufacturer();
        String family = os.getFamily();
        HardwareAbstractionLayer hardware = system.getHardware();
        String id = hardware.getProcessor().getProcessorIdentifier().getProcessorID();
        String identifier = hardware.getProcessor().getProcessorIdentifier().getIdentifier();

        hwid.append(hash(vendor + family + id + identifier));

        hwid.append(":");
        for (GraphicsCard graphicsCard : hardware.getGraphicsCards()) {
            hwid.append(hash(graphicsCard.getDeviceId()));
        }

        PulseClient.LOGGER.warn("HWID: {}", hwid.toString());
        this.hwid = hwid.toString();
        return this.hwid;
    }

    // todo
    @Override
    public boolean verifyIntegrity() {
        return true;
    }

    @Override
    public int getUID() {
        if(uid != null) return uid;
        if(!isValid) uid = -1;
        else {
            uid = Arrays.asList(hwids).indexOf(hwid);
        }
        return uid;
    }

    private static String hash(String s) {
        return Hashing.sha256().hashString(s, StandardCharsets.UTF_8).toString();
    }
}
