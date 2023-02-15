package com.hawolt.mitm.rule;

import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created: 12/02/2023 18:52
 * Author: Twitter @hawolt
 **/

public class ReplacementGroup {
    private final String replace;
    private final List<Integer> groups;

    public ReplacementGroup(JSONObject o) {
        this.replace = o.getString("replace");
        this.groups = o.getJSONArray("id")
                .toList()
                .stream()
                .map(Object::toString)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public String getReplacement() {
        return replace;
    }

    public List<Integer> getGroups() {
        return groups;
    }
}
