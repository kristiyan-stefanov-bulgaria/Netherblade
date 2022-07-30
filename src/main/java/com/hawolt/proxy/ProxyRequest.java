package com.hawolt.proxy;

import com.hawolt.http.Method;
import com.hawolt.http.Request;
import com.hawolt.http.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created: 30/07/2022 16:37
 * Author: Twitter @hawolt
 **/

public class ProxyRequest {

    private final Map<String, String> headers = new HashMap<>();
    private String url, body;
    private boolean output;
    private Method method;


    public ProxyRequest(String destination, Method method, boolean output) {
        this.url = destination;
        this.method = method;
        this.output = output;
    }

    Response execute() throws IOException {
        Request request = new Request(url, method, output);
        for (String header : headers.keySet()) {
            request.addHeader(header, headers.get(header));
        }
        if (output && body != null) request.write(body);
        return request.execute();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
