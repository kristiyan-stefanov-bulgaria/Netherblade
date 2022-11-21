package com.hawolt.http;

import com.hawolt.http.proxy.BasicProxyServer;
import com.hawolt.http.proxy.ProxyRequest;
import com.hawolt.http.proxy.ProxyResponse;
import com.hawolt.logger.Logger;
import com.hawolt.socket.SocketServer;
import com.hawolt.util.LocaleInstallation;
import com.hawolt.util.StaticConstants;
import com.hawolt.yaml.SystemYaml;
import io.javalin.http.Handler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;


/**
 * Created: 31/07/2022 00:57
 * Author: Twitter @hawolt
 **/

public class LocalExecutor {

    public static void configure() {
        path("/v1", () -> {
            path("/client", () -> {
                get("/available", LocalExecutor.AVAILABLE);
                get("/launch/{region}", LocalExecutor.LAUNCH);
            });
        });
    }

    private static final Handler AVAILABLE = context -> {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (String region : SystemYaml.config.keySet()) {
            array.put(region);
        }
        object.put("regions", array);
        context.result(object.toString());
    };


    private final static String[] SUPPORTED = new String[]{"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};

    private static final Handler LAUNCH = context -> {
        String client = LocaleInstallation.RIOT_CLIENT_SERVICES.toString();
        try {
            Runtime.getRuntime().exec(
                    String.join(" ",
                            client, "--client-config-url=\"http://127.0.0.1:" + StaticConstants.PORT_MAPPING.get("config") + "\"",
                            "--launch-product=league_of_legends",
                            "--launch-patchline=live",
                            "--allow-multiple-clients"
                    )
            );
        } catch (IOException e) {
            Logger.error(e);
        }
        Map<String, BasicProxyServer> map = new HashMap<>();
        JSONObject system = SystemYaml.config.getJSONObject("EUW");
        map.put("config", new BasicProxyServer(StaticConstants.PORT_MAPPING.get("config"), system.getString("config")));
        for (String type : system.keySet()) {
            //   if (!options.getBoolean("active")) continue;
            if (type.equalsIgnoreCase("config")) continue;
            map.put(type, new BasicProxyServer(StaticConstants.PORT_MAPPING.get(type), system.getString(type)));
            Logger.debug("Proxy {}:{} on {}:{}", type.toUpperCase(), system.getString(type), "http://127.0.0.1", StaticConstants.PORT_MAPPING.get(type));
        }
        map.get("config").register(new IRequestModifier() {
            @Override
            public ProxyRequest onBeforeRequest(ProxyRequest request) {
                return request;
            }

            @Override
            public ProxyResponse onResponse(ProxyResponse response) {
                String plain = new String(response.getBody());
                for (String type : system.keySet()) {
                    //JSONObject options = types.getJSONObject(type);
                    //if (!options.getBoolean("active")) continue;
                    String target = system.getString(type);
                    String replacement = String.format("http://127.0.0.1:%d", StaticConstants.PORT_MAPPING.get(type));
                    plain = plain.replaceAll(target, replacement);
                    Logger.debug("Rewriting {} to {} in request {}", target, replacement, response.getOriginal().getUrl());
                }
                response.setBody(plain.getBytes(StandardCharsets.UTF_8));
                return response;
            }

            @Override
            public void onException(Exception e) {
                Logger.error(e);
            }
        }, "GET");

       /* map.get("auth").register(new IRequestModifier() {
            private String cookie;

            @Override
            public ProxyRequest onBeforeRequest(ProxyRequest request) {
                if (cookie != null) request.addHeader("Cookie", cookie);
                return request;
            }

            @Override
            public ProxyResponse onResponse(ProxyResponse response) {
                for (Map.Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("Set-Cookie")) {
                        List<String> list = entry.getValue();
                        StringBuilder builder = new StringBuilder();
                        for (String value : list) {
                            builder.append(value.split(";")[0]).append("; ");
                        }
                        cookie = builder.toString();
                    }
                }
                return response;
            }

            @Override
            public void onException(Exception e) {

            }
        }, "POST", "PUT");*/

       /* BasicProxyServer tmp = new BasicProxyServer(2269, "https://auth.riotgames.com");
        tmp.register(new IRequestModifier() {
            private String __cf_bm;

            @Override
            public ProxyRequest onBeforeRequest(ProxyRequest request) {
                if (request.getMethod() == Method.POST && request.getUrl().equals("https://auth.riotgames.com/api/v1/authorization"))
                    request.setBody("{\"acr_values\":\"urn:riot:bronze\",\"claims\":\"\",\"client_id\":\"lol\",\"code_challenge\":\"\",\"code_challenge_method\":\"\",\"nonce\":\"46GdUkLGveInsOR7Exx2aA\",\"redirect_uri\":\"http://localhost/redirect\",\"response_type\":\"token id_token\",\"scope\":\"openid link ban lol_region account\"}");
                if (__cf_bm != null)
                    request.addHeader("Cookie", __cf_bm);
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

                    if ("Set-Cookie".equalsIgnoreCase(header)) {
                        System.out.println("COOKIE?");
                        if (request.getEndpoint().equals("https://auth.riotgames.com/api/v1/authorization"))
                            __cf_bm = response.getHeaders().get(header).get(0);
                        Logger.error("__cf_bm={}", __cf_bm);
                    }
                    builder.append(header).append(": ").append(response.getHeaders().get(header)).append(System.lineSeparator());
                }
                builder.append(new String(response.getBody())).append(System.lineSeparator());
                System.out.println(builder);
                return response;
            }

            @Override
            public void onException(Exception e) {

            }
        }, SUPPORTED);*/

        for (String key : map.keySet()) {
            BasicProxyServer server = map.get(key);
            server.register(new IRequestModifier() {
                @Override
                public ProxyRequest onBeforeRequest(ProxyRequest request) {
                    return request;
                }

                @Override
                public ProxyResponse onResponse(ProxyResponse response) {
                    ProxyRequest request = response.getOriginal();
                    JSONObject object = new JSONObject();
                    JSONObject sent = new JSONObject();
                    sent.put("method", request.getMethod());
                    String[] data = request.getUrl().split("\\?");
                    JSONArray query = new JSONArray();
                    if (data.length > 1) {
                        String[] params = data[1].split("&");
                        for (String pair : params) {
                            String[] values = pair.split("=");
                            JSONObject parameter = new JSONObject();
                            parameter.put("k", values[0]);
                            parameter.put("v", values.length > 1 ? values[1] : JSONObject.NULL);
                            query.put(parameter);
                        }
                    }
                    sent.put("uri", data[0]);
                    sent.put("query", query);
                    JSONArray headers1 = new JSONArray();
                    for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                        JSONObject header = new JSONObject();
                        header.put("k", entry.getKey());
                        header.put("v", entry.getValue());
                        headers1.put(header);
                    }

                    sent.put("headers", headers1);
                    sent.put("body", response.getOriginal().getBody());
                    object.put("request", sent);
                    JSONObject received = new JSONObject();
                    received.put("code", response.getCode());

                    JSONArray headers2 = new JSONArray();
                    for (Map.Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
                        List<String> list = entry.getValue();
                        for (String value : list) {
                            JSONObject header = new JSONObject();
                            header.put("k", entry.getKey());
                            header.put("v", value);
                            headers2.put(header);
                        }
                    }
                    received.put("headers", headers2);
                    received.put("body", response.getBody() != null ? new String(response.getBody()) : JSONObject.NULL);
                    object.put("received", received);

                    //System.out.println(object);
                    SocketServer.forward(object.toString());
                    return response;
                }

                @Override
                public void onException(Exception e) {

                }
            }, SUPPORTED);
        }
    };
}
/*

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
                    System.out.println(builder);
 */