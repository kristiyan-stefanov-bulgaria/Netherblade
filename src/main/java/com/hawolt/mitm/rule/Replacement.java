package com.hawolt.mitm.rule;

/**
 * Created: 11/02/2023 16:51
 * Author: Twitter @hawolt
 **/

public class Replacement {
    private final String replacement;
    private final int start, end;

    public Replacement(int start, int end, String replacement) {
        this.replacement = replacement;
        this.start = start;
        this.end = end;
    }

    public String getReplacement() {
        return replacement;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
