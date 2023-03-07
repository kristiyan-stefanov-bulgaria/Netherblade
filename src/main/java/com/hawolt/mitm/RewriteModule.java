package com.hawolt.mitm;

import com.hawolt.http.proxy.IRequest;
import com.hawolt.logger.Logger;
import com.hawolt.mitm.rule.AbstractRewriteRule;
import com.hawolt.mitm.rule.IRewrite;
import com.hawolt.mitm.rule.RuleType;
import com.hawolt.mitm.rule.impl.BodyRewriteRule;
import com.hawolt.mitm.rule.impl.CodeRewriteRule;
import com.hawolt.mitm.rule.impl.HeaderRewriteRule;
import com.hawolt.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created: 22/11/2022 03:46
 * Author: Twitter @hawolt
 **/

public abstract class RewriteModule<T extends IRequest> {
    protected Map<InstructionType, List<IRewrite<?, ?>>> map = new HashMap<>();

    public void supply(Map<InstructionType, List<IRewrite<?, ?>>> map) {
        for (InstructionType type : map.keySet()) {
            for (IRewrite<?, ?> rule : map.get(type)) {
                Logger.debug("RewriteModule [{}]: {} {}", type.name(), rule.getMethod(), rule.getTarget());
            }
            Logger.debug("RewriteModule [{}]: {} rules loaded", type.name(), map.get(type).size());
        }
        this.map = map;
    }

    public void inject(InstructionType type, IRewrite<?, ?> rule) {
        Logger.debug("RewriteModule [{}]: {} {}", type.name(), rule.getMethod(), rule.getTarget());
        Logger.debug("RewriteModule [{}]: rule injected", type.name());
        map.get(type).add(rule);
    }

    public T rewrite(T communication) {
        for (InstructionType type : map.keySet()) {
            List<IRewrite<?, ?>> rules = map.get(type);
            for (IRewrite<?, ?> rule : rules) {
                if (!rule.getTarget().matcher(communication.url()).matches()) continue;
                if (!rule.getMethod().equals(communication.method()) && !rule.getMethod().equals("*")) continue;
                Logger.debug("Matching url for rule [{}] {} ", communication.method(), communication.url());
                switch (type) {
                    case CODE:
                        communication = rewriteCode(communication, Unsafe.cast(rule));
                        break;
                    case URL:
                        communication = rewriteURL(communication, Unsafe.cast(rule));
                        break;
                    case QUERY:
                        communication = rewriteQuery(communication, Unsafe.cast(rule));
                        break;
                    case HEADER:
                        communication = rewriteHeaders(communication, Unsafe.cast(rule));
                        break;
                    case BODY:
                        communication = rewriteBody(communication, Unsafe.cast(rule));
                        break;
                }
            }
        }
        return communication;
    }

    private T rewriteBody(T communication, BodyRewriteRule rule) {
        String body = communication.getBody();
        body = rule.rewrite(body);
        communication.setBody(body);
        return communication;
    }

    private T rewriteHeaders(T communication, HeaderRewriteRule rule) {
        if (rule.getType() == RuleType.CORS) {
            for (String key : new ArrayList<>(communication.getHeaders().keySet())) {
                if (key.startsWith("access-control")) {
                    String value = communication.getHeaders().get(key).get(0);
                    communication.addHeader(fix(key), value);
                    communication.removeHeader(key);
                }
            }
        } else {
            Pair<String, String> result = rule.rewrite(communication.getHeaders());
            if (result == null && rule.getType() == RuleType.REMOVE) communication.removeHeader(rule.getKey());
            else if (result != null) communication.addHeader(result.getKey(), result.getValue());
        }
        return communication;
    }

    private String fix(String key) {
        StringBuilder builder = new StringBuilder(key);
        builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
        for (int i = 1; i < key.length(); i++) {
            if (builder.charAt(i) == '-') {
                builder.setCharAt(i + 1, Character.toUpperCase(builder.charAt(i + 1)));
            }
        }
        return builder.toString();
    }

    protected abstract T rewriteQuery(T communication, AbstractRewriteRule<?, ?> rule);

    protected abstract T rewriteURL(T communication, AbstractRewriteRule<?, ?> rule);

    protected abstract T rewriteCode(T communication, CodeRewriteRule rule);

}
