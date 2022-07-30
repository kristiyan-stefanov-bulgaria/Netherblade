package com.hawolt.proxy;

import com.hawolt.http.Request;
import com.hawolt.http.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created: 30/07/2022 16:33
 * Author: Twitter @hawolt
 **/

public class ProxyResponse {

    private final Map<String, List<String>> map;
    private final Request request;
    private byte[] body;
    private int code;

    public ProxyResponse(Response response) {
        this.request = response.getOriginRequest();
        this.map = response.getHeaders();
        this.code = response.getCode();
        this.body = response.getBody();
    }

    public void removeHeader(String name) {
        map.remove(name);
    }

    public void addHeader(String name, String value) {
        if (!map.containsKey(name)) map.put(name, new ArrayList<>());
        map.get(name).add(value);
    }

    public Request getRequest() {
        return request;
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
        return map;
    }
}
