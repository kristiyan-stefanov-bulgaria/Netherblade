package com.hawolt.mitm.cache;

import com.hawolt.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created: 07/03/2023 20:22
 * Author: Twitter @hawolt
 **/

public class InternalStorage {
    private static final List<StorageWatchdog> list = new ArrayList<>();
    private static final Map<String, String> cache = new HashMap<>();

    public static void registerStorageListener(String k, StorageListener listener) {
        registerStorageListener(false, k, listener);
    }

    public static void registerStorageListener(boolean persistent, String k, StorageListener listener) {
        Logger.info("[InternalStorage] register {} watchdog for [{}]", persistent ? "persistent" : "volatile", k);
        StorageWatchdog watchdog = new StorageWatchdog(k, listener, persistent);
        if (cache.containsKey(k)) {
            ping(listener, get(k));
            if (persistent) list.add(watchdog);
        } else {
            list.add(watchdog);
        }
    }

    public static void add(String k, String v) {
        cache.put(k, v);
        Logger.info("[InternalStorage] {}: {}", k, v);
        for (int i = list.size() - 1; i >= 0; i--) {
            StorageWatchdog watchdog = list.get(i);
            String o = watchdog.getK();
            if (o.equals(k)) {
                if (!watchdog.isPersistent()) list.remove(i);
                ping(watchdog.getListener(), get(o));
            }
        }
    }

    private static void ping(StorageListener listener, String k) {
        Logger.info("[InternalStorage] onItem({})", k);
        listener.onItem(k);
    }

    public static String get(String k) {
        Logger.info("[InternalStorage] fetch({})", k);
        return cache.get(k);
    }
}
