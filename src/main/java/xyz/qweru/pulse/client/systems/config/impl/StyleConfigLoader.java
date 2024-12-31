package xyz.qweru.pulse.client.systems.config.impl;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.config.ConfigLoader;
import xyz.qweru.pulse.client.systems.modules.Category;

import java.nio.file.Path;

public class StyleConfigLoader extends ConfigLoader {
    public StyleConfigLoader(Path filepath) {
        super(filepath);
    }

    @Override
    public NbtList getTargetData() {
        NbtList data = new NbtList();

        for (Category value : Category.values()) {
            NbtList category = new NbtList();
            category.add(NbtString.of(value.label));
            category.add(NbtString.of(value.visible ? "true" : "false"));
            data.add(category);
        }

        return data;
    }

    @Override
    public void parseSaveData(NbtCompound data) {
        NbtList styleData = (NbtList) data.get("data");

        for (NbtElement styleDat : styleData) {
            Category cat = null;
            for (Category value : Category.values()) {
                if(value.label.equalsIgnoreCase(((NbtList)styleDat).get(0).asString())) {
                    cat = value;
                    break;
                }
            }
            if(cat == null) {
                PulseClient.LOGGER.error("Invalid category name!");
                return;
            }

            cat.visible = Boolean.parseBoolean(((NbtList)styleDat).get(1).asString()); // very readable, ik
        }

    }



    @Override
    public String getPrefix() {
        return "StyleConfig";
    }

}
