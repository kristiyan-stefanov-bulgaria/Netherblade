package com.hawolt.mitm.rule.impl;

import com.hawolt.http.proxy.BasicProxyServer;
import com.hawolt.logger.Logger;
import com.hawolt.mitm.cache.InternalStorage;
import com.hawolt.mitm.rule.AbstractRewriteRule;
import com.hawolt.mitm.rule.Replacement;
import com.hawolt.mitm.rule.ReplacementGroup;
import com.hawolt.mitm.rule.RuleType;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created: 22/11/2022 04:02
 * Author: Twitter @hawolt
 **/

public class BodyRewriteRule extends AbstractRewriteRule<String, String> {
    private String replacement, plain, name;
    private List<ReplacementGroup> groups;
    private Pattern pattern;
    private int key, value;

    public BodyRewriteRule(JSONObject object) {
        super(object);
        if (type != RuleType.PLAINTEXT_CACHE) {
            this.plain = object.getString("find");
            if (type == RuleType.PLAIN) {
                this.replacement = object.getString("replace");
            } else {
                this.pattern = Pattern.compile(plain);
                if (type == RuleType.REGEX_CACHE) {
                    this.key = object.getInt("key");
                    this.value = object.getInt("value");
                } else if (getType() == RuleType.REGEX) {
                    this.groups = object.getJSONArray("groups")
                            .toList()
                            .stream()
                            .map(o -> (HashMap<?, ?>) o)
                            .map(JSONObject::new)
                            .map(ReplacementGroup::new)
                            .collect(Collectors.toList());
                }
            }
        } else {
            this.name = object.getString("name");
        }
    }

    public String getReplacement() {
        return replacement == null ? "SEE_GROUPS" : replacement;
    }

    public String getMethod() {
        return method;
    }

    public String getPlain() {
        return plain;
    }

    public List<ReplacementGroup> getGroups() {
        return groups;
    }

    @Override
    public String rewrite(String in) {
        Matcher matcher;
        StringBuilder builder;
        List<Replacement> list;
        String modified = null;
        switch (type) {
            case PLAIN:
                if (!in.contains(plain)) break;
                modified = in.replaceAll(plain, replacement);
                Logger.debug("[{}-RULE] {} -> {}", type.name(), plain, getReplacement());
                break;
            case REGEX:
                matcher = pattern.matcher(in);
                builder = new StringBuilder(in);
                list = new ArrayList<>();
                while (matcher.find()) {
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        for (ReplacementGroup group : getGroups()) {
                            if (!group.getGroups().contains(i)) continue;
                            String target = matcher.group(i);
                            if (target == null) continue;
                            int start = matcher.start(i);
                            list.add(new Replacement(start, start + target.length(), group.getReplacement()));
                        }
                    }
                }
                for (int i = list.size() - 1; i >= 0; i--) {
                    Replacement rule = list.get(i);
                    String target = builder.substring(rule.getStart(), rule.getEnd());
                    builder.replace(rule.getStart(), rule.getEnd(), rule.getReplacement());
                    Logger.info("[REWRITE] {} -> {}", target, rule.getReplacement());
                }
                in = builder.toString();
                Logger.debug("[{}-RULE] {}:{} -> {}", type.name(), plain, target, getReplacement());
                break;
            case PLAINTEXT_CACHE:
                InternalStorage.add(name, in);
                break;
            case REGEX_CACHE:
                matcher = pattern.matcher(in);
                while (matcher.find()) {
                    int count = matcher.groupCount();
                    if (count >= Math.max(key, value)) {
                        String k = matcher.group(key);
                        String v = matcher.group(value);
                        InternalStorage.add(k, v);
                    }
                }
                break;
            case UNKNOWN:
                break;
        }
        return modified == null ? in : modified;
    }
}
