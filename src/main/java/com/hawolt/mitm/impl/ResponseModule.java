package com.hawolt.mitm.impl;

import com.hawolt.http.proxy.ProxyResponse;
import com.hawolt.mitm.RewriteModule;
import com.hawolt.mitm.rule.AbstractRewriteRule;
import com.hawolt.mitm.rule.impl.CodeRewriteRule;

/**
 * Created: 22/11/2022 04:01
 * Author: Twitter @hawolt
 **/

public class ResponseModule extends RewriteModule<ProxyResponse> {

    @Override
    protected ProxyResponse rewriteQuery(ProxyResponse communication, AbstractRewriteRule<?, ?> rule) {
        return communication;
    }

    @Override
    protected ProxyResponse rewriteURL(ProxyResponse communication, AbstractRewriteRule<?, ?> rule) {
        return communication;
    }

    @Override
    protected ProxyResponse rewriteCode(ProxyResponse communication, CodeRewriteRule rule) {
        communication.setCode(rule.rewrite(communication.getCode()));
        return communication;
    }
}
