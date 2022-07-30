package com.hawolt.yaml.objects;

import java.util.Map;

/**
 * Created: 30/07/2022 18:10
 * Author: Twitter @hawolt
 **/

public class YamlRegion {
    private final String name;
    private final YamlServers servers;

    public YamlRegion(String key, Object o) {
        this.name = key;
        this.servers = new YamlServers(((Map<?, ?>) o).get("servers"));
    }

    public String getName() {
        return name;
    }

    public YamlServers getServers() {
        return servers;
    }
}
