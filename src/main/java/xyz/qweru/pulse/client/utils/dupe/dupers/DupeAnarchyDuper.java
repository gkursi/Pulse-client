package xyz.qweru.pulse.client.utils.dupe.dupers;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import xyz.qweru.pulse.client.utils.dupe.Duper;
import xyz.qweru.pulse.client.utils.player.ChatUtil;

public class DupeAnarchyDuper implements Duper {
    @Override
    public boolean dupe(Item item, int count) {
        return dupe(item2id(item), count);
    }

    @Override
    public boolean dupe(String item, int count) {
        ChatUtil.sendServerMsg("/dupe " + item + " " + count);
        return true;
    }

    @Override
    public boolean dupeMax(Item item) {
        return dupeMax(item2id(item));
    }

    @Override
    public boolean dupeMax(String item) {
        return dupe(item, 64);
    }

    String item2id(Item item) {
        String id = Registries.ITEM.getId(item).toString();
        return id.split(":")[1];
    }
}
