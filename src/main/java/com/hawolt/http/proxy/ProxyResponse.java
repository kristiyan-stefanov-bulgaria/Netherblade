package com.hawolt.http.proxy;

import java.util.List;
import java.util.Map;

/**
 * Created: 30/07/2022 16:33
 * Author: Twitter @hawolt
 **/

public class ProxyResponse {
    private final Map<String, List<String>> headers;
    private final ProxyRequest original;
    private byte[] body;
    private int code;

    public ProxyResponse(ProxyRequest request, GenerifiedResponse response) {
        this.headers = response.getHeaders();
        this.code = response.getCode();
        this.body = response.getBody();
        this.original = request;
    }

    public ProxyRequest getOriginal() {
        return original;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
