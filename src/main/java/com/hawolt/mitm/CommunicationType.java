package com.hawolt.mitm;

import com.hawolt.mitm.rule.RuleType;

/**
 * Created: 22/11/2022 03:43
 * Author: Twitter @hawolt
 **/

public enum CommunicationType {
    INGOING, OUTGOING, UNKNOWN;

    private static final CommunicationType[] COMMUNICATION_TYPES = CommunicationType.values();

    public static CommunicationType find(String in) {
        for (CommunicationType type : COMMUNICATION_TYPES) {
            if (type.name().equalsIgnoreCase(in)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
