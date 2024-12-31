package xyz.qweru.pulse.client.utils;

import java.util.ArrayList;
import java.util.List;

public class PulseArrayList<T> extends ArrayList<T> {

    public PulseArrayList(List<T> strings) {
        super(strings);
    }

    public int getModCount() {
        return modCount;
    }

}
