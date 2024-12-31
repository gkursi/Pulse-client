package xyz.qweru.pulse.client.auth.pulse;

import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.auth.pulse.impl.LocalAuthenticator;
import xyz.qweru.pulse.client.auth.pulse.impl.URLAuthenticator;

/**
 * Authenticator
 */
public class PulseAuth {
    static Authenticator authenticator = new URLAuthenticator();
    public static boolean authed = false;
    public static int uid = -1;

    public static void performAuth() {
        String HWID = authenticator.getHWID();
        if(!authenticator.verifyIntegrity()) {
            PulseClient.LOGGER.error("HWID integrity check failed!");
//            System.exit(-402);
        } else if(!authenticator.checkHWID(HWID)) {
            PulseClient.LOGGER.error("HWID {} does not own the client!", HWID);
//            System.exit(-401);
        }

        uid = authenticator.getUID();
        authed = true;
    }
}
