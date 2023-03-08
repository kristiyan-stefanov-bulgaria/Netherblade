package com.hawolt.mitm.cache;

/**
 * Created: 07/03/2023 20:25
 * Author: Twitter @hawolt
 **/

public class StorageWatchdog {
    private final StorageListener listener;
    private final boolean persistent;
    private final String k;

    public StorageWatchdog(String k, StorageListener listener, boolean persistent) {
        this.persistent = persistent;
        this.listener = listener;
        this.k = k;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public StorageListener getListener() {
        return listener;
    }

    public String getK() {
        return k;
    }
}
