package xyz.qweru.pulse.client.systems.config.impl;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import xyz.qweru.pulse.client.systems.config.ConfigLoader;
import xyz.qweru.pulse.client.systems.player.PlayerSystem;

import java.nio.file.Path;

public class PlayerConfigLoader extends ConfigLoader {
    private final PlayerSystem system;

    public PlayerConfigLoader(Path filepath, PlayerSystem system) {
        super(filepath);
        this.system = system;
    }

    @Override
    public NbtList getTargetData() {
        NbtList nbtList = new NbtList();

        for (String player : system.getPlayers()) {
            nbtList.add(NbtString.of(player));
        }

        return nbtList;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void parseSaveData(NbtCompound data) {
        for (NbtElement element : ((NbtList) data.get("data"))) {
            system.addPlayer(element.asString());
        }
    }

    @Override
    public String getPrefix() {
        return "PlayerConfig";
    }
}
