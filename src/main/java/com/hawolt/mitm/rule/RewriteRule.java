package com.hawolt.mitm.rule;

import com.hawolt.logger.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 22/11/2022 04:02
 * Author: Twitter @hawolt
 **/

public class RewriteRule implements IRewrite {
    private final String plain, replacement;
    private final Pattern target;
    private final String method;
    private final RuleType type;
    private Pattern pattern;

    public RewriteRule(JSONObject object) {
        this.target = Pattern.compile(object.getString("url"));
        this.type = RuleType.find(object.getString("type"));
        this.replacement = object.getString("replace");
        this.plain = object.getString("find");
        this.method = object.getString("method");
        if (type != RuleType.REGEX) return;
        this.pattern = Pattern.compile(plain);
    }

    public String getReplacement() {
        return replacement;
    }

    public Pattern getTarget() {
        return target;
    }

    public String getMethod() {
        return method;
    }

    public String getPlain() {
        return plain;
    }

    @Override
    public String rewrite(String in) {
        String modified = null;
        switch (type) {
            case PLAIN:
                if (!in.contains(plain)) break;
                modified = in.replaceAll(plain, replacement);
                Logger.debug("[{}-RULE] {} -> {}", type.name(), plain, replacement);
                break;
            case REGEX:
                Matcher matcher = pattern.matcher(in);
                StringBuilder builder = new StringBuilder(in);
                List<Replacement> list = new ArrayList<>();
                while (matcher.find()) {
                    for (int i = matcher.groupCount(); i >= 1; i--) {
                        String target = matcher.group(i);
                        if (target == null) continue;
                        int start = matcher.start(i);
                        list.add(new Replacement(start, start + target.length(), replacement));
                    }
                }
                for (int i = list.size() - 1; i >= 0; i--) {
                    Replacement rule = list.get(i);
                    builder.replace(rule.getStart(), rule.getEnd(), rule.getReplacement());
                }
                in = builder.toString();
                Logger.debug("[{}-RULE] {}:{} -> {}", type.name(), plain, target, replacement);
                break;
            case UNKNOWN:
                break;
        }
        return modified == null ? in : modified;
    }
}
