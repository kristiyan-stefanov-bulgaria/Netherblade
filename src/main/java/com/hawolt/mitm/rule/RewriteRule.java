package com.hawolt.mitm.rule;

import com.hawolt.logger.Logger;
import org.json.JSONObject;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 22/11/2022 04:02
 * Author: Twitter @hawolt
 **/

public class RewriteRule implements IRewrite {
    private final String plain, replacement;
    private final Pattern target;
    private final RuleType type;
    private Pattern pattern;

    public RewriteRule(JSONObject object) {
        this.target = Pattern.compile(object.getString("url"));
        this.type = RuleType.find(object.getString("type"));
        this.replacement = object.getString("replace");
        this.plain = object.getString("find");
        if (type != RuleType.REGEX) return;
        this.pattern = Pattern.compile(plain);
    }

    public String getReplacement() {
        return replacement;
    }

    public Pattern getTarget() {
        return target;
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
                if (!matcher.find()) break;
                StringBuilder builder = new StringBuilder(in);
                for (int i = matcher.groupCount(); i >= 1; i--) {
                    String target = matcher.group(i);
                    int start = matcher.start(i);
                    builder.replace(start, start + target.length(), replacement);
                    Logger.debug("[{}-RULE] {}:{} -> {}", type.name(), plain, target, replacement);
                }
                in = builder.toString();
                break;
            case UNKNOWN:
                break;
        }
        return modified == null ? in : modified;
    }
}
