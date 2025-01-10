package xyz.qweru.pulse.client.utils.dupe;

import net.minecraft.item.Item;
import xyz.qweru.pulse.client.utils.dupe.dupers.DupeAnarchyDuper;

public class DupeManager {
    public static DupeManager INSTANCE = new DupeManager();
    private DupeManager() {}
    public Duper current = null;

    public void setCurrent(Dupers current) {
        this.current = current.duper;
    }

    public boolean dupe(Item item, int count) {
        return current.dupe(item, count);
    }

    public boolean dupe(String item, int count) {
        return current.dupe(item, count);
    }

    public enum Dupers {
        DUPE_ANARCHY(new DupeAnarchyDuper()),
        HOP_DUPE(null),
        DUPE_TABLES(null);

        public final Duper duper;
        Dupers(Duper duper) {
            this.duper = duper;
        }
    }
}
