package com.dopkit.dispatch;

import java.util.Collections;
import java.util.Map;

/**
 * Result of matching a path pattern against an actual path.
 */
public final class PathMatchResult {
    private final String pattern;
    private final String path;
    private final Map<String, String> variables;

    public PathMatchResult(String pattern, String path, Map<String, String> variables) {
        this.pattern = pattern;
        this.path = path;
        this.variables = variables == null ? Collections.emptyMap() : Collections.unmodifiableMap(variables);
    }

    public String getPattern() {
        return pattern;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getVariables() {
        return variables;
    }
}
