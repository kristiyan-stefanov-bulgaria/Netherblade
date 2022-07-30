package com.hawolt.http;

import com.hawolt.proxy.ProxyRequest;
import com.hawolt.proxy.ProxyResponse;

/**
 * Created: 30/07/2022 16:02
 * Author: Twitter @hawolt
 **/

public interface IRequestModifier {

    public ProxyRequest onBeforeRequest(ProxyRequest request);

    public ProxyResponse onResponse(ProxyResponse response);

    void onException(Exception e);
}
