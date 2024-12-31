package xyz.qweru.pulse.client.utils;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.*;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.modules.impl.world.BoxBreaker;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class Util {
    public static void waitForWorld() {
        while(nullCheck());
    }
    public static boolean nullCheck(MinecraftClient mc) {
        return mc.world == null || mc.player == null;
    }
    public static boolean nullCheck() {
        return nullCheck(MinecraftClient.getInstance());
    }

    public static void delay(Runnable runnable, long ms) {
            new Thread(() -> {
                try {
                    Thread.sleep(ms);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                runnable.run();
            }).start();
    }

    public static void errorPopup(String msg, String titleBar)
    {
        JOptionPane.showMessageDialog(null, msg, "Error: " + titleBar, JOptionPane.ERROR_MESSAGE);
    }

    public static void infoPopup(String msg, String titleBar)
    {
        JOptionPane.showMessageDialog(null, msg, "Info: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public static <T, V> HashMap<T, V> cloneMap(HashMap<T, V> m) {
        return new HashMap<>(m);
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            PulseClient.LOGGER.warn("Sleep interrupted!");
        }
    }

    public static <T> ArrayList<T> cloneArray(List<T> arrayList) {
        return new ArrayList<>(arrayList);
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static boolean equalsAny(Object a, Object... objects) {
        for (Object object : objects) {
            if(a.equals(object)) return true;
        }
        return false;
    }

    public static boolean isUsingItem() {
        return mc.player.isUsingItem() || mc.options.useKey.isPressed();
    }

    public static void logFormattedException(Throwable e) {
        Logger log = PulseClient.LOGGER.getBase();

        log.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error("    -> {}#{}:{}", element.getClassName(), element.getMethodName(), element.getLineNumber());
        }
    }

    public static String orderedTextToString(OrderedText text) {
        StringBuilder result = new StringBuilder();

        text.accept((index, style, codePoint) -> {
            result.appendCodePoint(codePoint);
            return true;
        });

        return result.toString();
    }

    public static MutableText orderedTextToMutableText(OrderedText text) {
        MutableText result = Text.empty();

        text.accept((index, style, codePoint) -> {
            StringBuilder builder = new StringBuilder();
            builder.appendCodePoint(codePoint); // there is probably a better way to do this
            result.append(Text.empty().setStyle(style).append(builder.toString()));
            return true;
        });

        return result;
    }

    public static int getEnchantmentLevel(ItemStack itemStack, RegistryKey<Enchantment> enchantment) {
        if (itemStack.isEmpty()) return 0;
        Object2IntMap<RegistryEntry<Enchantment>> itemEnchantments = new Object2IntArrayMap<>();
        getEnchantments(itemStack, itemEnchantments);
        return getEnchantmentLevel(itemEnchantments, enchantment);
    }

    public static int getEnchantmentLevel(Object2IntMap<RegistryEntry<Enchantment>> itemEnchantments, RegistryKey<Enchantment> enchantment) {
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : Object2IntMaps.fastIterable(itemEnchantments)) {
            if (entry.getKey().matchesKey(enchantment)) return entry.getIntValue();
        }
        return 0;
    }

    public static void getEnchantments(ItemStack itemStack, Object2IntMap<RegistryEntry<Enchantment>> enchantments) {
        enchantments.clear();

        if (!itemStack.isEmpty()) {
            Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> itemEnchantments = itemStack.getItem() == Items.ENCHANTED_BOOK
                    ? itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS).getEnchantmentEntries()
                    : itemStack.getEnchantments().getEnchantmentEntries();

            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantments) {
                enchantments.put(entry.getKey(), entry.getIntValue());
            }
        }
    }

    // probably a threat to security, idc tho
    public static String execCmd(String cmd) {
        try {
            Process p = new ProcessBuilder(cmd.split(" ")).start();
            String stderr = IOUtils.toString(p.getErrorStream(), Charset.defaultCharset());
            String stdout = IOUtils.toString(p.getInputStream(), Charset.defaultCharset());
            return stdout;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//
//        String result = null;
//        try (InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
//             Scanner s = new Scanner(inputStream)) {
//            result = s.hasNext() ? s.next() : null;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
    }
}
