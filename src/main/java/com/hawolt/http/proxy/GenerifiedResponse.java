package com.hawolt.http.proxy;

import java.util.List;
import java.util.Map;

/**
 * Created: 21/11/2022 02:05
 * Author: Twitter @hawolt
 **/

public class GenerifiedResponse {
    private final Map<String, List<String>> headers;
    private final byte[] body, original;
    private final String url;
    private final int code;

    public GenerifiedResponse(String url, Map<String, List<String>> headers, int code, byte[] body, byte[] original) {
        this.original = original;
        this.headers = headers;
        this.body = body;
        this.code = code;
        this.url = url;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getUrl() {
        return url;
    }

    public int getCode() {
        return code;
    }

    public byte[] getOriginal() {
        return original;
    }
}
