package xyz.qweru.pulse.client.managers;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Manager<T> {
    protected List<T> itemList = new ArrayList<>();
    protected String name;

    public Manager(String name) {
        this.name = name;
    }

    public void init() {}

    public void addItem(T item) {
        itemList.add(item);
    }

    public @Nullable T getItemByClass(Class<? extends T> clazz) {
        for (T t : itemList) {
            if(t.getClass().equals(clazz)) {
                return t;
            }
        }
        return null;
    }

    public List<T> getItemList() {
        return itemList;
    }
}
