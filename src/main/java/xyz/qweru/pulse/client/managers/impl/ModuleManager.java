package xyz.qweru.pulse.client.managers.impl;

import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Manager;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;

import java.util.*;

import static org.reflections.scanners.Scanners.SubTypes;

public class ModuleManager extends Manager<ClientModule> {
    public static ModuleManager INSTANCE = new ModuleManager();

    public ModuleManager() {
        super("Module Manager");
        this.init();
//        PulseClient.Events.subscribe(this);
    }

    @Override
    public void init() {
        Reflections reflections = new Reflections("xyz.qweru.pulse.client.systems.modules.impl");
        Set<Class<?>> subTypes =
                reflections.get(SubTypes.of(ClientModule.class).asClass());
        int i = 0;
        for (Class<?> subType : subTypes) {
            if(subType.isAnnotationPresent(ExcludeModule.class)) continue;
            try {
                ClientModule m = (ClientModule) subType.getConstructors()[0].newInstance();
                i++;
                if(m == null) {
                    PulseClient.LOGGER.warn("Module instance was null!");
                } else addItem((ClientModule) subType.getConstructors()[0].newInstance());
            } catch (Exception e) {
                PulseClient.LOGGER.warn("Error while creating instance of module! (class {})", subType.getName());
            }
        }
    }


    public @Nullable ClientModule getModuleByName(String name) {
        for(ClientModule module : itemList) {
            if(Objects.equals(module.getName(), name)) return module;
        }
        return null;
    }

    public List<ClientModule> getModulesByCategory(Category category) {
        List<ClientModule> r = new ArrayList<>();
        for(ClientModule m : itemList) {
            if(m.getCategory() == category) r.add(m);
        }
        return r;
    }
}
