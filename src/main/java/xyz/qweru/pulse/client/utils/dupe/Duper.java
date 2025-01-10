package xyz.qweru.pulse.client.utils.dupe;

import net.minecraft.item.Item;

public interface Duper {
    boolean dupe(Item item, int count);
    boolean dupe(String item, int count);
    boolean dupeMax(Item item);
    boolean dupeMax(String item);
}
