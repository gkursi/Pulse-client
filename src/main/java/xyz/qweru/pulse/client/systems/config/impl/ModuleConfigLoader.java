package xyz.qweru.pulse.client.systems.config.impl;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.systems.config.ConfigLoader;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.Setting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.*;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

/**
 * Expected data struct:
 * "": {
 * 	data: [ <--- nbt list made from an nbt list for each module
 * 		[ <ModuleName>, "<is module enabled (bool)>", "<settingName>::<settingValue>::<settingType (boolean, ...)>", ],
 * 	]
 * }
 */
public class ModuleConfigLoader extends ConfigLoader {
    public ModuleConfigLoader(Path filepath) {
        super(filepath);
    }

    @Override
    public NbtList getTargetData() {
        NbtList modules = new NbtList();
        for (ClientModule m : ModuleManager.INSTANCE.getItemList()) {
            NbtList moduleSettings = new NbtList();
            try {
                moduleSettings.add(NbtString.of(m.getName()));
                moduleSettings.add(NbtString.of(String.valueOf(m.isEnabled())));
                moduleSettings.add(NbtString.of("" + m.getBind()));
                for(Setting setting : m.getSettings()) {
                    if(setting instanceof SeperatorSetting) continue;
                    moduleSettings.add(NbtString.of(settingToString(setting)));
                }
                if(m instanceof HudModule n) {
                    moduleSettings.add(NbtString.of("pos::%s/%s::2dPos".formatted(n.getX(), n.getY())));
                }
            } catch (Exception e) {
                PulseClient.LOGGER.warn("Error while saving config, skipping module!");
                PulseClient.throwException(e);
            }
            modules.add(moduleSettings);
        }
        return modules;
    }

    @Override
    public void parseSaveData(NbtCompound data) {
        NbtList moduleList = (NbtList) data.get("data");
        moduleList.forEach((element) -> {
            NbtList module = (NbtList) element;
//            PulseClient.LOGGER.info("got module");
            List<String> settingList = new ArrayList<>();
            module.forEach((sett) -> settingList.add(sett.asString()));

            ClientModule module1 = ModuleManager.INSTANCE.getModuleByName(settingList.get(0));
            if(module1 == null) {
                PulseClient.LOGGER.warn("Invalid module name! ({})", settingList.get(0));
                return;
            }

            module1.setEnabled(Boolean.parseBoolean(settingList.get(1)));
            module1.setBind(Integer.parseInt(settingList.get(2)));

            settingList.remove(0);
            settingList.remove(0);
            settingList.remove(0);
            if(!settingList.isEmpty()) {
                for(String setStr : settingList) {
                    Setting tS = stringToSetting(setStr, module1);
                    if(tS == null) {
                        if(!(module1 instanceof HudModule)) PulseClient.LOGGER.error("Could not match setting string: {}", setStr);
                    }
                }
            }
        });
    }

    /**
     * String format: name::value::type
     *          (boolean setting) name::state::boolean
     *          (number setting)  name::value::number
     *          (mode setting)    name::currentmode::mode
     *
     */
    public static String settingToString(Setting setting) {
        if (setting instanceof BooleanSetting b) {
            return "%s::%s::%s".formatted(setting.getName(), b.isEnabled(), "boolean");
        } else if (setting instanceof NumberSetting n) {
            return "%s::%s::%s".formatted(setting.getName(), n.getValue(), "number");
        } else if (setting instanceof ModeSetting m) {
            return "%s::%s::%s".formatted(setting.getName(), m.getCurrent(), "mode");
        } else if (setting instanceof TextSetting t) {
            return "%s::%s::%s".formatted(setting.getName(), t.getValue(), "text");
        } else if(setting instanceof ColorSetting c) {
            return "%s::%s;%s;%s;%s;::%s".formatted(setting.getName(), c.getRed().asInt(), c.getGreen().asInt(), c.getBlue().asInt(), c.getAlpha().asInt(), "color");
        }

        return "%s::null::null".formatted(setting.getName());
    }

    public static Setting stringToSetting(String str, ClientModule module) {
        try {
            String[] components = str.split("::");
            Setting setting = module.getSettingByName(components[0]);
            switch (components[2]) {
                case "boolean" -> {
                    BooleanSetting bSetting = (BooleanSetting) setting;
//                    PulseClient.LOGGER.info("[ Parser ] Setting name: {}, type: boolean, state: {}", components[0], components[1]);

                    bSetting.setState(Boolean.parseBoolean(components[1]));
                    return bSetting;
                }
                case "number" -> {
                    NumberSetting numberSetting = (NumberSetting) setting;
//                    PulseClient.LOGGER.info("[ Parser ] Setting name: {}, type: number, value: {}", components[0], components[1]);
                    numberSetting.setCurrentValue(Float.parseFloat(components[1]));
                    return numberSetting;
                }
                case "mode" -> {
                    ModeSetting modeSetting = (ModeSetting) setting;
//                    PulseClient.LOGGER.info("[ Parser ] Setting name: {}, type: mode, mode: {}", components[0], components[1]);
                    modeSetting.setMode(components[1]);
                    return modeSetting;
                }
                case "text" -> {
                    TextSetting textSetting = (TextSetting) setting;
//                    PulseClient.LOGGER.info("[ Parser ] Setting name: {}, type: text, value: {}", components[0], components[1]);
                    textSetting.setValue(components[1]);
                    return textSetting;
                }
                case "2dPos" -> {
                    if(module instanceof HudModule x) {
                        x.setX(Double.parseDouble(components[1].split("/")[0]));
                        x.setY(Double.parseDouble(components[1].split("/")[1]));
//                        PulseClient.LOGGER.warn("Parsed 2dPos for {} and got {} {}", x.getName(), x.getX(), x.getY());
                        return null;
                    } else {
                        PulseClient.LOGGER.error("Non-hud module has hud setting!");
                    }
                } case "color" -> {
                    String[] colorComponents = components[1].split(";");
                    ColorSetting colorSetting = (ColorSetting) setting;
                    colorSetting.setColor(Integer.parseInt(colorComponents[0]), Integer.parseInt(colorComponents[1]), Integer.parseInt(colorComponents[2]), Integer.parseInt(colorComponents[3]));
                    return colorSetting;
                }
                default -> {
                    PulseClient.LOGGER.error("Unknown setting! {}", components[2]);
                    return null;
                }
            }
        } catch (Exception e) {
            PulseClient.LOGGER.error("Error while parsing!");
            PulseClient.throwException(e);
            return null;
        }
        return null;
    }

    @Override
    public String getPrefix() {
        return "ModuleConfig";
    }

    public void quickLoad(Path path) {
        ThreadManager.cachedPool.submit(() -> {
            ChatUtil.sendLocalMsg("Loading config");
            Path prev = this.filepath;
            this.filepath = path;
            Managers.MODULE.getItemList().forEach(clientModule -> clientModule.setEnabled(false));
            load();
            this.filepath = prev;
            mc.reloadResources();
            ChatUtil.sendLocalMsg("Config loaded!");
        });
    }

    public void quickSave(Path path) {
        Path prev = this.filepath;
        this.filepath = path;
        save();
        this.filepath = prev;
    }
}
