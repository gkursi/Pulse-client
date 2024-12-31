package xyz.qweru.pulse.client.systems.player;

import net.minecraft.entity.player.PlayerEntity;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.config.impl.PlayerConfigLoader;
import xyz.qweru.pulse.client.utils.thread.ThreadManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerSystem {
    List<String> players = new ArrayList<>();
    PlayerConfigLoader loader;
    public PlayerSystem(String type) {
        loader = new PlayerConfigLoader(PulseClient.CONFIG.resolve(type + "_storage.nbt"), this);
        ThreadManager.fixedPool.submit(loader::load);
    }

    public void shutdown() {
        loader.save();
    }

    public void addPlayer(String player) {
        players.add(player);
    }

    public boolean isPlayerInSystem(String player) {
        return players.contains(player);
    }

    public boolean isPlayerInSystem(PlayerEntity player) {
        return isPlayerInSystem(player.getGameProfile().getName());
    }

    public boolean removePlayer(String player) {
        return players.remove(player);
    }

    public List<String> getPlayers() {
        return players;
    }
}
