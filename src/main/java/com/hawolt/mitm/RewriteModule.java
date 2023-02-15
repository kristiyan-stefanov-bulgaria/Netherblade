package com.hawolt.mitm;

import com.hawolt.http.proxy.IRequest;
import com.hawolt.logger.Logger;
import com.hawolt.mitm.rule.RewriteRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created: 22/11/2022 03:46
 * Author: Twitter @hawolt
 **/

public abstract class RewriteModule<T extends IRequest> {
    protected Map<InstructionType, List<RewriteRule>> map = new HashMap<>();

    public void supply(Map<InstructionType, List<RewriteRule>> map) {
        for (InstructionType type : map.keySet()) {
            for (RewriteRule rule : map.get(type)) {
                Logger.debug("RewriteModule [{}]: {} -> {}", type.name(), rule.getPlain(), rule.getReplacement());
            }
        }
        this.map = map;
    }

    public T rewrite(T communication) {
        for (InstructionType type : map.keySet()) {
            List<RewriteRule> rules = map.get(type);
            for (RewriteRule rule : rules) {
                if (!rule.getTarget().matcher(communication.url()).matches()) continue;
                if (!rule.getMethod().equals(communication.method()) && !rule.getMethod().equals("*")) continue;
                Logger.debug("Matching url for rule [{}] {} ", communication.method(), communication.url());
                switch (type) {
                    case URL:
                        communication = rewriteURL(communication, rule);
                        break;
                    case QUERY:
                        communication = rewriteQuery(communication, rule);
                        break;
                    case HEADER:
                        communication = rewriteHeaders(communication, rule);
                        break;
                    case BODY:
                        communication = rewriteBody(communication, rule);
                        break;
                }
            }
        }
        return communication;
    }

    private T rewriteBody(T communication, RewriteRule rule) {
        String body = communication.getBody();
        body = rule.rewrite(body);
        communication.setBody(body);
        return communication;
    }

    protected abstract T rewriteHeaders(T communication, RewriteRule rule);

    protected abstract T rewriteQuery(T communication, RewriteRule rule);

    protected abstract T rewriteURL(T communication, RewriteRule rule);

}
