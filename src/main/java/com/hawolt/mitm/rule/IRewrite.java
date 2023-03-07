package com.hawolt.mitm.rule;

import java.util.regex.Pattern;

/**
 * Created: 22/11/2022 04:24
 * Author: Twitter @hawolt
 **/

public interface IRewrite<T, S> {
    S rewrite(T in);

    Pattern getTarget();

    String getMethod();

    RuleType getType();
}
