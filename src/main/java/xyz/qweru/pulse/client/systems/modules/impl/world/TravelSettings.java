package xyz.qweru.pulse.client.systems.modules.impl.world;

import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;

public class TravelSettings extends ClientModule {

    public static NumberSetting travelY = new NumberSetting("Travel Y", "What Y level to travel at", 256f, 1000f, 320f, true);
    public static BooleanSetting shouldRepeatTP = new BooleanSetting("Should repeat TP", "Should teleports be repeated until target is reached", true, true);
    public static BooleanSetting doubleTP = new BooleanSetting("High ping mode", "Tries to make this work for higher ping, will reduce speed for low ping", false, true);

    public TravelSettings() {
        super("Travel", "Settings for the travel command", -1, Category.WORLD);
        builder(this).settings(travelY, shouldRepeatTP, doubleTP);
    }
}

