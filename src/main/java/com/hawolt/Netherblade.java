package com.hawolt;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.hawolt.cli.Argument;
import com.hawolt.cli.CLI;
import com.hawolt.cli.Parser;
import com.hawolt.cli.ParserException;
import com.hawolt.http.IRequestModifier;
import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.logger.Logger;
import com.hawolt.proxy.BasicProxyServer;
import com.hawolt.proxy.ProxyRequest;
import com.hawolt.proxy.ProxyResponse;
import com.hawolt.yaml.SystemYaml;
import com.hawolt.yaml.objects.YamlRegion;
import com.hawolt.yaml.objects.YamlServers;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created: 30/07/2022 15:37
 * Author: Twitter @hawolt
 **/

public class Netherblade {

    private final static Map<String, BasicProxyServer> map = new HashMap<>();

    public static void main(String[] args) {
        try {
            patchPermittedHttpMethods("PATCH", "CONNECT");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logger.error(e);
            System.err.println("Unable to modify permitted HTTP methods, exiting (1).");
            System.exit(1);
        }
        Parser parser = new Parser();
        parser.add(Argument.create("y", "yaml", "specify path to system.yaml", false, true, false));
        parser.add(Argument.create("i", "init", "generate a base netherblade-config.json", false, true, true));
        try {
            CLI cli = parser.check(args);
            if (cli.has("yaml")) {
                YamlReader reader = new YamlReader(new String(Files.readAllBytes(Paths.get(cli.getValue("yaml")))));
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
                    object.put(yamlRegion.getName(), region);
                }
                try (FileWriter writer = new FileWriter("system.json")) {
                    writer.write(object.toString(2));
                }
                Logger.debug("Completed mapping of system.yaml");
            } else if (cli.has("init")) {
                JSONObject main = new JSONObject();
                main.put("region", "define_region_here");
                JSONObject config = new JSONObject();
                config.put("config", new JSONObject().put("port", 35200));
                config.put("email", new JSONObject().put("active", true).put("port", 35201));
                config.put("entitlement", new JSONObject().put("active", true).put("port", 35202));
                config.put("queue", new JSONObject().put("active", true).put("port", 35203));
                config.put("ledge", new JSONObject().put("active", true).put("port", 35204));
                config.put("platform", new JSONObject().put("active", true).put("port", 35205));
                main.put("config", config);
                try (FileWriter writer = new FileWriter("netherblade-config.json")) {
                    writer.write(main.toString(2));
                }
                Logger.debug("Created netherblade-config.json");
            } else {
                if (!Paths.get("netherblade-config.json").toFile().exists()) {
                    System.err.println("Please run using --init to generate netherblade-config.json, exiting (2)");
                    System.exit(2);
                }
                if (!Paths.get("system.json").toFile().exists()) {
                    System.err.println("Please run using --yaml to generate a base system.json, exiting (3)");
                    System.exit(3);
                }
                JSONObject config = new JSONObject(new String(Files.readAllBytes(Paths.get("netherblade-config.json"))));
                JSONObject system = new JSONObject(new String(Files.readAllBytes(Paths.get("system.json")))).getJSONObject(config.getString("region"));
                JSONObject types = config.getJSONObject("config");
                int port = types.getJSONObject("config").getInt("port");
                map.put("config", new BasicProxyServer(port, system.getString("config")));
                for (String type : types.keySet()) {
                    JSONObject options = types.getJSONObject(type);
                    if (!options.getBoolean("active")) continue;
                    if (type.equalsIgnoreCase("config")) continue;
                    map.put(type, new BasicProxyServer(options.getInt("port"), system.getString(type)));
                }
                map.get("config").register(new IRequestModifier() {
                    @Override
                    public ProxyRequest onBeforeRequest(ProxyRequest request) {
                        return request;
                    }

                    @Override
                    public ProxyResponse onResponse(ProxyResponse response) {
                        String plain = new String(response.getBody());
                        for (String type : types.keySet()) {
                            JSONObject options = types.getJSONObject(type);
                            if (!options.getBoolean("active")) continue;
                            String target = system.getString(type);
                            String replacement = String.format("http://localhost:%d", options.getInt("port"));
                            plain = plain.replaceAll(target, replacement);
                            Logger.debug("Rewriting {} to {} in request {}", target, replacement, response.getRequest().getEndpoint());
                        }
                        response.setBody(plain.getBytes(StandardCharsets.UTF_8));
                        return response;
                    }

                    @Override
                    public void onException(Exception e) {
                        Logger.error(e);
                    }
                }, Method.GET);
                String client = Paths.get("C:\\Riot Games").resolve("Riot Client").resolve("RiotClientServices.exe").toString();
                try {
                    Runtime.getRuntime().exec(String.join(" ", client, "--client-config-url=\"http://127.0.0.1:" + port + "\"", "--launch-product=league_of_legends", "--launch-patchline=live", "--allow-multiple-clients"));
                } catch (IOException e) {
                    Logger.error(e);
                }
            }
        } catch (ParserException e) {
            Logger.error(e);
            System.err.println(parser.getHelp());
        } catch (IOException e) {
            Logger.error(e);
        }

        for (String key : map.keySet()) {
            BasicProxyServer server = map.get(key);
            server.register(new IRequestModifier() {
                @Override
                public ProxyRequest onBeforeRequest(ProxyRequest request) {
                    return request;
                }

                @Override
                public ProxyResponse onResponse(ProxyResponse response) {
                    Request request = response.getRequest();
                    StringBuilder builder = new StringBuilder();
                    builder.append(request.getMethod().name()).append(" ").append(request.getEndpoint()).append(System.lineSeparator());
                    for (String header : request.getHeaders().keySet()) {
                        builder.append(header).append(": ").append(request.getHeaders().get(header)).append(System.lineSeparator());
                    }
                    if (request.getBody() != null) {
                        builder.append(request.getBody().toString()).append(System.lineSeparator());
                    }
                    builder.append("Response: ").append(response.getCode()).append(System.lineSeparator());
                    for (String header : response.getHeaders().keySet()) {
                        builder.append(header).append(": ").append(response.getHeaders().get(header)).append(System.lineSeparator());
                    }
                    builder.append(new String(response.getBody())).append(System.lineSeparator());
                    return response;
                }

                @Override
                public void onException(Exception e) {

                }
            }, Method.values());
        }
    }

    private static void patchPermittedHttpMethods(String... methods) throws NoSuchFieldException, IllegalAccessException {
        Field declaredFieldMethods = HttpURLConnection.class.getDeclaredField("methods");
        Field declaredFieldModifiers = Field.class.getDeclaredField("modifiers");
        declaredFieldModifiers.setAccessible(true);
        declaredFieldModifiers.setInt(declaredFieldMethods, declaredFieldMethods.getModifiers() & ~Modifier.FINAL);
        declaredFieldMethods.setAccessible(true);
        String[] previous = (String[]) declaredFieldMethods.get(null);
        Set<String> current = new LinkedHashSet<>(Arrays.asList(previous));
        current.addAll(Arrays.asList(methods));
        String[] patched = current.toArray(new String[0]);
        declaredFieldMethods.set(null, patched);
    }
}
