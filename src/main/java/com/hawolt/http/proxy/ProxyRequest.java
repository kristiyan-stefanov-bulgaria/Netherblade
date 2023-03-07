package com.hawolt.http.proxy;


import com.hawolt.io.Core;
import kotlin.Pair;
import okhttp3.*;
import okio.GzipSource;
import okio.Okio;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created: 30/07/2022 16:37
 * Author: Twitter @hawolt
 **/

public class ProxyRequest implements IRequest {

    private final String method, url;
    private Map<String, String> headers = new HashMap<>();
    private String body;


    public ProxyRequest(String destination, String method) {
        this.url = destination;
        this.method = method;
    }

    GenerifiedResponse execute() throws IOException {
        if (body != null && !body.isEmpty() && "GET".equalsIgnoreCase(method)) {
            //OkHttp refuses to send a GET with body due to RFC spec, hence this fallback
            HttpGet get = new HttpGet(url);
            for (String header : headers.keySet()) {
                get.addHeader(header, headers.get(header));
            }
            get.setEntity(new StringEntity(body));
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                try (CloseableHttpResponse response = client.execute(get)) {
                    byte[] body = Core.read(response.getEntity().getContent()).toByteArray();
                    int code = response.getCode();
                    Header[] headers = response.getHeaders();
                    Map<String, List<String>> map = new HashMap<>();
                    for (Header header : headers) {
                        if (!map.containsKey(header.getName())) map.put(header.getValue(), new ArrayList<>());
                        map.get(header.getName()).add(header.getValue());
                    }
                    return new GenerifiedResponse(url, map, code, body, body);
                }
            }
        } else {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request.Builder request = new Request.Builder().url(url);
            for (String header : headers.keySet()) {
                String value = headers.get(header);
                request.addHeader(header, value);
                if (header.equals("Content-Type")) {
                    MediaType mediaType = MediaType.parse(value);
                    if (body == null) throw new IOException("null body with content-type header");
                    RequestBody body = RequestBody.create(this.body, mediaType);
                    if ("POST".equals(method)) {
                        request.post(body);
                    } else if ("PUT".equals(method)) {
                        request.put(body);
                    }
                }
            }
            try (Response response = client.newCall(request.build()).execute()) {
                String encoding = response.headers().get("content-encoding");
                byte[] body = null, original = null;
                try (ResponseBody internal = response.body()) {
                    if (internal != null) {
                        body = (original = internal.source().readByteArray());
                        if ("gzip".equals(encoding)) {
                            GzipSource gzipSource = new GzipSource(Okio.source(new ByteArrayInputStream(original)));
                            body = Okio.buffer(gzipSource).readByteArray();
                        }
                    }
                }
                int code = response.code();
                Map<String, List<String>> map = new HashMap<>();
                for (Pair<? extends String, ? extends String> header : response.headers()) {
                    if (!map.containsKey(header.getFirst())) map.put(header.getFirst(), new ArrayList<>());
                    map.get(header.getFirst()).add(header.getSecond());
                }
                return new GenerifiedResponse(url, map, code, body, original);
            }
        }
    }

    @Override
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public void removeHeader(String key) {
        headers.remove(key);
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> map = new HashMap<>();
        for (String key : headers.keySet()) {
            map.put(key, Collections.singletonList(headers.get(key)));
        }
        return map;
    }

    public String getBody() {
        return body;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getOriginalHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }
}
