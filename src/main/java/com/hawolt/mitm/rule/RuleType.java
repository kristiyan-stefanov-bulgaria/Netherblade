package com.hawolt.mitm.rule;

/**
 * Created: 22/11/2022 04:16
 * Author: Twitter @hawolt
 **/

public enum RuleType {
    PLAIN, REGEX, UNKNOWN;

    private static final RuleType[] RULE_TYPES = RuleType.values();

    public static RuleType find(String in) {
        for (RuleType type : RULE_TYPES) {
            if (type.name().equalsIgnoreCase(in)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
