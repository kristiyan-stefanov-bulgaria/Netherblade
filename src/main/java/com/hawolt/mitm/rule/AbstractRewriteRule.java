package com.hawolt.mitm.rule;

import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Created: 06/03/2023 15:26
 * Author: Twitter @hawolt
 **/

public abstract class AbstractRewriteRule<T, S> implements IRewrite<T, S> {

    protected final RuleType type;
    protected final Pattern target;
    protected final String method, url;

    public AbstractRewriteRule(JSONObject o) {
        this.url = o.getString("url");
        this.method = o.getString("method");
        this.target = Pattern.compile(this.url);
        this.type = RuleType.find(o.getString("type"));
    }

    @Override
    public Pattern getTarget() {
        return target;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public RuleType getType() {
        return type;
    }
}
