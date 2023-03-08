package com.hawolt.yaml;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.hawolt.logger.Logger;
import com.hawolt.util.LocaleInstallation;
import com.hawolt.yaml.objects.YamlRegion;
import com.hawolt.yaml.objects.YamlServers;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created: 30/07/2022 17:52
 * Author: Twitter @hawolt
 **/

@SuppressWarnings("all")
public class SystemYaml {

    public static JSONObject config;

    static {
        try {
            SystemYaml.config = generate();
        } catch (IOException e) {
            Logger.error(e);
            System.err.println("Unable to generate internal json mapping, exiting (1).");
            System.exit(1);
        }
    }

    public static JSONObject generate() throws IOException {
        YamlReader reader = new YamlReader(new String(Files.readAllBytes(LocaleInstallation.SYSTEM_YAML.toPath())));
        SystemYaml yaml = reader.read(SystemYaml.class);
        JSONObject object = new JSONObject();
        for (Object key : yaml.region_data.keySet()) {
            YamlRegion yamlRegion = new YamlRegion((String) key, yaml.region_data.get(key));
            YamlServers yamlServers = yamlRegion.getServers();
            JSONObject region = new JSONObject();
            region.put("config", yamlServers.getConfig());
            region.put("email", yamlServers.getEmail());
            region.put("entitlement", yamlServers.getEntitlement());
            region.put("queue", yamlServers.getQueue());
            region.put("ledge", yamlServers.getLedge());
            region.put("platform", yamlServers.getPlatform());
            region.put("geo", "https://riot-geo.pas.si.riotgames.com");
            object.put(yamlRegion.getName(), region);
        }
        return object;
    }

    public String default_region, player_bug_report_url;
    public Map app, build, patcher, player_support_url, region_data, riotclient;
    public ArrayList<LinkedHashMap> products;

}
