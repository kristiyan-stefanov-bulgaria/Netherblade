package com.hawolt.mitm.impl;

import com.hawolt.http.proxy.ProxyResponse;
import com.hawolt.mitm.CommunicationType;
import com.hawolt.mitm.RewriteModule;

/**
 * Created: 22/11/2022 04:01
 * Author: Twitter @hawolt
 **/

public class ResponseModule extends RewriteModule<ProxyResponse> {
    public ResponseModule(CommunicationType type) {
        super(type);
    }
}
