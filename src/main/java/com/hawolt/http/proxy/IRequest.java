package com.hawolt.http.proxy;

import java.util.List;
import java.util.Map;

/**
 * Created: 22/11/2022 04:05
 * Author: Twitter @hawolt
 **/

public interface IRequest {
    Map<String, List<String>> getHeaders();

    String getBody();

    void setBody(String in);

    String url();
}
