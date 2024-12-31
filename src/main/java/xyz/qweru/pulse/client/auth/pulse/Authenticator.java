package xyz.qweru.pulse.client.auth.pulse;

public interface Authenticator {
    boolean checkHWID(String HWID);
    String getHWID();
    boolean verifyIntegrity();
    int getUID();
}
