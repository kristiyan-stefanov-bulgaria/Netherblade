package com.hawolt.mitm.impl;

import com.hawolt.http.proxy.ProxyResponse;
import com.hawolt.mitm.CommunicationType;
import com.hawolt.mitm.RewriteModule;
import com.hawolt.mitm.rule.RewriteRule;

import java.util.List;

/**
 * Created: 22/11/2022 04:01
 * Author: Twitter @hawolt
 **/

public class ResponseModule extends RewriteModule<ProxyResponse> {


    @Override
    protected ProxyResponse rewriteHeaders(ProxyResponse communication, RewriteRule rule) {
        return null;
    }

    @Override
    protected ProxyResponse rewriteQuery(ProxyResponse communication, RewriteRule rule) {
        return null;
    }

    @Override
    protected ProxyResponse rewriteURL(ProxyResponse communication, RewriteRule rule) {
        return null;
    }
}
