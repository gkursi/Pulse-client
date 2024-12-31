package xyz.qweru.pulse.client.auth.pulse.impl;

import xyz.qweru.pulse.client.auth.pulse.Authenticator;
import xyz.qweru.pulse.client.utils.PulseArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * Temp auth
 */
public class LocalAuthenticator implements Authenticator {
    PulseArrayList<String> hwids = new PulseArrayList<>(List.of(new String[]{""}));

    @Override
    public boolean checkHWID(String HWID) {
        return hwids.contains(HWID);
    }

    @Override
    public String getHWID() {
        return "";
    }

    @Override
    public boolean verifyIntegrity() {
        return hwids.getModCount() == 0;
    }

    @Override
    public int getUID() {
        return 0;
    }
}
