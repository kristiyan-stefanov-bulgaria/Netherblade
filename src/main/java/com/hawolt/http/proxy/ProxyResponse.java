package com.hawolt.http.proxy;

import java.util.List;
import java.util.Map;

/**
 * Created: 30/07/2022 16:33
 * Author: Twitter @hawolt
 **/

public class ProxyResponse implements IRequest {
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

    public byte[] getByteBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    @Override
    public String getBody() {
        return new String(body);
    }

    @Override
    public void setBody(String in) {
        this.body = in.getBytes();
    }

    @Override
    public String url() {
        return original.url();
    }

    @Override
    public String method() {
        return original.method();
    }
}
